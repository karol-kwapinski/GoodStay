import {useEffect, useState} from "react";
import {getReservationDetails, getReservations} from "../model/ReservationAPI.js";

export function useReservationsViewModel() {

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [reservations, setReservations] = useState([]);
    const [reservationDetails, setReservationDetails] = useState({});
    const [visibleReservations, setVisibleReservations] = useState({});

    useEffect(() => {
        const fetchReservations = async () => {
            try {
                setLoading(true);
                const data = await getReservations();
                setReservations(data);
                setError(null);
            } catch (error) {
                switch (error?.code) {
                    case "SERVER_UNAVAILABLE":
                        setError("Server is unavailable");
                        break
                    case "USER_NOT_FOUND":
                        setError("User was not found");
                        break
                    case "UNKNOWN_ERROR":
                        setError("Unknown error has occurred");
                        break
                    default:
                        setError("Failed to fetch reservations");
                        break
                }
            } finally {
                setLoading(false)
            }
        }

        fetchReservations();
    }, []);

    const handleReservationDetails = async (reservationId) => {

        try {

            if (reservationDetails[reservationId]) {
                setVisibleReservations(prev => ({
                    ...prev,
                    [reservationId]: true
                }));
                return;
            }

            setLoading(true);
            const data = await getReservationDetails(reservationId);
            console.log(data)

            setReservationDetails(prev => ({
                ...prev,
                [reservationId]: data
            }));

            setVisibleReservations(prev => ({
                ...prev,
                [reservationId]: true
            }));

            setError(null);
        } catch (error) {
            switch (error?.code) {
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                case "USER_NOT_FOUND":
                    setError("User was not found");
                    break
                case "UNKNOWN_ERROR":
                    setError("Unknown error has occurred");
                    break
                default:
                    setError("Failed to fetch reservation details");
                    break
            }
        } finally {
            setLoading(false);
        }
    }

    const hideReservationDetails = (reservationId) => {
        setVisibleReservations(prev => ({
            ...prev,
            [reservationId]: false
        }))
    }

    return {
        error,
        loading,
        reservations,
        reservationDetails,
        visibleReservations,
        handleReservationDetails,
        hideReservationDetails
    }
}