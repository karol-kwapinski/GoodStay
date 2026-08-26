import {useHotelViewModel} from "../viewmodel/useHotelViewModel.js";
import {Link} from "react-router-dom";
import Header from "./components/Header.jsx";

export default function HotelListingPage() {

    const vm = useHotelViewModel();

    return (
        <>
            <Header />

            <form onSubmit={vm.handleSubmit}>
                <input
                    name="cityName"
                    required={true}
                    value={vm.form.cityName}
                    onChange={vm.handleChange}
                    placeholder="City"
                />

                <input
                    name="checkInDate"
                    required={true}
                    type="date"
                    value={vm.form.checkInDate}
                    onChange={vm.handleChange}
                    placeholder="check in date"
                    min={vm.minDate}
                />

                <input
                    name="checkOutDate"
                    required={true}
                    type="date"
                    value={vm.form.checkOutDate}
                    onChange={vm.handleChange}
                    placeholder="check in date"
                    min={vm.getNextDay(vm.form.checkInDate)}
                    disabled={!vm.form.checkInDate}
                />

                <button type="submit" disabled={vm.loading}>
                    Find
                </button>
            </form>

            {vm.error && (
                <div>
                    {vm.error}
                </div>
            )}

            {vm.hotelList && (
                <div
                    style={{
                        display: "flex",
                        alignItems: "flex-start",
                        gap: "40px",
                        marginTop: "30px"
                    }}
                >
                    <aside
                        style={{
                            width: "200px",
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px"
                        }}
                    >
                        {vm.facilities?.length > 0 && (
                            <>
                                <h3>Facilities</h3>

                                {vm.facilities.map((facility) => (
                                    <label
                                        key={facility}
                                        style={{
                                            display: "flex",
                                            alignItems: "center",
                                            gap: "8px"
                                        }}
                                    >
                                        <input
                                            type="checkbox"
                                            value={facility}
                                            checked={vm.selectedFacilities.includes(facility)}
                                            onChange={(e) =>
                                                vm.handleFacilitiesChange(facility)}
                                        />
                                        {facility}
                                    </label>
                                    ))}
                                </>
                        )}

                    </aside>

                    <div
                        style={{
                            flex: 1
                        }}
                    >
                        {vm.hotelList.map((hotel) => (
                            <Link
                                key={hotel.id}
                                to={`/rooms/${hotel.id}?checkInDate=${vm.form.checkInDate}&checkOutDate=${vm.form.checkOutDate}`}
                                style={{
                                    textDecoration: "none"
                                }}
                            >
                                <div>
                                    <h2>{hotel.name}</h2>
                                    <p>City: {hotel.cityName}</p>
                                    <p>
                                        Address: {hotel.strett} {hotel.buildingNumber}
                                    </p>
                                    <p>Stars: {hotel.stars}</p>
                                    <p>Number of ratings: {hotel.numberOfRatings}</p>
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>
            )}
        </>
    );
}