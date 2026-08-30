import Header from "./components/Header.jsx";
import {useAdminViewModel} from "../viewmodel/useAdminViewModel.js";

export function AdminPanelPage() {

    const vm = useAdminViewModel();

    return (
        <div>
            <Header />
            {vm.isFormVisible ? (
                <div
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center"
                    }}
                >
                    <button onClick={vm.handleFormVisibility}>
                        Cancel
                    </button>

                    <form onSubmit={vm.handleAddHotel}
                          style={{
                              display: "flex",
                              flexDirection: "column",
                              alignItems: "center"
                          }}
                    >
                        <div
                            style={{
                                display: "grid",
                                gridTemplateColumns: "150px 200px",
                                alignItems: "center",
                                textAlign: "left"
                            }}
                        >
                            <label>Hotel name:</label>
                            <input
                                name="name"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.name}
                            />
                            <label>City name:</label>
                            <input
                                name="cityName"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.cityName}
                            />
                            <label>Street:</label>
                            <input
                                name="street"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.street}
                            />
                            <label>Building number:</label>
                            <input
                                name="buildingNumber"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.buildingNumber}
                            />
                            <label>Number of stars:</label>
                            <input
                                name="stars"
                                type="number"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.stars}
                                min="1"
                                max="5"
                            />
                            <label>Brand:</label>
                            <input
                                name="brand"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.brand}
                            />
                            <label>Owner:</label>
                            {vm.hotelOwnersEmails?.length > 0 && (
                                <select
                                    name="ownerId"
                                    value={vm.addHotelForm.ownerId}
                                    onChange={vm.handleChangeAddHotelForm}
                                >
                                    {vm.hotelOwnersEmails.map((ownerEmail) => (
                                        <option key={ownerEmail.id} value={ownerEmail.id}>{ownerEmail.email}</option>
                                    ))}
                                </select>
                            )}
                            <label>Check in from:</label>
                            <input
                                name="checkInFrom"
                                type="time"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.checkInFrom}
                            />

                            <label>Check in until:</label>
                            <input
                                name="checkInUntil"
                                type="time"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.checkInUntil}
                            />

                            <label>Check out:</label>
                            <input
                                name="checkOutUntil"
                                type="time"
                                onChange={vm.handleChangeAddHotelForm}
                                value={vm.addHotelForm.checkOutUntil}
                            />
                        </div>
                        <button type="submit">
                            Add hotel
                        </button>

                        {vm.error && (
                            <>
                                {vm.error}
                            </>
                        )}

                    </form>
                </div>
            ) : (
                <button onClick={vm.handleFormVisibility}>
                    Add hotel
                </button>
            )}
        </div>
    )
}