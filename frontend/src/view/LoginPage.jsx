import {useLoginViewModel} from "../viewmodel/useLoginViewModel.js";

export default function LoginPage() {

    const vm = useLoginViewModel();

    return (
      <>
          <form onSubmit={vm.handleSubmit}>
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

              <button type="submit" disabled={vm.loading}>
                  Login
              </button>
          </form>
      </>
    );
}