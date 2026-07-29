import axios from 'axios';

export const registerUser = async (form) => {
    try {

        const response = await axios.post(
            "http://localhost:8082/api/users/register", form);

        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {

            if (error.response) {
                throw {
                    status: error.response.status,
                    code: error.response.data.code,
                }
            }
            if (error.request) {
                throw {
                    status: null,
                    code: "SERVER_UNAVAILABLE",
                }
            }
        }
        else {
            throw {
                status: null,
                code: "UNKNOWN_ERROR",
            }
        }
    }
}