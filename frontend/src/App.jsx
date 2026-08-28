import {BrowserRouter as Router, Routes, Route, useNavigate, Navigate} from 'react-router-dom';
import RegisterPage from "./view/RegisterPage.jsx";
import {AuthProvider} from "./config/authContext.jsx";
import LoginPage from "./view/LoginPage.jsx";
import HotelListingPage from "./view/HotelListingPage.jsx";
import HotelPage from "./view/HotelPage.jsx";
import ReservationPage from "./view/ReservationPage.jsx";
import UserPanelPage from "./view/UserPanelPage.jsx";
import {useAuth} from "./config/authContext.jsx";

function AppContent() {

    const {user} = useAuth();

    return (
        <Routes>
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/hotelListing" element={<HotelListingPage />} />
            <Route path="/hotel/:hotelId" element={<HotelPage />} />

            <Route path="/reservation/:hotelId" element={ user ? <ReservationPage />
            : <Navigate to="/hotelListing"/> } />

            <Route path="/userPanel" element={ user ? < UserPanelPage/>
                : <Navigate to="/hotelListing" />} />
        </Routes>
    )
}

export default function App() {
    return (
        <AuthProvider>
            <Router>
                <AppContent />
            </Router>
        </AuthProvider>
    );
}