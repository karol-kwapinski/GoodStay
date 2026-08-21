import {useEffect, useState} from "react";
import {getAllRoomsByDatesAndHotelId} from "../model/roomAPI.js";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
import {useAuth} from "../config/authContext.jsx";

export function useRoomViewModel() {

    const {hotelId} = useParams();
    const [searchParams] = useSearchParams();

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const checkInDate = searchParams.get("checkInDate");
    const checkOutDate = searchParams.get("checkOutDate");
    const [rooms, setRooms] = useState([])
    const [selectedRoomTypes, setSelectedRoomTypes] = useState({});

    const {user} = useAuth();

    const handleRoomTypeChange = (roomTypeId, quantity) => {
        setSelectedRoomTypes(prev => ({
            ...prev,
            [roomTypeId]: quantity
        }));
    }

    const params = new URLSearchParams({
        checkInDate,
        checkOutDate,
        roomTypes: JSON.stringify(selectedRoomTypes)
    });

    const navigate = useNavigate();

    const isAnyRoomSelected = Object.values(selectedRoomTypes)
        .some(quantity => quantity > 0);

    const isLoggedIn = () => !!user;

    useEffect(() => {
        const loadRooms = async () => {

            try {
                setLoading(true);
                const data = await getAllRoomsByDatesAndHotelId(
                    hotelId,
                    checkInDate,
                    checkOutDate);

                setRooms(data);
            } catch(error) {

                setRooms([]);
                switch(error?.code) {
                    case "METHOD_ARGUMENT_NOT_VALID":
                        setError("Dates are not correct");
                        break
                    case "INVALID_DATE_RANGE":
                        setError("Check out day has to be after check in date");
                        break
                    case "SERVER_UNAVAILABLE":
                        setError("Server is unavailable");
                        break
                    default:
                        setError("Unknown error has occurred");
                }
            } finally {
                setLoading(false);
            }

        }

        loadRooms();
    }, []);

    return {
        error,
        loading,
        rooms,
        hotelId,
        params,
        selectedRoomTypes,
        isAnyRoomSelected,
        navigate,
        handleRoomTypeChange,
        isLoggedIn
    }
}