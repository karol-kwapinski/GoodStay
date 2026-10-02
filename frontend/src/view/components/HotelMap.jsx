import {MapContainer, TileLayer, Marker, Popup, useMap} from "react-leaflet";
import {Link} from "react-router-dom";
import {useEffect} from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "./HotelMap.css";

// The pin is a 36px square rotated 45deg, so its tip sits half a diagonal (~26px) below the centre
const PIN_SIZE = 36;
const PIN_TIP_Y = Math.round(PIN_SIZE / 2 + (PIN_SIZE / 2) * Math.SQRT2);

const iconCache = new Map();

function hotelIcon(stars) {
    if (!iconCache.has(stars)) {
        iconCache.set(stars, L.divIcon({
            className: "hotel-marker",
            html: `<div class="hotel-pin hotel-pin--stars-${stars}">
                       <span class="hotel-pin__label">${stars}★</span>
                   </div>`,
            iconSize: [PIN_SIZE, PIN_SIZE],
            iconAnchor: [PIN_SIZE / 2, PIN_TIP_Y],
            popupAnchor: [0, -PIN_TIP_Y],
        }));
    }
    return iconCache.get(stars);
}

function FitToHotels({positions}) {
    const map = useMap();
    // Re-fit only when the set of hotels changes, not on every re-render
    const positionsKey = JSON.stringify(positions);

    useEffect(() => {
        const points = JSON.parse(positionsKey);
        if (points.length === 1) {
            map.setView(points[0], 16);
        } else if (points.length > 1) {
            map.fitBounds(points, {padding: [30, 30]});
        }
    }, [map, positionsKey]);

    return null;
}

export default function HotelMap({hotels, linkQuery = "", height = "400px"}) {

    const hotelsWithLocation = hotels.filter(
        (hotel) => hotel.latitude != null && hotel.longitude != null
    );

    if (hotelsWithLocation.length === 0) {
        return null;
    }

    const positions = hotelsWithLocation.map((hotel) => [hotel.latitude, hotel.longitude]);

    return (
        <MapContainer
            center={positions[0]}
            zoom={13}
            scrollWheelZoom={false}
            style={{height, width: "100%"}}
        >
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            <FitToHotels positions={positions} />

            {hotelsWithLocation.map((hotel) => (
                <Marker
                    key={hotel.id}
                    position={[hotel.latitude, hotel.longitude]}
                    icon={hotelIcon(hotel.stars)}
                    title={hotel.name}
                >
                    <Popup>
                        <div className="hotel-popup">
                            {hotels.length > 1 ? (
                                <Link to={`/hotel/${hotel.id}${linkQuery}`} className="hotel-popup__name">
                                    {hotel.name}
                                </Link>
                            ) : (
                                <span className="hotel-popup__name">{hotel.name}</span>
                            )}
                            <span className="hotel-popup__stars">{"★".repeat(hotel.stars)}</span>
                            <br />
                            {hotel.street} {hotel.buildingNumber}
                        </div>
                    </Popup>
                </Marker>
            ))}
        </MapContainer>
    );
}
