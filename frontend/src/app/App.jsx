import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

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
    SubscriptionPage,
    MockPaymentPage,
    SubscriptionReturnPage,
} from "../pages";

import AdminLogin from "../modules/admin/ui/AdminLogin.jsx";
import AdminDashboard from "../modules/admin/ui/AdminDashboard.jsx";

import ApplicationTable from "../modules/admin/ui/AdminPages/ApplicationTable.jsx";
import CompanyTable from "../modules/admin/ui/AdminPages/CompanyTable.jsx";
import ApplicantTable from "../modules/admin/ui/AdminPages/ApplicantTable.jsx";
import JobPostTable from "../modules/admin/ui/AdminPages/JobPostTable.jsx";

import AccountBannedPage from "../modules/auth/ui/AccountBannedPage.jsx";
import AuthInitializer from "../components/AuthInitializer.jsx";

import UserProtectedRoute from "../routes/UserProtectedRoute.jsx";
import AdminProtectedRoute from "../routes/AdminProtectedRoute.jsx";

function NotFound() {
    return (
        <div className="p-6">
            <div className="text-lg font-semibold text-slate-900">Not found</div>
            <div className="mt-1 text-sm text-slate-500">
                The page you are looking for doesn’t exist.
            </div>
        </div>
    );
}

export default function App() {
    return (
        <Router>
            {/* Handle when reloading page, the redux resets */}
            <AuthInitializer />

            <Routes>
                {/* Public routes */}
                <Route path="/" element={<HomePage />} />
                <Route path="/bannedAccount" element={<AccountBannedPage />} />
                {/* optional alias to keep old URL working */}
                <Route path="/BannedAccount" element={<AccountBannedPage />} />

                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/jobs" element={<JobListPage />} />
                <Route path="/searchProfile" element={<SearchProfilePage />} />
                <Route path="/auth/callback" element={<AuthCallback />} />

                <Route path="/subscription" element={<SubscriptionPage />} />
                <Route path="/subscription/return" element={<SubscriptionReturnPage />} />
                <Route path="/payment/mock" element={<MockPaymentPage />} />

                {/* Admin login is public */}
                <Route path="/adminLogin" element={<AdminLogin />} />

                {/* Protected USER routes (requires UserProtectedRoute to return <Outlet /> when no children) */}
                <Route element={<UserProtectedRoute />}>
                    <Route path="/profile" element={<ProfilePage />} />
                    <Route path="/apply/:id" element={<ApplicationPage />} />
                    <Route path="/notifications" element={<NotificationPage />} />
                    <Route path="/applications" element={<ApplicationPage />} />
                </Route>

                {/* Protected ADMIN routes (requires AdminProtectedRoute to return <Outlet /> when no children) */}
                <Route path="/adminDashboard" element={<AdminProtectedRoute />}>
                    <Route index element={<ApplicantTable />} />
                    <Route path="adminApplicants" element={<ApplicantTable />} />
                    <Route path="adminCompanies" element={<CompanyTable />} />
                    <Route path="adminApplications" element={<ApplicationTable />} />
                    <Route path="adminJobs" element={<JobPostTable />} />
                </Route>

                {/* Not found */}
                <Route path="*" element={<NotFound />} />
            </Routes>
        </Router>
    );
}
