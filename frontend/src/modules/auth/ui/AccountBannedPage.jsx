import React from "react";

export default function AccountBannedPage() {
    const handleBackToLogin = () => {

        window.location.href = "/login";
    };
    const handleSupport = () => {
        window.location.href = "https://www.facebook.com/dat.11.09.01/";
    };
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
            <div className="w-full max-w-lg rounded-2xl bg-white shadow-lg border border-gray-200 p-6 sm:p-8">
                {/* Icon */}
                <div className="flex items-center justify-center">
                    <div className="h-14 w-14 rounded-full bg-red-50 flex items-center justify-center">
                        <svg
                            className="h-7 w-7 text-red-600"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                            strokeWidth={2}
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                d="M12 9v4m0 4h.01M10.29 3.86l-8.1 14.04A2 2 0 003.93 21h16.14a2 2 0 001.74-3.1l-8.1-14.04a2 2 0 00-3.42 0z"
                            />
                        </svg>
                    </div>
                </div>

                {/* Title */}
                <h1 className="mt-5 text-center text-2xl font-semibold text-gray-900">
                    Account Banned
                </h1>

                {/* Description */}
                <p className="mt-3 text-center text-gray-600 leading-relaxed">
                    Your account has been banned and you can’t access the application right now.
                    If you believe this is a mistake, you can contact support to appeal.
                </p>

                {/* Info box */}
                <div className="mt-6 rounded-xl border border-gray-200 bg-gray-50 p-4">
                    <p className="text-sm text-gray-700">
                        <span className="font-medium">What you can do:</span>
                    </p>
                    <ul className="mt-2 list-disc pl-5 text-sm text-gray-600 space-y-1">
                        <li>Review your email for any ban-related message.</li>
                        <li>Contact support to appeal or request more information.</li>
                        <li>Return to login to switch accounts.</li>
                    </ul>
                </div>

                {/* Actions */}
                <div className="mt-7 flex flex-col sm:flex-row gap-3">
                    <button
                        onClick={handleBackToLogin}
                        className="w-full rounded-xl bg-gray-900 px-4 py-3 text-white font-medium hover:bg-gray-800 transition"
                    >
                        Back to Login
                    </button>

                    <button
                        onClick={handleSupport}
                        className="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 text-gray-800 font-medium hover:bg-gray-50 transition"
                    >
                        Contact Support
                    </button>
                </div>

                {/* Footer */}
                <p className="mt-6 text-center text-xs text-gray-500">
                    If you continue to see this page, please reach out to support with your account email.
                </p>
            </div>
        </div>
    );
}
