import React from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";

export default function AdminProtectedRoute({ children }) {
    const location = useLocation();

    const token =
        localStorage.getItem("admin_access_token") ||
        sessionStorage.getItem("admin_access_token");

    if (!token) {
        return <Navigate to="/adminLogin" replace state={{ from: location }} />;
    }

    // If used as wrapper: <AdminProtectedRoute><Comp/></AdminProtectedRoute>
    // If used as route element with nested routes: <Route element={<AdminProtectedRoute/>}>...</Route>
    return children ?? <Outlet />;
}
