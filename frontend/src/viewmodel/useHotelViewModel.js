import {useEffect, useState} from "react";
import {getAllHotelsByReservationDateAndCityName} from "../model/hotelAPI.js";
import {getFacilities} from "../model/FacilityAPI.js";

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
    const [facilities, setFacilities] = useState([]);
    const [selectedFacilities, setSelectedFacilities] = useState([]);

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
        await searchHotels(selectedFacilities);
    }

    const searchHotels = async (facilities) => {
        try {
            setLoading(true);

            const response = await getAllHotelsByReservationDateAndCityName(
                form.cityName,
                form.checkInDate,
                form.checkOutDate,
                facilities
            );
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

    useEffect(() => {
        const fetchFacilities = async () => {
            if (!hotelList?.length) {
                return;
            }

            try {
                const params = new URLSearchParams();

                hotelList.forEach(hotel => {
                    params.append("hotelIds", hotel.id);
                });

                const data = await getFacilities(params);
                setFacilities(data);

            } catch (error) {
                switch (error.code) {
                    case "SERVER_UNAVAILABLE":
                        setError("Server is unavailable");
                        break;
                    default:
                        setError("Unknown Error has occurred");
                }
            }
        };

        fetchFacilities();
    }, [hotelList]);

    const handleFacilitiesChange = async (facility) => {
        const newFacilities = selectedFacilities.includes(facility)
            ? selectedFacilities.filter(f => f !== facility)
            : [...selectedFacilities, facility];

        setSelectedFacilities(newFacilities);

        await searchHotels(newFacilities);
    };

    return {
        handleChange,
        handleSubmit,
        getNextDay,
        handleFacilitiesChange,
        hotelList,
        form,
        error,
        loading,
        minDate,
        facilities,
        selectedFacilities,
    }
}