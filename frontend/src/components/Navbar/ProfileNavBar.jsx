import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../../modules/auth/auth/authSlice.js";
import { useEffect, useRef, useState } from "react";
import { useNotifications } from "../../modules/notifications/hooks/useNotifications.js";

function ProfileNavBar() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const fullName = user?.fullName || "User";
  const applicantId = user?.applicantId;

  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);
  const { items, newCount, loading, refresh, markAllSeen } =
    useNotifications(applicantId);

  const handleLogOut = () => {
    dispatch(logout());
    navigate("/login");
  };

  useEffect(() => {
    function onClickOutside(e) {
      if (
        open &&
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target)
      ) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", onClickOutside);
    return () => document.removeEventListener("mousedown", onClickOutside);
  }, [open]);

  const toggleOpen = () => {
    const next = !open;
    setOpen(next);
    if (next) {
      refresh();
      markAllSeen();
    }
  };

  return (
    <header className="border-b-4 border-black bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20">
          <div className="text-2xl font-black text-black tracking-tight">
            DEVision
            <span className="text-black font-bold text-lg px-6">
              Welcome back,
              <span className="text-primary"> {fullName}!</span>
            </span>
          </div>

          <nav className="hidden md:flex items-center space-x-8">
            <Link
              to="/profile"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Home
            </Link>
            <Link
              to="/searchProfile"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Search Profile
            </Link>
            <Link
              to="/jobs"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Jobs
            </Link>
            <Link
              to="/applications"
              className="text-black font-bold uppercase text-sm hover:bg-black hover:text-white px-3 py-2 transition-none"
            >
              Application
            </Link>
            <span className="text-sm font-bold uppercase">VN/EN</span>
          </nav>

          <div className="flex items-center space-x-6">
            <div className="relative" ref={dropdownRef}>
              <button
                aria-label="Notifications"
                onClick={toggleOpen}
                className="relative h-12 w-12 flex items-center justify-center border-4 border-black bg-white hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none"
              >
                <svg
                  className="w-8 h-8"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M15 17h5l-1.4-1.4A2 2 0 0 1 18 14.2V11a6 6 0 1 0-12 0v3.2c0 .5-.2 1-.6 1.4L4 17h5m6 0a3 3 0 1 1-6 0h6z"
                  />
                </svg>
                {newCount > 0 && (
                  <span className="absolute -top-2 -right-2 bg-red-600 text-white text-xs font-bold rounded-full px-2 py-0.5 border-2 border-black">
                    {newCount}
                  </span>
                )}
              </button>

              {open && (
                <div className="absolute right-0 mt-1 w-96 max-h-96 overflow-auto bg-white border-4 border-black shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] z-50">
                  <div className="px-3 py-2 border-b-4 border-black font-bold">
                    Notifications
                  </div>

                  {loading && <div className="p-4 text-sm">Loading...</div>}
                  {!loading && items.length === 0 && (
                    <div className="p-4 text-sm">No notifications yet</div>
                  )}
                  <ul className="divide-y-2 divide-black">
                    {items.map((n) => (
                      <li key={n.id} className="p-3">
                        <div className="flex items-center justify-between">
                          <div className="font-bold">{n.jobTitle}</div>
                          <div className="text-xs text-gray-600">
                            {n.matchedAt
                              ? new Date(n.matchedAt).toLocaleString()
                              : ""}
                          </div>
                        </div>
                        <div className="text-sm">
                          {n.company} • {n.country}
                        </div>
                        {Array.isArray(n.matchedSkills) &&
                          n.matchedSkills.length > 0 && (
                            <div className="mt-1 flex flex-wrap gap-1">
                              {n.matchedSkills.map((s, i) => (
                                <span
                                  key={i}
                                  className="text-xs border-2 border-black px-2 py-0.5 bg-gray-100"
                                >
                                  {s}
                                </span>
                              ))}
                            </div>
                          )}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>

            <button
              onClick={handleLogOut}
              className="bg-primary text-white font-bold uppercase text-sm border-4 border-black px-6 py-3 hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none"
            >
              Log Out
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}

export default ProfileNavBar;
