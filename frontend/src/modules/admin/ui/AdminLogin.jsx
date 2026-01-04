import "./css/AdminLogin.css"
import useAdminLogin from "../hooks/AdminLoginHook/useAdminLogin.jsx";
import { Field,  IconMail, IconLock, IconArrow } from "../../../Share/Admin_Share_Component/AdminHelperComponent.jsx";
export default function AdminLogin (){
    const {
        year,
        email, setEmail,
        password, setPassword,
        remember, setRemember,
        showPassword, setShowPassword,
        loading, status,
        onSubmit,
    } = useAdminLogin();
    return(
        <>
            <div className="dv-page">
                <div className="dv-noise" aria-hidden="true" />

                <main className="dv-wrap">
                    <section className="dv-shell">
                        {/* Left / Brand */}
                        <div className="dv-panel dv-brand">
                            <div className="dv-blob dv-blob1" aria-hidden="true" />
                            <div className="dv-blob dv-blob2" aria-hidden="true" />

                            <div>
                                <div className="dv-logo" aria-label="DeVision">
                                    <div className="dv-mark" aria-hidden="true">
                                        <div className="dv-markInner" />
                                    </div>
                                    <div className="dv-name">
                                        <strong>DeVision</strong>
                                        <span>• Admin Console</span>
                                    </div>
                                </div>
                            </div>
                            <div className="dv-blob dv-blob1 dv-formBlob1" aria-hidden="true" />
                            <div className="dv-blob dv-blob2 dv-formBlob2" aria-hidden="true" />

                            <header className="dv-formHeader">
                                <div>
                                    <h2>Admin Login</h2>
                                    <p>Enter your credentials to access the dashboard.</p>
                                </div>
                            </header>

                            <form onSubmit={onSubmit} className="dv-formBody">
                                <div className="dv-fields">
                                    <Field label="Email">
                                        <div className="dv-control">
                                            <IconMail />
                                            <input
                                                value={email}
                                                onChange={(e) => setEmail(e.target.value)}
                                                type="email"
                                                placeholder="admin@devision.com"
                                                autoComplete="email"
                                                required
                                            />
                                        </div>
                                    </Field>

                                    <Field label="Password">
                                        <div className="dv-control">
                                            <IconLock />
                                            <input
                                                value={password}
                                                onChange={(e) => setPassword(e.target.value)}
                                                type={showPassword ? "text" : "password"}
                                                placeholder="••••••••••••"
                                                autoComplete="current-password"
                                                minLength={6}
                                                required
                                                className="dv-passwordInput"
                                            />
                                            <button
                                                type="button"
                                                onClick={() => setShowPassword((s) => !s)}
                                                className="dv-rightBtn"
                                                aria-label={showPassword ? "Hide password" : "Show password"}
                                            >
                                                {showPassword ? "Hide" : "Show"}
                                            </button>
                                        </div>
                                    </Field>
                                </div>

                                <div className="dv-row">
                                    <label className="dv-checkbox">
                                        <input
                                            type="checkbox"
                                            checked={remember}
                                            onChange={(e) => setRemember(e.target.checked)}
                                        />
                                        Remember me
                                    </label>

                                    <a
                                        className="dv-link"
                                        href="#"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            alert("Hook this to your reset flow.");
                                        }}
                                    >
                                        Forgot password?
                                    </a>
                                </div>

                                <div className="dv-actions">
                                    <button type="submit" className="dv-btn dv-btnPrimary" disabled={loading} onClick={() => console.log("submit button clicked")}>
                                        <IconArrow />
                                        {loading ? "Signing in..." : "Sign in"}
                                    </button>
                                </div>
                            </form>
                            <div className="dv-footer">
                                <span>© {year} DeVision</span>
                                <span>Admin UI v1.0</span>
                            </div>
                        </div>
                    </section>
                </main>
            </div>
        </>
    )
}