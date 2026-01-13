import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import FooterSection from "../components/Footer/FooterSection";
import HomeNavbar from '../components/Navbar/HomeNavbar';
import ProfileNavBar from "../components/Navbar/ProfileNavBar.jsx";
import {useSelector} from "react-redux";


function HomePage() {
  const [displayText, setDisplayText] = useState('');
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isDeleting, setIsDeleting] = useState(false);
  
  const words = ['Opportunities.', 'Roles.', 'Companies.', 'Careers.'];
  const {token} = useSelector((state) => state.auth);
  
  useEffect(() => {
    const currentWord = words[currentIndex];
    const timeout = setTimeout(() => {
      if (!isDeleting) {
        setDisplayText(currentWord.substring(0, displayText.length + 1));
        
        if (displayText === currentWord) {
          setTimeout(() => setIsDeleting(true), 2000);
        }
      } else {
        setDisplayText(currentWord.substring(0, displayText.length - 1));
        
        if (displayText === '') {
          setIsDeleting(false);
          setCurrentIndex((currentIndex + 1) % words.length);
        }
      }
    }, isDeleting ? 50 : 150);
    
    return () => clearTimeout(timeout);
  }, [displayText, isDeleting, currentIndex]);

  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      {token ? <ProfileNavBar /> : <HomeNavbar/> }
      
      {/* Hero Section */}
      <section className="bg-light-gray border-b-4 border-black py-20 lg:py-32">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* <div className="border-4 border-black bg-white p-6 mb-8 inline-block">
            <p className="text-sm font-black uppercase tracking-wider">For Recruiters & Companies</p>
          </div> */}
          
          <h1 className="text-5xl md:text-6xl lg:text-7xl font-black text-black mb-6 leading-none uppercase">
            FIND THE RIGHT JOB.  
            <br />
            <span className="text-primary">{displayText}<span className="animate-pulse">|</span></span>
          </h1>
          
          <p className="text-xl md:text-2xl font-bold text-black mb-10 max-w-3xl border-l-8 border-black pl-6 leading-tight">
            Discover job opportunities that match your skills, experience, and career goals.
          </p>
          
          <div className="flex flex-wrap gap-6">
            <Link 
              to="/register" 
              className="bg-primary text-white font-black uppercase text-lg border-4 border-black px-10 py-4 hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] transition-none"
            >
              Search Jobs →
            </Link>
            <a 
              href="#pricing" 
              className="bg-white text-black font-black uppercase text-lg border-4 border-black px-10 py-4 hover:bg-black hover:text-white transition-none"
            >
              View Plan
            </a>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 border-b-4 border-black">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="border-4 border-black bg-white p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none">
              <div className="w-20 h-20 mb-6 flex items-center justify-center border-4 border-black bg-light-gray">
                <svg className="w-12 h-12" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                  <path d="M12 2v20M2 12h20"/>
                </svg>
              </div>
              <h3 className="text-2xl font-black text-black mb-4 uppercase">Smart Job Search</h3>
              <p className="text-black font-bold leading-relaxed">
                Find jobs using filters for skills, salary, employment type, and more.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="border-4 border-black bg-white p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none">
              <div className="w-20 h-20 mb-6 flex items-center justify-center border-4 border-black bg-light-gray">
                <svg className="w-12 h-12" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
              </div>
              <h3 className="text-2xl font-black text-black mb-4 uppercase">Easy Applications</h3>
              <p className="text-black font-bold leading-relaxed">
                Create a profile once and apply instantly to multiple job posts.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="border-4 border-black bg-white p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none">
              <div className="w-20 h-20 mb-6 flex items-center justify-center border-4 border-black bg-light-gray">
                <svg className="w-12 h-12" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                  <path d="M9 11l3 3L22 4"/>
                  <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
                </svg>
              </div>
              <h3 className="text-2xl font-black text-black mb-4 uppercase">Real-Time Notifications</h3>
              <p className="text-black font-bold leading-relaxed">
                Premium users get job alerts when new matching roles appear.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section id="pricing" className="py-20 bg-light-gray border-b-4 border-black">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-5xl font-black text-black mb-4 uppercase">Subscription</h2>
            <p className="text-xl font-bold text-black border-4 border-black bg-white inline-block px-8 py-4">
              Upgrade for unlocking smarter tools, faster applications, and real-time job alerts.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8 max-w-5xl mx-auto">
            {/* Starter Plan */}
            <div className="bg-white border-4 border-black p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none">
              <div className="border-b-4 border-black pb-6 mb-6">
                <h3 className="text-2xl font-black text-black mb-2 uppercase">Starter</h3>
                <div className="text-5xl font-black text-primary">$0</div>
              </div>
              <ul className="space-y-4">
                <li className="flex items-start">
                  <span className="text-primary font-black text-2xl mr-3">✓</span>
                  <span className="text-black font-bold">Search Jobs</span>
                </li>
                <li className="flex items-start">
                  <span className="text-primary font-black text-2xl mr-3">✓</span>
                  <span className="text-black font-bold">Apply Instantly</span>
                </li>
                <li className="flex items-start">
                  <span className="text-primary font-black text-2xl mr-3">✓</span>
                  <span className="text-black font-bold">Save Job Posts</span>
                </li>
              </ul>
            </div>

            {/* Growth Plan */}
            <div className="bg-primary border-4 border-black p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none">
              <div className="border-b-4 border-black pb-6 mb-6">
                <h3 className="text-2xl font-black text-white mb-2 uppercase">Growth</h3>
                <div className="text-5xl font-black text-white">$5</div>
              </div>
              <ul className="space-y-4">
                <li className="flex items-start">
                  <span className="text-white font-black text-2xl mr-3">✓</span>
                  <span className="text-white font-bold">Advanced Filters</span>
                </li>
                <li className="flex items-start">
                  <span className="text-white font-black text-2xl mr-3">✓</span>
                  <span className="text-white font-bold">Profile Boost</span>
                </li>
                <li className="flex items-start">
                  <span className="text-white font-black text-2xl mr-3">✓</span>
                  <span className="text-white font-bold">Application Tracking</span>
                </li>
              </ul>
            </div>

            {/* Scale Plan */}
            <div className="bg-white border-4 border-black p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none">
              <div className="border-b-4 border-black pb-6 mb-6">
                <h3 className="text-2xl font-black text-black mb-2 uppercase">Scale</h3>
                <div className="text-5xl font-black text-primary">$15</div>
              </div>
              <ul className="space-y-4">
                <li className="flex items-start">
                  <span className="text-primary font-black text-2xl mr-3">✓</span>
                  <span className="text-black font-bold">Instant Job Alerts </span>
                </li>
                <li className="flex items-start">
                  <span className="text-primary font-black text-2xl mr-3">✓</span>
                  <span className="text-black font-bold">Top Profile Visibility</span>
                </li>
                <li className="flex items-start">
                  <span className="text-primary font-black text-2xl mr-3">✓</span>
                  <span className="text-black font-bold">Priority Application Review</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <FooterSection />
    </div>
  );
}

export default HomePage;
