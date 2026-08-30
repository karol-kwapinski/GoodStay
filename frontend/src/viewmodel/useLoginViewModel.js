import {useState} from "react";
import {loginUser, logoutUser} from "../model/userAPI.js";
import {useAuth} from "../config/authContext.jsx"
import {useNavigate} from "react-router-dom";

export function useLoginViewModel() {

    const [formData, setFormData] = useState({
        email: "",
        password: ""
    })

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { setUser } = useAuth();
    const navigate = useNavigate();

    const handleChange = (event) => {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        })
    }

    const handleSubmit = async (event) => {

        event.preventDefault();

        try {
            setLoading(true);
            const user = await loginUser(formData);
            setUser(user);
            setError("");
        } catch (error) {
            switch(error.code) {

                case "INVALID_CREDENTIALS":
                    setError("Incorrect e-mail or password");
                    break
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable")
                    break
                default:
                    setError("Unknown error has occurred");
            }
        } finally {
            setLoading(false);
        }
    }

    const handleLogout = async (event) => {
        event.preventDefault();

        try {
            await logoutUser();
            setUser(null);
            navigate('/login');
        } catch (error) {
            setError("Could not log out user");
        }
    }

    return {
        handleChange,
        handleSubmit,
        handleLogout,
        formData,
        error,
        loading
    }
}