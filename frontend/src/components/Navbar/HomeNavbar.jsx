import { Link } from "react-router-dom";

function HomeNavbar() {
  return (
    <header className="border-b-4 border-black bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20">
          <div className="text-2xl font-black text-black tracking-tight">
            DEVision
          </div>

          <nav className="hidden md:flex items-center space-x-8">
            <Link
              to="/profile"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Home
            </Link>
            <a
              href="#pricing"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Top Content
            </a>
            <Link
              to="/jobs"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Jobs
            </Link>
            <Link
              to="/apply/:id"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Application
            </Link>

            <span className="text-sm font-bold uppercase">VN | EN</span>
          </nav>

          <div className="flex items-center space-x-4">
            <Link
              to="/register"
              className="text-black font-bold uppercase text-sm border-2 border-black px-4 py-2 hover:bg-black hover:text-white transition-none"
            >
              Get started!
            </Link>
            <Link
              to="/login"
              className="bg-primary text-white font-bold uppercase text-sm border-4 border-black px-6 py-3 hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none"
            >
              SIGN IN
            </Link>
            <div className="w-px h-8 bg-gray-300"></div>
            <Link
              to="/#"
              className="bg-primary text-white font-bold uppercase text-sm border-4 border-black px-6 py-3 hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none"
            >
              HIRE TALENT
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
}

export default HomeNavbar;
