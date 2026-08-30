import {useAuth} from "../../config/authContext.jsx";
import {useLoginViewModel} from "../../viewmodel/useLoginViewModel.js";
import {Link, useNavigate} from "react-router-dom";

export default function Header() {

    const {user} = useAuth();
    const vm = useLoginViewModel();
    const navigate = useNavigate();

    return (
        <div style={{ display: "flex", gap: "20px"}}>
            {user ? (
                <div>
                    <button onClick={vm.handleLogout}>
                        LOG OUT
                    </button>
                    <Link to={'/userPanel'}> User Panel </Link><br />
                </div>
            ) : (
                <div>
                    <button onClick={() => navigate("/login")}>
                        LOG IN
                    </button>
                </div>
            )}
            {user?.role === 'ADMIN' && (
                <Link to={'/adminPanel'}> Admin Panel</Link>
            )}
            <Link to={'/'}> GoodStay </Link>
            {user && (
                <>
                    <p>Logged in as: {user.email}</p>
                </>
            )}
        </div>
    )
}