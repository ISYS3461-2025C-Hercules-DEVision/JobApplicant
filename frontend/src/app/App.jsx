import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import {
  HomePage,
  LoginPage,
  RegisterPage,
  // Future pages:
  ProfilePage,
  // JobListPage,
  // JobDetailPage,
  // ApplicationPage,
  // DashboardPage,
  // PaymentPage,
  // AdminPage,
} from '../pages';

function App() {
  return (
    <Router>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Future routes */}
        <Route path="/profile" element={<ProfilePage />} />
        {/* <Route path="/jobs" element={<JobListPage />} /> */}
        {/* <Route path="/job/:id" element={<JobDetailPage />} /> */}
        {/* <Route path="/apply/:id" element={<ApplicationPage />} /> */}
        {/* <Route path="/dashboard" element={<DashboardPage />} /> */}
        {/* <Route path="/payment" element={<PaymentPage />} /> */}
        {/* <Route path="/admin" element={<AdminPage />} /> */}
      </Routes>
    </Router>
  );
}

export default App;
