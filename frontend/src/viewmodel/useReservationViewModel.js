import {useEffect, useState} from "react";
import {useParams, useSearchParams} from "react-router-dom";
import {useAuth} from "../config/authContext.jsx";
import {addReservation, getTotalPrice} from "../model/ReservationAPI.js";

export function useReservationViewModel() {

    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        email: "",
        phoneNumber: "",
        country: ""
    });

    const { user } = useAuth();
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const [searchParams] = useSearchParams();
    const checkInDate = searchParams.get("checkInDate");
    const checkOutDate = searchParams.get("checkOutDate");
    const [totalPrice, setTotalPrice] = useState();
    const {hotelId} = useParams();
    const selectedRoomTypes = JSON.parse(searchParams.get("roomTypes"));

    const roomTypes = Object.entries(selectedRoomTypes).map(
        ([roomTypeId, quantity]) => ({
            roomTypeId: Number(roomTypeId),
            quantity
        })
    )

    useEffect(() => {
        if (!user) return;
        setForm({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phoneNumber: user.phoneNumber,
            country: user.country
        })
    }, [user]);

    useEffect(() => {
        const getTotal = async () => {
            try {
                const dataToSend = {
                    checkInDate,
                    checkOutDate,
                    roomTypes
                }

                const data = await getTotalPrice(
                    hotelId,
                    dataToSend
                );
                setTotalPrice(data);
            } catch (error) {
                switch(error?.code) {
                    case "INVALID_DATE_RANGE":
                        setError("Date range is invalid");
                        break
                    case "SERVER_UNAVAILABLE":
                        setError("Server is unavailable");
                        break
                    case "DUPLICATE_ROOMS":
                        setError("Found duplicate rooms in request")
                        break
                    case "ROOM_NOT_FOUND":
                        setError("Room / rooms were not found")
                        break
                    case "UNKNOWN_ERROR":
                        setError("Unknown error has occurred");
                        break
                    default:
                        setError("Failed to add reservation");
                        break
                }
            }

        }

        getTotal();

    }, []);

    const handleChange = (event) => {
        setForm({
            ...form,
            [event.target.name]: event.target.value
        })
    }

    const handleSubmit = async (event) => {

        event.preventDefault();

        const data = {
            checkInDate: checkInDate,
            checkOutDate: checkOutDate,
            ...form,
            hotelId: hotelId,
            roomTypes: roomTypes
        }

        try {
            setLoading(true);
            await addReservation(data);
        } catch (error) {
            switch (error?.code) {
                case "INVALID_DATE_RANGE":
                    setError("Date range is invalid");
                    break
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                case "USER_NOT_FOUND":
                    setError("User was not found");
                    break
                case "INVALID_ROOM_TYPES":
                    setError("Found invalid room types")
                    break
                case "INVALID_ROOM_QUANTITY":
                    setError("Room quantity was invalid")
                    break
                case "ROOM_NOT_AVAILABLE":
                    setError("Room / rooms are no longer available");
                    break
                case "UNKNOWN_ERROR":
                    setError("Unknown error has occurred");
                    break
                default:
                    setError("Failed to add reservation");
                    break
            }
        } finally {
            setLoading(false)
        }
    }

    return {
        error,
        loading,
        totalPrice,
        form,
        handleSubmit,
        handleChange,
    }
}