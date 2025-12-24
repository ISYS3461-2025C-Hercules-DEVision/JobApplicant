import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";

export default function LoginForm() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  // Zustand state & actions
  const login = useAuthStore((s) => s.login);
  const loginWithGoogle = useAuthStore((s) => s.googleLogin);
  const loading = useAuthStore((s) => s.loading);
  const error = useAuthStore((s) => s.error);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((p) => ({ ...p, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await login(formData, {
      onSuccess: () => navigate("/"), // dashboard route
    });
  };

  return (
    <div className="min-h-screen bg-light-gray flex items-center justify-center p-4">
      <div className="bg-white border-4 border-black p-10 w-full max-w-md shadow-[8px_8px_0px_0px_rgba(0,0,0,1)]">
        <h1 className="text-4xl font-black text-center text-black mb-8 uppercase">
          Welcome back!
        </h1>

        {error && (
          <div className="border-4 border-black p-3 mb-5 font-bold">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <input
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            className="w-full px-4 py-3 border-4 border-black font-bold"
            required
          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            className="w-full px-4 py-3 border-4 border-black font-bold"
            required
          />

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary text-white font-black py-4 border-4 border-black uppercase disabled:opacity-60"
          >
            {loading ? "Loading..." : "Continue"}
          </button>
        </form>

        <div className="my-6 text-center">
          <Link to="/forgot-password" className="font-bold uppercase text-sm">
            Forgot your password?
          </Link>
        </div>

        <button
          type="button"
          onClick={loginWithGoogle}
          className="w-full border-4 border-black font-bold py-4 uppercase"
        >
          Login with Google
        </button>

        <p className="text-center mt-6 font-bold uppercase text-sm">
          Don't have an account?{" "}
          <Link to="/register" className="underline font-black">
            Sign Up
          </Link>
        </p>
      </div>
    </div>
  );
}

