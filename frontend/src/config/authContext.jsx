import {createContext, useContext, useEffect, useState} from "react";
import {getCurrentUser} from "../model/userAPI.js";

const AuthContext = createContext();

export const useAuth = () => {
    return useContext(AuthContext);
}

export function AuthProvider({children}) {

    const [user, setUser] = useState(null);

    useEffect(() => {
        const loadUser = async () => {
            try {
                const data = await getCurrentUser();
                setUser(data);
            } catch (e) {
                setUser(null);
            }
        }

        loadUser();
    }, []);

    return (
        <AuthContext.Provider value={
            {
                user,
                setUser
            }
        }>
            {children}
        </AuthContext.Provider>
    )
}