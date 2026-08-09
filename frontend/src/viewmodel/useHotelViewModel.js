import {useEffect, useState} from "react";
import {getAllHotelsByReservationDateAndCityName} from "../model/hotelAPI.js";

export function useHotelViewModel() {

    const [form, setForm] = useState({
        cityName: "",
        checkInDate: "",
        checkOutDate: ""
    });

    const [hotelList, setHotelList] = useState([]);

    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)
    const today = new Date();
    const minDate =
        `${today.getFullYear()}-${String(today.getMonth() + 1)
            .padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`;

    const handleChange = (event) => {
        setForm({
            ...form,
            [event.target.name]: event.target.value
        });
    }

    const getNextDay = (dateString) => {
        if (!dateString) return "";

        const [year, month, day] = dateString.split("-").map(Number);
        const date = new Date(year, month - 1, day);

        date.setDate(date.getDate() + 1);

        return date.toLocaleDateString("en-CA");
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        try {
            setLoading(true)
            const response = await getAllHotelsByReservationDateAndCityName(form);
            setHotelList(response);
            setError(null);
        } catch (error) {
            setHotelList([]);
            switch (error.code) {
                case "INVALID_DATE_RANGE":
                    setError("Date range is invalid");
                    break
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                default:
                    setError("Unknown Error has occurred");
            }
        } finally {
            setLoading(false);
        }
    }

    return {
        handleChange,
        handleSubmit,
        getNextDay,
        hotelList,
        form,
        error,
        loading,
        minDate
    }
}