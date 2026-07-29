import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {registerUser} from "../model/userAPI.js";

export function useRegisterViewModel() {

    const [formData, setFormData] = useState({
        email: "",
        password: "",
        confirmPassword: "",
        firstName: "",
        lastName: "",
        phoneNumber: "",
        country: ""
    })

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleChange = (event) => {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        });
    }

    const handleSubmit = async (event) => {

        event.preventDefault();

        try {
            setLoading(true);
            await registerUser(formData);
            setError("");
        } catch (e) {
            switch (e.code) {
                case "EMAIL_ALREADY_EXISTS":
                    setError("Email already exists");
                    break
                case "SERVER_UNAVAILABLE":
                    setError("Server is unavailable");
                    break
                default:
                    setError("Failed to register user");
            }
        } finally {
            setLoading(false);
        }
    }

    return {
        handleSubmit,
        handleChange,
        formData,
        loading,
        error
    }
}