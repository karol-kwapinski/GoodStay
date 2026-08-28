import {useEffect, useState} from "react";
import {getAllRoomsByDatesAndHotelId} from "../model/roomAPI.js";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
import {useAuth} from "../config/authContext.jsx";
import {addReview, getReviews} from "../model/reviewAPI.js";

export function useRoomViewModel() {

    const {hotelId} = useParams();
    const [searchParams] = useSearchParams();

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const checkInDate = searchParams.get("checkInDate");
    const checkOutDate = searchParams.get("checkOutDate");
    const [rooms, setRooms] = useState([])
    const [selectedRoomTypes, setSelectedRoomTypes] = useState({});
    const [reviews, setReviews] = useState([]);
    const {user} = useAuth();
    const [form, setForm] = useState({
        rating: "10",
        comment: ""
    });

    const params = new URLSearchParams({
        checkInDate,
        checkOutDate,
        roomTypes: JSON.stringify(selectedRoomTypes)
    });

    const handleRoomTypeChange = (roomTypeId, quantity) => {
        setSelectedRoomTypes(prev => ({
            ...prev,
            [roomTypeId]: quantity
        }));
    }

    const navigate = useNavigate();

    const isAnyRoomSelected = Object.values(selectedRoomTypes)
        .some(quantity => quantity > 0);

    const handleChange = (event) => {
        setForm(prev => ({
            ...prev,
            [event.target.name]: event.target.value
        }))
    }

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

        const loadReviews = async () => {
            try {
                const data = await getReviews(hotelId);
                setReviews(data);
            } catch (error) {
                setReviews([]);
                switch(error?.code) {
                    case "SERVER_UNAVAILABLE":
                        setError("Server is unavailable");
                        break
                    default:
                        setError("Unknown error has occurred");
                }
            }
        }

        loadRooms();
        loadReviews();
    }, []);

    const handleReviewSubmit = async (event) => {

        event.preventDefault();

        try {

            const data = {
                ...form,
                hotelId
            }
            const review = await addReview(data);
            setReviews(prev => [...prev, review])
            setForm({
                rating: "10",
                comment: ""
            });
            setError(null);
        } catch (error) {
            switch(error?.code) {
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                case "USER_NOT_FOUND":
                    setError("User has not been found");
                    break
                case "HOTEL_DOES_NOT_EXIST":
                    setError("Hotel does not exist");
                    break
                case "REVIEW_ALREADY_EXISTS":
                    setError("You have already submitted a review");
                    break
                default:
                    setError("Unknown error has occurred");
            }
        }
    }

    return {
        error,
        loading,
        rooms,
        hotelId,
        params,
        selectedRoomTypes,
        isAnyRoomSelected,
        reviews,
        form,
        navigate,
        handleRoomTypeChange,
        isLoggedIn,
        handleChange,
        handleReviewSubmit
    }
}