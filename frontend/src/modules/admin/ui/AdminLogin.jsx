import "./css/AdminLogin.css"
import React, { useMemo, useState } from "react";
import { Field, Pill, IconMail, IconLock, IconShield, IconArrow } from "../../../Share/Admin_Share_Component/AdminHelperComponent.jsx";
export default function AdminLogin (){
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [remember, setRemember] = useState(true);
    const [showPw, setShowPw] = useState(false);
    const [loading, setLoading] = useState(false);
    const [status, setStatus] = useState({ type: "", msg: "" }); // "ok" | "err" | ""

    const year = useMemo(() => new Date().getFullYear(), []);

    const validate = () => {
        const e = email.trim();
        const p = password.trim();
        if (!e || !/^\S+@\S+\.\S+$/.test(e)) return "Please enter a valid admin email address.";
        if (p.length < 6) return "Password must be at least 6 characters.";
        return "";
    };

    const onSubmit = async (ev) => {
        ev.preventDefault();
        setStatus({ type: "", msg: "" });

        const err = validate();
        if (err) return setStatus({ type: "err", msg: err });

        setLoading(true);
        try {
            // Replace with your real backend call:
            // const res = await fetch("/api/admin/login", {
            //   method: "POST",
            //   headers: { "Content-Type": "application/json" },
            //   body: JSON.stringify({ email, password, remember }),
            // });
            // if (!res.ok) throw new Error("Invalid credentials");
            // const data = await res.json();

            await new Promise((r) => setTimeout(r, 700)); // demo delay
            setStatus({ type: "ok", msg: "Signed in (demo). Wire this to your backend auth to continue." });
        } catch (e) {
            setStatus({ type: "err", msg: e?.message || "Login failed. Please try again." });
        } finally {
            setLoading(false);
        }
    };
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
                                        <span>Application • Admin Console</span>
                                    </div>
                                </div>

                                <div className="dv-headline">
                                    <h1>Welcome back, Admin.</h1>
                                    <p>
                                        Sign in to manage applicants, review CVs, and keep your hiring pipeline moving—fast,
                                        secure, and beautifully organized.
                                    </p>

                                    <div className="dv-pillRow" aria-label="Highlights">
                                        <Pill>Role-based access</Pill>
                                        <Pill>Audit-ready activity logs</Pill>
                                        <Pill>Secure session handling</Pill>
                                    </div>

                                    <div className="dv-quote">
                                        <strong>Tip:</strong> Use a strong password and enable 2FA in settings for maximum protection.
                                    </div>
                                </div>
                            </div>

                            <div className="dv-footer">
                                <span>© {year} DeVision</span>
                                <span>Admin UI v1.0</span>
                            </div>
                        </div>

                        {/* Right / Form */}
                        <div className="dv-panel dv-form">
                            <div className="dv-blob dv-blob1 dv-formBlob1" aria-hidden="true" />
                            <div className="dv-blob dv-blob2 dv-formBlob2" aria-hidden="true" />

                            <header className="dv-formHeader">
                                <div>
                                    <h2>Admin Login</h2>
                                    <p>Enter your credentials to access the dashboard.</p>
                                </div>
                                <span className="dv-badge">Secure Area</span>
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
                                                type={showPw ? "text" : "password"}
                                                placeholder="••••••••••••"
                                                autoComplete="current-password"
                                                minLength={6}
                                                required
                                                className="dv-passwordInput"
                                            />
                                            <button
                                                type="button"
                                                onClick={() => setShowPw((s) => !s)}
                                                className="dv-rightBtn"
                                                aria-label={showPw ? "Hide password" : "Show password"}
                                            >
                                                {showPw ? "Hide" : "Show"}
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
                                    <button type="submit" className="dv-btn dv-btnPrimary" disabled={loading}>
                                        <IconArrow />
                                        {loading ? "Signing in..." : "Sign in"}
                                    </button>

                                    <div className="dv-divider">
                                        <span />
                                        <span>or</span>
                                        <span />
                                    </div>

                                    <button
                                        type="button"
                                        className="dv-btn dv-btnGhost"
                                        onClick={() => alert("Optional: Connect SSO (Okta/Microsoft/Google).")}
                                    >
                                        <IconShield />
                                        Continue with SSO
                                    </button>

                                    {status.msg ? (
                                        <div
                                            className={`dv-status ${status.type === "ok" ? "ok" : "err"}`}
                                            role="status"
                                            aria-live="polite"
                                        >
                                            {status.msg}
                                        </div>
                                    ) : null}

                                    <div className="dv-foot">
                                        By continuing, you agree to DeVision’s Admin policies and security requirements.
                                    </div>
                                </div>
                            </form>
                        </div>
                    </section>
                </main>
            </div>
        </>
    )
}