import { Link } from 'react-router-dom';
import FooterSection from "../components/Footer/FooterSection";
import HomeNavbar from '../components/Navbar/HomeNavbar';
import LoginForm from "../modules/auth/ui/LoginForm.jsx";

function LoginPage() {
  return (
    <div>
      {/* Header */}
      <HomeNavbar />
        <LoginForm/>
      {/* Footer */}
      <FooterSection />
    </div>
    
  );
}

export default LoginPage;
