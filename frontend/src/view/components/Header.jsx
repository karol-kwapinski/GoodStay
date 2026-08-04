import {useAuth} from "../../config/authContext.jsx";
import {useLoginViewModel} from "../../viewmodel/useLoginViewModel.js";
import {useNavigate} from "react-router-dom";

export default function Header() {

    const {user} = useAuth();
    const vm = useLoginViewModel();
    const navigate = useNavigate();

    return (
        <div>
            {user ? (
                <div>
                    <button onClick={vm.handleLogout}>
                        LOG OUT
                    </button>
                </div>
            ) : (
                <div>
                    <button onClick={() => navigate("/login")}>
                        LOG IN
                    </button>
                </div>
            )}
        </div>
    )
}