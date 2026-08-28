import {useLoginViewModel} from "../viewmodel/useLoginViewModel.js";
import {Link, useNavigate} from "react-router-dom";

export default function LoginPage() {

    const vm = useLoginViewModel();
    const navigate = useNavigate();

    return (
      <div
          style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center"
          }}
      >
          <form
              onSubmit={vm.handleSubmit}
              style={{
                  display: "flex",
                  flexDirection: "column",
                  width: "250px",
              }}
          >
              <input
                  name="email"
                  type="email"
                  onChange={vm.handleChange}
                  value={vm.formData.email}
                  placeholder="E-mail"
              />
              <input
                  name="password"
                  type="password"
                  onChange={vm.handleChange}
                  value={vm.formData.password}
                  placeholder="Password"
              />

              {vm.error && (
                  <div>
                      {vm.error}
                  </div>
              )}

              <button
                  type="submit"
                  disabled={vm.loading}
                  onClick={() => navigate('/hotelListing')}
              >
                  Login
              </button>
          </form>
          <Link
            to={'/register'}
          >
              Don't have an account? Register here!
          </Link>
      </div>
    );
}