import {useEffect, useState} from "react";
import {addHotel, editHotel, getHotels, getHotelWithFullDataById} from "../model/hotelAPI.js";
import {getAllHotelOwnersEmails} from "../model/userAPI.js";

export function useAdminViewModel() {

    const initialForm = {
        name: "",
        cityName: "",
        street: "",
        buildingNumber: "",
        stars: "",
        checkInFrom: "",
        checkInUntil: "",
        checkOutUntil: "",
        brand: "",
        ownerId: ""
    };
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [addHotelForm, setAddHotelForm] = useState(initialForm);
    const [editHotelForms, setEditHotelForms] = useState({});

    const [isAddHotelFormVisible, setIsAddHotelFormVisible] = useState(false);
    const [isEditHotelFormVisible, setIsEditHotelFormVisible] = useState({});
    const [hotelOwnersEmails, setHotelOwnerEmails] = useState([]);
    const [hotels, setHotels] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [numberOfPages, setNumberOfPages] = useState();

    const handleAddHotelFormVisibility = () => setIsAddHotelFormVisible(prev => !prev);

    const handleEditHotelFormVisibility = (hotelId) => {
        const isVisible = isEditHotelFormVisible[hotelId];

        if (isVisible === undefined) {
            fetchHotelWithFullData(hotelId);
        }

        setIsEditHotelFormVisible(prev => ({
            ...prev,
            [hotelId]: !prev[hotelId]
        }));
    };

    const handleChangeEditHotelForm = (hotelId) => (event) => {
        setEditHotelForms(prev => ({
            ...prev,
            [hotelId]: {
                ...prev[hotelId],
                [event.target.name]: event.target.value
            }
        }))
    }

    const handleChangeAddHotelForm = (event) => {
        setAddHotelForm(prev => ({
            ...addHotelForm,
            [event.target.name]: event.target.value
        }))
    }

    useEffect(() => {
        const fetchHotelOwnersEmails = async () => {
            try {
                const data = await getAllHotelOwnersEmails();
                setHotelOwnerEmails(data);
            } catch (error) {
                setHotelOwnerEmails([]);
                switch(error?.code) {
                    case "SERVER_UNAVAILABLE":
                        setError("Server is unavailable");
                        break
                    default:
                        setError("Could not fetch hotel owners");
                }
            }
        }

        fetchHotels(0);
        fetchHotelOwnersEmails();
    }, []);

    const fetchHotels = async (pageDiff) => {

        const nextPage = currentPage + pageDiff;

        if (pageDiff < -1 || pageDiff > 1) {
            return;
        }

        if (nextPage < 0 || nextPage >= numberOfPages) {
            return;
        }

        try {
            setLoading(true);
            const data = await getHotels(nextPage, 2);
            setHotels(data.content);
            setCurrentPage(data.page);
            setNumberOfPages(data.totalPages);
            setError(null);
        } catch (error) {
            setHotels([]);
            switch(error?.code) {
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                default:
                    setError("Could not fetch hotels");
            }
        } finally {
            setLoading(false);
        }
    }

    const handleAddEditHotel = async (event, hotelId) => {
        event.preventDefault();

        try {

            if (hotelId === null) {

                const data = {
                    ...addHotelForm,
                    stars: Number(addHotelForm.stars),
                    ownerId: Number(addHotelForm.ownerId)
                }

                const addedHotel = await addHotel(data);
                setHotels(prev => [...prev, addedHotel]);
                setError(null);
                setAddHotelForm(initialForm);
                setIsAddHotelFormVisible(false);
            }

            else {
                const data = {
                    ...editHotelForms[hotelId],
                    stars: Number(editHotelForms[hotelId].stars),
                    ownerId: Number(editHotelForms[hotelId].ownerId)
                }

                const editedHotel = await editHotel(data, hotelId);

                setHotels(prev =>
                    prev.map(hotel =>
                        hotel.id === hotelId ? editedHotel : hotel
                    )
                );

                setError(null);
                setIsEditHotelFormVisible(prev => ({
                    ...prev,
                    [hotelId]: false
                }));
            }

        } catch (error) {
            switch(error?.code) {
                case "METHOD_ARGUMENT_NOT_VALID":
                    setError("Submitted data is incorrect");
                    break
                case "INVALID_TIME_RANGE":
                    setError("Check in from time has to be before " +
                        "check in until time and check out time has to be before check in " +
                        "from time");
                    break
                case "USER_NOT_FOUND":
                    setError("Owner is not found");
                    break
                case "INVALID_HOTEL_DATA":
                    setError("Hotel with given city, street, and building number" +
                        " already exists");
                    break
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                default:
                    setError("Unknown error has occurred");
            }
        }
    }

    const fetchHotelWithFullData = async (hotelId) => {
        try {
            const data = await getHotelWithFullDataById(hotelId);
            setEditHotelForms((prev) => ({
                ...prev,
                [hotelId]: data
            }));
        } catch (error) {
            switch(error?.code) {
                case "HOTEL_DOES_NOT_EXIST":
                    setError("Hotel does not exist")
                    break
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                default:
                    setError("Unknown error has occurred");
            }
        }
    }


    return {
        error,
        addHotelForm,
        isAddHotelFormVisible,
        hotelOwnersEmails,
        loading,
        hotels,
        currentPage,
        numberOfPages,
        isEditHotelFormVisible,
        editHotelForms,
        handleChangeAddHotelForm,
        handleAddEditHotel,
        handleAddHotelFormVisibility,
        fetchHotels,
        handleEditHotelFormVisibility,
        handleChangeEditHotelForm
    }
}