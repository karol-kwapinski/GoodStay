import React from "react";
import Header from "./components/Header.jsx";
import {useAdminViewModel} from "../viewmodel/useAdminViewModel.js";
import AddEditHotelForm from "./components/AddEditHotelForm.jsx";

export function AdminPanelPage() {

    const vm = useAdminViewModel();

    return (
        <div
            style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center"
            }}
        >
            <Header />
            {vm.loading && (
                <>
                    {vm.loading}
                </>
            )}
            {vm.error && (
                <>
                    {vm.error}
                </>
            )}
            {vm.hotels?.length > 0 && (
                <div>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>City</th>
                            </tr>
                        </thead>
                        <tbody>
                            {vm.hotels.map((hotel) => (
                                <React.Fragment key={hotel.id}>
                                    <tr>
                                        <td>{hotel.id}</td>
                                        <td>{hotel.name}</td>
                                        <td>{hotel.cityName}</td>
                                        <td>
                                            <button
                                                onClick={() =>
                                                    vm.handleEditHotelFormVisibility(hotel.id)
                                                }
                                            >
                                                {vm.isEditHotelFormVisible[hotel.id] ? "Cancel" : "Edit"}
                                            </button>
                                        </td>
                                    </tr>

                                        {vm.isEditHotelFormVisible[hotel.id] &&
                                            vm.editHotelForms[hotel.id] && (

                                            <tr>
                                                <td colSpan="4">
                                                    <AddEditHotelForm
                                                        op="edit"
                                                        inputValue={vm.editHotelForms[hotel.id]}
                                                        onChange={vm.handleChangeEditHotelForm(hotel.id)}
                                                        hotelOwnersEmails={vm.hotelOwnersEmails}
                                                        error={vm.error}
                                                        onSubmit={(event) =>
                                                            vm.handleAddEditHotel(event, hotel.id)}
                                                    />
                                                </td>
                                            </tr>
                                        )}
                                </React.Fragment>
                            ))}
                        </tbody>
                    </table>
                    <button onClick={() => vm.fetchHotels(-1)}
                            style={{
                                width: "100px"
                            }}
                    >
                        Previous page
                    </button>
                    <button onClick={() => vm.fetchHotels(1)}
                        style={{
                            width: "100px"
                        }}
                    >
                        Next page
                    </button>
                    <p>Page {vm.currentPage + 1} / {vm.numberOfPages}</p>
                    <br/><br/><br/>
                </div>
            )}
            {vm.isAddHotelFormVisible ? (
                <div
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center"
                    }}
                >
                    <button onClick={vm.handleAddHotelFormVisibility}>
                        Cancel
                    </button>

                    <AddEditHotelForm
                        op="Add"
                        inputValue={vm.addHotelForm}
                        onChange={vm.handleChangeAddHotelForm}
                        hotelOwnersEmails={vm.hotelOwnersEmails}
                        error={vm.error}
                        onSubmit={(event) =>
                            vm.handleAddEditHotel(event, null)}
                    />
                </div>
            ) : (
                <button onClick={vm.handleAddHotelFormVisibility}>
                    Add hotel
                </button>
            )}
        </div>
    )
}