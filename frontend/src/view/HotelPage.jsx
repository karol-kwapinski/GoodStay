import {useHotelViewModel} from "../viewmodel/useHotelViewModel.js";
import Header from "./components/Header.jsx";

export default function HotelPage() {

    const vm = useHotelViewModel();

    return (
        <div>
            <Header />

            {vm.hotel && (
                <div>
                    <h3>{vm.hotel.name}</h3>
                    <p>City name: {vm.hotel.cityName}</p>
                    <p>Street: {vm.hotel.street}</p>
                    <p>Building number: {vm.hotel.buildingNumber}</p>
                    <p>{vm.hotel.stars} star hotel</p>
                    <p>Hotel has {vm.hotel.numberOfRatings} ratings</p>
                </div>
            )}

            <br/>
            {vm.rooms.length > 0 && (
                <div>
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
            )}

            {vm.reviews?.length > 0 && (
                <>
                    <h3>Reviews: </h3>

                    {vm.reviews.map((review) => (
                        <>
                            <p>Username: {review.userName}</p>
                            <p>Rating: {review.rating}</p>
                            {review.comment &&
                                <p>Comment: {review.comment}</p>
                            }
                            <p>Review added: {review.createdAt}</p>
                            <br />
                        </>
                    ))}
                </>
            )}

            <br/>
            {vm.isLoggedIn() ? (
                <div>
                    <form onSubmit={vm.handleReviewSubmit}>

                        <p>Rate hotel!</p>
                        <select
                            name="rating"
                            onChange={vm.handleChange}
                            value={vm.form.rating}
                        >
                            {Array.from({length: 10}, (_, i) => (
                                <option key={i + 1} value={i + 1}>
                                    {i + 1}
                                </option>
                            ))}
                        </select>

                        <textarea
                            name="comment"
                            onChange={vm.handleChange}
                            value={vm.form.comment}
                            placeholder="Comment"
                            rows={5}
                            maxLength={3000}
                        />

                        <button type="submit">
                            Submit review
                        </button>
                    </form>
                    {vm.error && (
                        <div>
                            {vm.error}
                        </div>
                    )}
                </div>
            ) : (
                <p>Log in to submit a review!</p>
            )}

        </div>
    )
}