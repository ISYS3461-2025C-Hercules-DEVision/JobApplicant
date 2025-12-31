import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
// import "bootstrap/dist/css/bootstrap.min.css";
import {
  HomePage,
  LoginPage,
  RegisterPage,
  // Future pages:
  ProfilePage,
  JobListPage,
  // JobDetailPage,
  NotificationPage,
  ApplicationPage,
  // SubscriptionPage,
  SearchProfilePage,
  // DashboardPage,
  // PaymentPage,
  // AdminPage,
    AuthCallback,
} from '../pages';
import AdminLogin from "../modules/admin/ui/AdminLogin.jsx";
import  AdminApplication from "../modules/admin/ui/AdminPages/AdminApplication.jsx";
import CompanyTable from "../modules/admin/ui/AdminPages/CompanyTable.jsx";
import ApplicantTable from "../modules/admin/ui/AdminPages/ApplicantTable.jsx";
import JobPostTable from "../modules/admin/ui/AdminPages/JobPostTable.jsx";
import React from "react";
import AdminDashboard from "../modules/admin/ui/AdminDashboard.jsx";
import AuthLoader from "../components/AuthLoader.jsx";
//import AuthCallback from "../pages/AuthCallback.jsx";

function App() {
  return (
    <Router>
        <AuthLoader/>
      <Routes>
          <Route path="/adminLogin" element={<AdminLogin />} />
          <Route path="/auth/callback" element={<AuthCallback />} />

        {/* Public routes */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
          <Route path="/auth/callback" element={<AuthCallback />}/>
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/jobs" element={<JobListPage />} />
        {/* <Route path="/job/:id" element={<JobDetailPage />} /> */}
        <Route path="/apply/:id" element={<ApplicationPage />} />
        {/* <Route path="/subscription" element={<SubscriptionPage />} /> */}
        <Route path="/searchProfile" element={<SearchProfilePage />} />
        <Route path="/notifications" element={<NotificationPage />} />






          <Route path="/adminDashboard" element={<AdminDashboard />}>
              <Route index element={<ApplicantTable />} />  {/* default right page */}
              <Route path="adminApplicants" element={<ApplicantTable />} />
              <Route path="adminCompanies" element={<CompanyTable />} />
              <Route path="adminApplications" element={<AdminApplication />} />
              <Route path="adminJobs" element={<JobPostTable />} />
          </Route>

          <Route
              path="*"
              element={
                  <div className="p-6">
                      <div className="text-lg font-semibold text-slate-900">Not found</div>
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
