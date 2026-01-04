import { Navigate, useLocation } from "react-router-dom";

export default function UserProtectedRoute({ children }) {
    const location = useLocation();

    // User token stored as "token"
    const token = localStorage.getItem("token");

    if (!token) {
        return <Navigate to="/login" replace state={{ from: location }} />;
    }

    return children;
}
