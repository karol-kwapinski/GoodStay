import Header from "./components/Header.jsx";
import {useReservationsViewModel} from "../viewmodel/useReservationsViewModel.js";

export default function UserPanelPage() {

    const vm = useReservationsViewModel();

    return (
        <div>
            <Header />
            <br></br>
            {vm.error && (
                <div>
                    {vm.error}
                </div>
            )}

            {vm.loading && (
                <div>
                    <p>Loading...</p>
                </div>
            )}

            {vm.reservations.map((reservation) => (
                <div key={reservation.id}>
                    <p>Check in date: {reservation.checkInDate}</p>
                    <p>Check out date: {reservation.checkOutDate}</p>
                    <p>City: {reservation.cityName}</p>
                    <p>Hotel: {reservation.hotelName}</p>

                    {!vm.visibleReservations[reservation.id] ? (
                        <button onClick={() =>
                            vm.handleReservationDetails(reservation.id)}>
                            Show details
                        </button>
                    ) : (
                        <button onClick={() =>
                            vm.hideReservationDetails(reservation.id)}>
                            Hide details
                        </button>
                    )}

                    {vm.visibleReservations[reservation.id] &&
                        vm.reservationDetails[reservation.id] && (
                        <div>
                            <p>Check in from: {vm.reservationDetails[reservation.id].checkInFrom}</p>
                            <p>Check in until: {vm.reservationDetails[reservation.id].checkInUntil}</p>
                            <p>Checkout: {vm.reservationDetails[reservation.id].checkOutUntil}</p>
                            <p>First name: {vm.reservationDetails[reservation.id].guestFirstName}</p>
                            <p>Last name: {vm.reservationDetails[reservation.id].guestLastName}</p>
                            <p>E-mail: {vm.reservationDetails[reservation.id].guestEmail}</p>
                            <p>Phone number: {vm.reservationDetails[reservation.id].guestPhoneNumber}</p>
                            <p>Country: {vm.reservationDetails[reservation.id].guestCountry}</p>
                            <p>Total price: {vm.reservationDetails[reservation.id].totalPrice}</p>
                            <p>Booked at: {vm.reservationDetails[reservation.id].createdAt}</p>
                            <p>Status: {vm.reservationDetails[reservation.id].reservationStatus}</p>

                            <h3>Booked Rooms:</h3>

                            {vm.reservationDetails[reservation.id].roomsDetailsList.map((roomsDetails) => (
                                <div key={roomsDetails.roomType}>
                                    <p>Room type: {roomsDetails.roomType}</p>
                                    <p>Quantity: {roomsDetails.quantity}</p>
                                    <p>Max number of guests: {roomsDetails.maxGuests}</p>
                                </div>
                            ))}
                        </div>
                    )}
                    <br></br>
                </div>
            ))}
        </div>
    )
}