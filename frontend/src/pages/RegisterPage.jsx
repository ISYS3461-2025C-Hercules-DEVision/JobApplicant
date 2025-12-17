import { Link } from "react-router-dom";
import { useState } from "react";
import FooterSection from "../components/Footer/FooterSection";
import HomeNavbar from "../components/Navbar/HomeNavbar";
import RegisterForm from "../modules/auth/ui/RegisterForm.jsx";

function RegisterPage() {
  return (
    <div>
      {/* Header */}
      <HomeNavbar />
        <RegisterForm/>
      {/* Footer */}
      <FooterSection />
    </div>
  );
}

export default RegisterPage;
