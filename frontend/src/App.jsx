import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RegisterPage from "./view/RegisterPage.jsx";

function AppContent() {

    return (
        <Routes>
            <Route path="/register" element={<RegisterPage />} />
        </Routes>
    )
}

export default function App() {
    return (
        <Router>
            <AppContent />
        </Router>
    );
}