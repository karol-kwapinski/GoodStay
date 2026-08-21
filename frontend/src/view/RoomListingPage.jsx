import {useRoomViewModel} from "../viewmodel/useRoomViewModel.js";
import Header from "./components/Header.jsx";

export default function RoomListingPage() {

    const vm = useRoomViewModel();

    return (
        <div>
            <Header />
            <table>
                <thead>
                    <tr>
                        <th>Room type</th>
                        <th>Price per night</th>
                        <th>Number of guests</th>
                        <th>Number of rooms</th>
                        <th>Choose</th>
                    </tr>
                </thead>

                <tbody>
                    {vm.rooms.map((roomType) => (
                        <tr key={roomType.id}>
                            <td>{roomType.roomType}</td>
                            <td>{roomType.pricePerNight}</td>
                            <td>{roomType.maxNumberOfGuests}</td>
                            <td>{roomType.numberOfRooms}</td>
                            <td>
                                <select
                                    value={vm.selectedRoomTypes[roomType.id] ?? 0}
                                    onChange={(e) =>
                                        vm.handleRoomTypeChange(
                                            roomType.id,
                                            Number(e.target.value)
                                        )}
                                >
                                    {
                                        Array.from(
                                            {length: roomType.numberOfRooms + 1},
                                            (_, i) => (
                                                <option key={i} value={i}>
                                                    {i}
                                                </option>
                                            )
                                        )
                                    }
                                </select>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <button
                disabled={!vm.isAnyRoomSelected}
                onClick={() => vm.navigate(
                    vm.isLoggedIn()
                        ? `/reservation/${vm.hotelId}?${vm.params.toString()}`
                        : '/login'
                )}
            >
                Reserve
            </button>

        </div>
    )
}