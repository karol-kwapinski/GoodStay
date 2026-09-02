import React from "react";

export default function AddEditHotelForm({
     op,
     inputValue,
     onChange,
     hotelOwnersEmails,
     error,
     onSubmit
    }) {

    console.log(op);
    return (
        <form onSubmit={onSubmit}
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
                    onChange={onChange}
                    value={inputValue?.name}
                />
                <label>City name:</label>
                <input
                    name="cityName"
                    onChange={onChange}
                    value={inputValue?.cityName}
                />
                <label>Street:</label>
                <input
                    name="street"
                    onChange={onChange}
                    value={inputValue?.street}
                />
                <label>Building number:</label>
                <input
                    name="buildingNumber"
                    onChange={onChange}
                    value={inputValue?.buildingNumber}
                />
                <label>Number of stars:</label>
                <input
                    name="stars"
                    type="number"
                    onChange={onChange}
                    value={inputValue?.stars}
                    min="1"
                    max="5"
                />
                <label>Brand:</label>
                <input
                    name="brand"
                    onChange={onChange}
                    value={inputValue?.brand}
                />
                <label>Owner:</label>
                {hotelOwnersEmails?.length > 0 && (
                    <select
                        name="ownerId"
                        value={inputValue?.ownerId}
                        onChange={onChange}
                    >
                        {hotelOwnersEmails.map((ownerEmail) => (
                            <option key={ownerEmail.id} value={ownerEmail.id}>{ownerEmail.email}</option>
                        ))}
                    </select>
                )}
                <label>Check in from:</label>
                <input
                    name="checkInFrom"
                    type="time"
                    onChange={onChange}
                    value={inputValue?.checkInFrom}
                />

                <label>Check in until:</label>
                <input
                    name="checkInUntil"
                    type="time"
                    onChange={onChange}
                    value={inputValue?.checkInUntil}
                />

                <label>Check out:</label>
                <input
                    name="checkOutUntil"
                    type="time"
                    onChange={onChange}
                    value={inputValue?.checkOutUntil}
                />
            </div>

            <button type="submit">
                {op === "edit" ? "Edit hotel" : "Add hotel"}
            </button>

            {error && (
                <>
                    {error}
                </>
            )}
        </form>
    );
}