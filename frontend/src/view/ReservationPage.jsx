import {useReservationViewModel} from "../viewmodel/useReservationViewModel.js";
import Header from "./components/Header.jsx";

export default function ReservationPage() {

    const vm = useReservationViewModel();

    return (
        <div>
            <Header />
            <form onSubmit={vm.handleSubmit}>
                <input
                    name="firstName"
                    value={vm.form.firstName}
                    onChange={vm.handleChange}
                    placeholder="First Name"
                />
                <input
                    name="lastName"
                    value={vm.form.lastName}
                    onChange={vm.handleChange}
                    placeholder="Last Name"
                />
                <input
                    name="email"
                    type="email"
                    value={vm.form.email}
                    onChange={vm.handleChange}
                    placeholder="E-mail"
                />
                <input
                    name="phoneNumber"
                    value={vm.form.phoneNumber}
                    onChange={vm.handleChange}
                    placeholder="Phone number"
                />
                <input
                    name="country"
                    value={vm.form.country}
                    onChange={vm.handleChange}
                    placeholder="Country"
                />
                <button type="submit">
                    Pay now
                </button>
            </form>
            {vm.error && (
                <div>
                    {vm.error}
                </div>
            )}
            <p>Total price: {vm.totalPrice} zł</p>
        </div>
    )
}