import { Navigate, useLocation } from "react-router-dom";

export default function AdminProtectedRoute({ children }) {
    const location = useLocation();

    // Admin token stored as "admin_token"
    const token =
        localStorage.getItem("admin_access_token") || sessionStorage.getItem("admin_access_token");

    if (!token) {
        return <Navigate to="/adminLogin" replace state={{ from: location }} />;
    }

    return children;
}
