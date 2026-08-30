import {useRegisterViewModel} from "../viewmodel/useRegisterViewModel.js";
import Header from "./components/Header.jsx";

export default function RegisterPage() {

    const vm = useRegisterViewModel();
    return (
        <div>
            <Header />
            <div
                style={{
                    display: "flex",
                    justifyContent: "center"
                }}
            >
                <form
                    onSubmit={vm.handleSubmit}
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "10px",
                        width: "300px"
                    }}
                >
                    <input
                        name="email"
                        type="email"
                        value={vm.formData.email}
                        onChange={vm.handleChange}
                        placeholder="Email"
                    />
                    <input
                        name="password"
                        type="password"
                        value={vm.formData.password}
                        onChange={vm.handleChange}
                        placeholder="Password"
                    />
                    <input
                        name="confirmPassword"
                        type="password"
                        value={vm.formData.confirmPassword}
                        onChange={vm.handleChange}
                        placeholder="Confirm Password"
                    />
                    <input
                        name="firstName"
                        value={vm.formData.firstName}
                        onChange={vm.handleChange}
                        placeholder="First name"
                    />
                    <input
                        name="lastName"
                        value={vm.formData.lastName}
                        onChange={vm.handleChange}
                        placeholder="Last name"
                    />
                    <input
                        name="phoneNumber"
                        value={vm.formData.phoneNumber}
                        onChange={vm.handleChange}
                        placeholder="Phone number"
                    />
                    <input
                        name="country"
                        value={vm.formData.country}
                        onChange={vm.handleChange}
                        placeholder="Country"
                    />

                    {vm.error && (
                        <div>
                            {vm.error}
                        </div>
                    )}

                    {vm.successMessage && (
                        <div>
                            {vm.successMessage}
                        </div>
                    )}

                    <button type="submit" disabled={vm.loading}>
                        Register
                    </button>
                </form>
            </div>
        </div>
    )
}