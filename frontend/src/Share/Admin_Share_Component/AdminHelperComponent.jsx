/* ---------- small helpers ---------- */
function Field({ label, children }) {
    return (
        <div className="dv-field">
            <label className="dv-label">{label}</label>
            {children}
        </div>
    );
}

function Pill({ children }) {
    return (
        <div className="dv-pill">
            <span className="dv-dot" aria-hidden="true" />
            {children}
        </div>
    );
}

/* ---------- icons ---------- */
function IconMail() {
    return (
        <svg className="dv-icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path
                d="M4 7.5a3 3 0 0 1 3-3h10a3 3 0 0 1 3 3v9a3 3 0 0 1-3 3H7a3 3 0 0 1-3-3v-9Z"
                stroke="rgba(255,255,255,.75)"
                strokeWidth="1.6"
            />
            <path
                d="M6 8l6 5 6-5"
                stroke="rgba(255,255,255,.75)"
                strokeWidth="1.6"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </svg>
    );
}

function IconLock() {
    return (
        <svg className="dv-icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path
                d="M7.5 10V8.2A4.5 4.5 0 0 1 12 3.7a4.5 4.5 0 0 1 4.5 4.5V10"
                stroke="rgba(255,255,255,.75)"
                strokeWidth="1.6"
                strokeLinecap="round"
            />
            <path
                d="M6.8 10h10.4A2.8 2.8 0 0 1 20 12.8v5.4A2.8 2.8 0 0 1 17.2 21H6.8A2.8 2.8 0 0 1 4 18.2v-5.4A2.8 2.8 0 0 1 6.8 10Z"
                stroke="rgba(255,255,255,.75)"
                strokeWidth="1.6"
            />
        </svg>
    );
}

function IconArrow() {
    return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M5 12h12" stroke="rgba(0,0,0,.75)" strokeWidth="2" strokeLinecap="round" />
            <path
                d="M13 6l6 6-6 6"
                stroke="rgba(0,0,0,.75)"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </svg>
    );
}

function IconShield() {
    return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path
                d="M12 3l7 4v6c0 5-3 8-7 9-4-1-7-4-7-9V7l7-4Z"
                stroke="rgba(255,255,255,.8)"
                strokeWidth="1.8"
                strokeLinejoin="round"
            />
            <path
                d="M9.5 12l1.7 1.7L14.8 10"
                stroke="rgba(255,255,255,.8)"
                strokeWidth="1.8"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </svg>
    );
}

export { Field, Pill, IconMail, IconLock, IconShield, IconArrow };