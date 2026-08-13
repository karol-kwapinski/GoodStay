import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RegisterPage from "./view/RegisterPage.jsx";
import {AuthProvider} from "./config/authContext.jsx";
import LoginPage from "./view/LoginPage.jsx";
import HotelListingPage from "./view/HotelListingPage.jsx";
import ReservationPage from "./view/ReservationPage.jsx";

function AppContent() {

    return (
        <Routes>
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/hotelListing" element={<HotelListingPage />} />
            <Route path="/reservation/:hotelId" element={<ReservationPage />} />
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