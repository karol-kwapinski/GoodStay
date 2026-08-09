import {useHotelViewModel} from "../viewmodel/useHotelViewModel.js";

export default function HotelListingPage() {

    const vm = useHotelViewModel();
    return (
        <>
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
                <>
                    {vm.hotelList.map((hotel) => (
                        <div key={hotel.id}>
                            <h2>{hotel.name}</h2>
                            <p>City: {hotel.cityName}</p>
                            <p>Address: {hotel.strett} {hotel.buildingNumber}</p>
                            <p>Stars: {hotel.stars}</p>
                            <p>Number of ratings: {hotel.numberOfRatings}</p>
                        </div>
                    ))}
                </>
            )}
        </>
    )
}