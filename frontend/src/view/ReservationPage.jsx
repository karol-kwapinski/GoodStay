import {useRoomViewModel} from "../viewmodel/useRoomViewModel.js";

export default function ReservationPage() {

    const vm = useRoomViewModel();

    return (
        <div>
            <table>
                <thead>
                    <tr>
                        <th>Room type</th>
                        <th>Price per night</th>
                        <th>Number of guests</th>
                    </tr>
                </thead>

                <tbody>
                    {vm.rooms.map((room) => (
                        <tr key={room.id}>
                            <td>{room.roomType}</td>
                            <td>{room.pricePerNight}</td>
                            <td>{room.maxNumberOfGuests}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}