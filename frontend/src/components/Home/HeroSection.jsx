import { Link } from "react-router-dom";
import { useState, useEffect } from "react";

function HeroSection() {
  const [displayText, setDisplayText] = useState('');
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isDeleting, setIsDeleting] = useState(false);

  const words = ['TALENT.', 'Engineers.', 'Developers.', 'Leader.'];

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
    <section className="bg-light-gray border-b-4 border-black py-20 lg:py-32">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="border-4 border-black bg-white p-6 mb-8 inline-block">
          <p className="text-sm font-black uppercase tracking-wider">For Recruiters & Companies</p>
        </div>

        <h1 className="text-5xl md:text-6xl lg:text-7xl font-black text-black mb-6 leading-none uppercase">
          NO MIDDLEMAN.<br />
          <span className="text-primary">{displayText}<span className="animate-pulse">|</span></span>
        </h1>

        <p className="text-xl md:text-2xl font-bold text-black mb-10 max-w-3xl border-l-8 border-black pl-6 leading-tight">
          Directly connect with top developers and tech companies. No fluff, just code and contracts.
        </p>

        <div className="flex flex-wrap gap-6">
          <Link 
            to="/register"
            className="bg-primary text-white font-black uppercase text-lg border-4 border-black px-10 py-4 hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] transition-none"
          >
            Hire Talent →
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
  );
}

export default HeroSection;
