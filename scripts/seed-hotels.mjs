// Fetches real hotels for the cities in CITIES from OpenStreetMap and generates an idempotent SQL seed.
//
//  - hotels:     Overpass API (tourism=hotel)
//  - addresses:  Nominatim reverse geocoding for hotels without addr:* tags
//  - images:     Wikimedia Commons, via the OSM wikidata / wikimedia_commons / image tags
//  - rooms, prices, facilities, check-in hours, missing stars: generated (deterministic per hotel)
//
// Usage (from the GoodStay folder):
//   node scripts/seed-hotels.mjs [--refresh]      -> writes scripts/seed-hotels.sql
//   docker exec -i goodstay-postgres psql -U admin -d goodstay < scripts/seed-hotels.sql
//
// API responses are cached in scripts/.cache so re-runs don't hit the public APIs again
// (Nominatim allows max 1 request/second). Pass --refresh to download fresh hotel data.
//
// Hotel data © OpenStreetMap contributors, ODbL (https://www.openstreetmap.org/copyright).
// Images: Wikimedia Commons, licenses vary per file (see the file page on commons.wikimedia.org).

import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { dirname, join } from "node:path";

const CITIES = [
    { osmName: "Hel", dbName: "Hel" },
];

// OSM tourism=* values imported as hotels
const LODGING_TYPES = ["hotel", "guest_house", "apartment", "chalet", "hostel", "motel"];

const OVERPASS_URLS = [
    "https://overpass-api.de/api/interpreter",
    "https://overpass.private.coffee/api/interpreter",
    "https://maps.mail.ru/osm/tools/overpass/api/interpreter",
];
const NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse";
const WIKIDATA_URL = "https://www.wikidata.org/w/api.php";
const USER_AGENT = "GoodStay-seed/1.0 (https://github.com/karol-kwapinski/GoodStay)";

const ROOM_TYPES = [
    { name: "Single Room", maxGuests: 1, priceFactor: 0.8 },
    { name: "Double Room", maxGuests: 2, priceFactor: 1.0 },
    { name: "Family Suite", maxGuests: 3, priceFactor: 1.6 },
    { name: "Apartment", maxGuests: 4, priceFactor: 2.0 },
];

const FACILITIES = [
    "Wi-Fi", "Parking", "Restaurant", "Bar", "Fitness center",
    "Swimming pool", "Hot tub", "Spa", "Air conditioning", "Airport shuttle",
];

// Base price per night (PLN) for a double room, by star rating
const BASE_PRICE_BY_STARS = { 1: 150, 2: 200, 3: 280, 4: 420, 5: 750 };

const SCRIPT_DIR = dirname(fileURLToPath(import.meta.url));
const CACHE_DIR = join(SCRIPT_DIR, ".cache");
const REFRESH = process.argv.includes("--refresh");

// ---------- helpers ----------

const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));

// Deterministic pseudo-random generator seeded by OSM id, so re-runs give the same data
function rng(seed) {
    let s = seed % 2147483647 || 1;
    return () => (s = (s * 16807) % 2147483647) / 2147483647;
}

function sql(value) {
    if (value === null || value === undefined) return "NULL";
    if (typeof value === "number") return String(value);
    return `'${String(value).replace(/'/g, "''")}'`;
}

function readCache(name, fallback) {
    const file = join(CACHE_DIR, name);
    return existsSync(file) ? JSON.parse(readFileSync(file, "utf8")) : fallback;
}

function writeCache(name, data) {
    mkdirSync(CACHE_DIR, { recursive: true });
    writeFileSync(join(CACHE_DIR, name), JSON.stringify(data, null, 1), "utf8");
}

// ---------- Overpass: hotels ----------

function fetchHotels(osmCityName) {
    return overpassInCity(osmCityName, `nwr["tourism"~"^(${LODGING_TYPES.join("|")})$"]`, `overpass-${osmCityName}.json`);
}

// All objects with a house number in the city - used to give lodgings without an address the nearest one
function fetchAddressPoints(osmCityName) {
    return overpassInCity(osmCityName, `nwr["addr:housenumber"]`, `addresses-${osmCityName}.json`);
}

async function overpassInCity(osmCityName, selector, cacheName) {
    if (!REFRESH) {
        const cached = readCache(cacheName, null);
        if (cached) return cached;
    }

    const query = `
        [out:json][timeout:120];
        area["name"="${osmCityName}"]["boundary"="administrative"]->.city;
        ${selector}(area.city);
        out center tags;`;

    // Public Overpass servers are often overloaded (429/504), so retry across mirrors
    for (let attempt = 0; attempt < 6; attempt++) {
        const url = OVERPASS_URLS[attempt % OVERPASS_URLS.length];
        try {
            const response = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded", "User-Agent": USER_AGENT },
                body: "data=" + encodeURIComponent(query),
            });
            if (response.ok) {
                const elements = (await response.json()).elements;
                // Some mirrors return an empty result when their area index is stale
                if (elements.length) {
                    writeCache(cacheName, elements);
                    return elements;
                }
                console.warn(`  Overpass ${new URL(url).host}: empty result, retrying...`);
            } else {
                console.warn(`  Overpass ${new URL(url).host}: HTTP ${response.status}, retrying...`);
            }
        } catch (error) {
            console.warn(`  Overpass ${new URL(url).host}: ${error.message}, retrying...`);
        }
        await sleep(5000 * (attempt + 1));
    }
    throw new Error(`Could not fetch ${cacheName} from any Overpass server`);
}

// ---------- nearest OSM address point ----------

const MAX_ADDRESS_DISTANCE_M = 60;

function distanceMeters(lat1, lon1, lat2, lon2) {
    const rad = Math.PI / 180;
    const x = (lon2 - lon1) * rad * Math.cos(((lat1 + lat2) / 2) * rad);
    const y = (lat2 - lat1) * rad;
    return Math.sqrt(x * x + y * y) * 6371000;
}

function nearestAddress(addressPoints, lat, lon, street) {
    let best = null;
    for (const point of addressPoints) {
        const t = point.tags;
        const pointStreet = t["addr:street"] ?? t["addr:place"];
        if (!pointStreet || (street && pointStreet !== street)) continue;
        const d = distanceMeters(lat, lon, point.lat ?? point.center.lat, point.lon ?? point.center.lon);
        if (d <= MAX_ADDRESS_DISTANCE_M && (!best || d < best.d)) {
            best = { d, street: pointStreet, buildingNumber: t["addr:housenumber"] };
        }
    }
    return best;
}

// ---------- Nominatim: missing addresses ----------

const nominatimCache = readCache("nominatim.json", {});

async function reverseGeocode(lat, lon) {
    const key = `${lat.toFixed(6)},${lon.toFixed(6)}`;
    if (key in nominatimCache) return nominatimCache[key];

    await sleep(1100); // Nominatim usage policy: max 1 request per second
    const url = `${NOMINATIM_URL}?format=jsonv2&zoom=18&addressdetails=1&lat=${lat}&lon=${lon}`;
    const response = await fetch(url, { headers: { "User-Agent": USER_AGENT, "Accept-Language": "pl" } });
    if (!response.ok) {
        console.warn(`  Nominatim: HTTP ${response.status} for ${key}`);
        return null;
    }
    const address = (await response.json()).address ?? {};
    const result = {
        street: address.road ?? address.pedestrian ?? address.square ?? address.footway ?? null,
        buildingNumber: address.house_number ?? null,
    };
    nominatimCache[key] = result;
    writeCache("nominatim.json", nominatimCache);
    return result;
}

// ---------- Wikidata / Wikimedia Commons: images ----------

const wikidataCache = readCache("wikidata-images.json", {});

function commonsFileUrl(fileName) {
    const name = fileName.replace(/^File:/i, "").trim().replace(/ /g, "_");
    return `https://commons.wikimedia.org/wiki/Special:FilePath/${encodeURIComponent(name)}?width=1280`;
}

async function fetchWikidataImages(ids) {
    const missing = [...new Set(ids)].filter(id => !(id in wikidataCache));
    for (let i = 0; i < missing.length; i += 50) {
        const batch = missing.slice(i, i + 50);
        const url = `${WIKIDATA_URL}?action=wbgetentities&props=claims&format=json&ids=${batch.join("|")}`;
        const response = await fetch(url, { headers: { "User-Agent": USER_AGENT } });
        if (!response.ok) {
            console.warn(`  Wikidata: HTTP ${response.status}`);
            continue;
        }
        const entities = (await response.json()).entities ?? {};
        for (const id of batch) {
            // P18 = image
            const claims = entities[id]?.claims?.P18 ?? [];
            wikidataCache[id] = claims.map(c => c.mainsnak?.datavalue?.value).filter(Boolean);
        }
        writeCache("wikidata-images.json", wikidataCache);
    }
}

function imagesFor(tags) {
    const urls = [];
    for (const file of wikidataCache[tags.wikidata] ?? []) urls.push(commonsFileUrl(file));
    if (tags.wikimedia_commons?.startsWith("File:")) urls.push(commonsFileUrl(tags.wikimedia_commons));
    if (/^https?:\/\/.+\.(jpe?g|png|webp)$/i.test(tags.image ?? "")) urls.push(tags.image);
    return [...new Set(urls)].filter(url => url.length <= 255).slice(0, 5);
}

// ---------- mapping OSM -> GoodStay ----------

async function resolveAddress(element, addressPoints) {
    const t = element.tags ?? {};
    let street = (t["addr:street"] ?? t["addr:place"])?.trim();
    let buildingNumber = t["addr:housenumber"]?.trim();
    let source = "osm";

    const lat = element.lat ?? element.center?.lat;
    const lon = element.lon ?? element.center?.lon;
    if ((!street || !buildingNumber) && lat != null && lon != null) {
        // 1) nearest OSM address point (keeps the street if OSM already has it)
        const near = nearestAddress(addressPoints, lat, lon, street);
        if (near) {
            street = near.street;
            buildingNumber = near.buildingNumber;
            source = "nearby";
        } else {
            // 2) Nominatim reverse geocoding
            const geocoded = await reverseGeocode(lat, lon);
            street = street ?? geocoded?.street ?? undefined;
            buildingNumber = buildingNumber ?? geocoded?.buildingNumber ?? undefined;
            source = "nominatim";
        }
    }
    return { street, buildingNumber, lat: lat ?? null, lon: lon ?? null, source };
}

// Name for lodgings that have no name in OSM, e.g. "Pensjonat Wiejska 62"
const TYPE_LABELS = {
    hotel: "Hotel", guest_house: "Pensjonat", apartment: "Apartamenty",
    chalet: "Domki", hostel: "Hostel", motel: "Motel",
};

function toHotel(element, address, dbCityName) {
    const t = element.tags ?? {};
    const { street, buildingNumber, lat, lon } = address;
    const name = t.name?.trim() || (street && buildingNumber && `${TYPE_LABELS[t.tourism]} ${street} ${buildingNumber}`);

    // Skip entries that don't satisfy the DB constraints
    if (!name || !street || !buildingNumber) return null;
    if (name.length > 100 || street.length > 80 || buildingNumber.length > 6) return null;

    const random = rng(element.id);
    const parsedStars = parseInt(t.stars, 10);
    // Missing stars: 2-4 for hotels, 1-3 for guest houses, apartments etc.
    const defaultStars = t.tourism === "hotel" ? 2 + Math.floor(random() * 3) : 1 + Math.floor(random() * 3);
    const stars = parsedStars >= 1 && parsedStars <= 5 ? parsedStars : defaultStars;
    const brand = (t.brand ?? t.operator ?? name).slice(0, 30);

    const checkInFrom = ["14:00", "15:00", "13:00"][Math.floor(random() * 3)];
    const checkInUntil = ["22:00", "23:00", "00:00"][Math.floor(random() * 3)];
    const checkOutUntil = ["11:00", "12:00", "10:00"][Math.floor(random() * 3)];

    // 2-3 room types per hotel, 1-3 rooms of each type
    const basePrice = BASE_PRICE_BY_STARS[stars];
    const rooms = [];
    const typeCount = 2 + Math.floor(random() * 2);
    const startType = random() < 0.5 ? 0 : 1;
    for (const type of ROOM_TYPES.slice(startType, startType + typeCount)) {
        const roomsOfType = 1 + Math.floor(random() * 3);
        for (let i = 0; i < roomsOfType; i++) {
            const price = Math.round((basePrice * type.priceFactor * (0.85 + random() * 0.3)) / 5) * 5;
            rooms.push({ type: type.name, price });
        }
    }

    // Facilities: take what OSM knows, fill the rest by star rating
    const facilities = new Set();
    if (["wlan", "yes", "wifi"].includes(t.internet_access)) facilities.add("Wi-Fi");
    if (t.swimming_pool === "yes") facilities.add("Swimming pool");
    if (t.parking || t["parking:fee"]) facilities.add("Parking");
    if (t.restaurant === "yes") facilities.add("Restaurant");
    if (t.air_conditioning === "yes") facilities.add("Air conditioning");
    for (const facility of FACILITIES) {
        if (random() < 0.15 + stars * 0.1) facilities.add(facility);
    }

    return {
        name, cityName: dbCityName, street, buildingNumber, stars, brand, lat, lon,
        checkInFrom, checkInUntil, checkOutUntil, rooms, facilities: [...facilities], images: imagesFor(t),
    };
}

// ---------- SQL ----------

function toSql(hotels) {
    const lines = [
        "-- Generated by scripts/seed-hotels.mjs",
        "-- Hotel data © OpenStreetMap contributors, ODbL; images from Wikimedia Commons",
        "BEGIN;",
        "",
    ];

    for (const type of ROOM_TYPES) {
        lines.push(`INSERT INTO room_type (name, max_guests) SELECT ${sql(type.name)}, ${type.maxGuests} ` +
            `WHERE NOT EXISTS (SELECT 1 FROM room_type WHERE name = ${sql(type.name)});`);
    }
    for (const facility of FACILITIES) {
        lines.push(`INSERT INTO facility (name) SELECT ${sql(facility)} ` +
            `WHERE NOT EXISTS (SELECT 1 FROM facility WHERE name = ${sql(facility)});`);
    }
    lines.push("");

    hotels.forEach((h, index) => {
        // Hotels are assigned to existing HOTEL_OWNER accounts in round-robin
        const owner = `(SELECT id FROM "user" WHERE role = 'HOTEL_OWNER' ORDER BY id ` +
            `OFFSET ${index} % GREATEST((SELECT count(*) FROM "user" WHERE role = 'HOTEL_OWNER'), 1) LIMIT 1)`;
        const roomValues = h.rooms.map(r => `(${sql(r.type)}, ${r.price})`).join(", ");
        const facilityValues = h.facilities.map(f => `(${sql(f)})`).join(", ");

        lines.push(`WITH h AS (
    INSERT INTO hotel (name, city_name, street, building_number, stars, brand, latitude, longitude,
                       check_in_from, check_in_until, check_out_until, number_of_ratings, owner_id)
    VALUES (${sql(h.name)}, ${sql(h.cityName)}, ${sql(h.street)}, ${sql(h.buildingNumber)}, ${h.stars}, ${sql(h.brand)},
            ${sql(h.lat)}, ${sql(h.lon)}, ${sql(h.checkInFrom)}, ${sql(h.checkInUntil)}, ${sql(h.checkOutUntil)}, 0, ${owner})
    ON CONFLICT (city_name, street, building_number) DO NOTHING
    RETURNING id
), r AS (
    INSERT INTO room (hotel_id, room_type, price_per_night)
    SELECT h.id, rt.id, v.price FROM h, (VALUES ${roomValues}) AS v(type, price)
    JOIN room_type rt ON rt.name = v.type
)
INSERT INTO hotel_facility (hotel_id, facility_id)
SELECT h.id, f.id FROM h, (VALUES ${facilityValues || "(NULL)"}) AS v(name)
JOIN facility f ON f.name = v.name;
`);

        // Images are added separately (keyed by address), so they also reach hotels seeded earlier
        h.images.forEach((url, position) => {
            lines.push(`INSERT INTO hotel_image (hotel_id, path, position)
SELECT id, ${sql(url)}, ${position} FROM hotel
WHERE city_name = ${sql(h.cityName)} AND street = ${sql(h.street)} AND building_number = ${sql(h.buildingNumber)}
  AND NOT EXISTS (SELECT 1 FROM hotel_image i WHERE i.hotel_id = hotel.id AND i.path = ${sql(url)});
`);
        });
    });

    lines.push("COMMIT;");
    return lines.join("\n");
}

// ---------- main ----------

const allHotels = [];
for (const city of CITIES) {
    console.log(`${city.osmName}:`);
    const elements = await fetchHotels(city.osmName);
    const addressPoints = await fetchAddressPoints(city.osmName);

    await fetchWikidataImages(elements.map(e => e.tags?.wikidata).filter(id => /^Q\d+$/.test(id ?? "")));

    // Only one lodging per address fits the DB unique constraint - prefer named ones with an OSM address
    const hasOsmAddress = e => (e.tags["addr:street"] ?? e.tags["addr:place"]) && e.tags["addr:housenumber"];
    const priority = e => (e.tags.name ? 0 : 2) + (hasOsmAddress(e) ? 0 : 1);
    const sorted = [...elements].sort((a, b) => priority(a) - priority(b));

    const seen = new Set();
    const sources = { osm: 0, nearby: 0, nominatim: 0 };
    let generatedNames = 0;
    const hotels = [];
    for (const element of sorted) {
        const address = await resolveAddress(element, addressPoints);
        const hotel = toHotel(element, address, city.dbName);
        const key = hotel && `${hotel.street}|${hotel.buildingNumber}`;
        if (!hotel || seen.has(key)) continue;
        seen.add(key);
        sources[address.source]++;
        if (!element.tags.name) generatedNames++;
        hotels.push(hotel);
    }

    const withImages = hotels.filter(h => h.images.length).length;
    console.log(`  ${elements.length} lodgings in OSM, ${hotels.length} usable, ${withImages} with images`);
    console.log(`  addresses: ${sources.osm} from OSM tags, ${sources.nearby} from nearest address point, ` +
        `${sources.nominatim} from Nominatim; ${generatedNames} without name in OSM got a generated name`);
    allHotels.push(...hotels);
}

const outFile = join(SCRIPT_DIR, "seed-hotels.sql");
writeFileSync(outFile, toSql(allHotels), "utf8");
console.log(`Wrote ${allHotels.length} hotels to ${outFile}`);
