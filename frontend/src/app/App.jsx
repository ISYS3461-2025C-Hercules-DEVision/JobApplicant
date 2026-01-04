import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import React from "react";

import {
    HomePage,
    LoginPage,
    RegisterPage,
    ProfilePage,
    JobListPage,
    NotificationPage,
    ApplicationPage,
    SearchProfilePage,
    AuthCallback,
} from "../pages";

import AdminLogin from "../modules/admin/ui/AdminLogin.jsx";
import AdminApplication from "../modules/admin/ui/AdminPages/AdminApplication.jsx";
import CompanyTable from "../modules/admin/ui/AdminPages/CompanyTable.jsx";
import ApplicantTable from "../modules/admin/ui/AdminPages/ApplicantTable.jsx";
import JobPostTable from "../modules/admin/ui/AdminPages/JobPostTable.jsx";
import AdminDashboard from "../modules/admin/ui/AdminDashboard.jsx";

import UserProtectedRoute from "../routes/UserProtectedRoute.jsx";
import AdminProtectedRoute from "../routes/AdminProtectedRoute.jsx";
import AuthInitializer from "../components/AuthInitializer.jsx";

function App() {
    return (
        <Router>
            {/* Handle when reloading page, the redux resets */}
            <AuthInitializer/>

            <Routes>
                {/* Public routes */}
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/jobs" element={<JobListPage />} />
                <Route path="/searchProfile" element={<SearchProfilePage />} />
                <Route path="/auth/callback" element={<AuthCallback />} />

                {/* Admin login is public */}
                <Route path="/adminLogin" element={<AdminLogin />} />

                {/*  Protected USER routes */}
                <Route
                    path="/profile"
                    element={
                        <UserProtectedRoute>
                            <ProfilePage />
                        </UserProtectedRoute>
                    }
                />

                <Route
                    path="/apply/:id"
                    element={
                        <UserProtectedRoute>
                            <ApplicationPage />
                        </UserProtectedRoute>
                    }
                />

                <Route
                    path="/notifications"
                    element={
                        <UserProtectedRoute>
                            <NotificationPage />
                        </UserProtectedRoute>
                    }
                />

                {/*  Protected ADMIN routes */}
                <Route
                    path="/adminDashboard"
                    element={
                        <AdminProtectedRoute>
                            <AdminDashboard />
                        </AdminProtectedRoute>
                    }
                >
                    <Route index element={<ApplicantTable />} />
                    <Route path="adminApplicants" element={<ApplicantTable />} />
                    <Route path="adminCompanies" element={<CompanyTable />} />
                    <Route path="adminApplications" element={<AdminApplication />} />
                    <Route path="adminJobs" element={<JobPostTable />} />
                </Route>

                {/* Not found */}
                <Route
                    path="*"
                    element={
                        <div className="p-6">
                            <div className="text-lg font-semibold text-slate-900">
                                Not found
                            </div>
                            <div className="mt-1 text-sm text-slate-500">
                                The page you are looking for doesn’t exist.
                            </div>
                        </div>
                    }
                />
            </Routes>
        </Router>
    );
}

export default App;
