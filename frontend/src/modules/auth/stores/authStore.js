import { create } from "zustand";
import { authService } from "../services/authService";

export const useAuthStore = create((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  loading: false,
  error: null,

  login: async (payload) => {
    set({ loading: true, error: null });

    try {
      const res = await authService.login(payload);

      const token = res?.token || res?.accessToken;
      const user = res?.user || null;

      if (!token) {
        throw new Error("Token not found in response");
      }

      // persist token
      localStorage.setItem("access_token", token);

      set({
        user,
        token,
        isAuthenticated: true,
        loading: false,
      });

      return res; 
    } catch (err) {
      set({
        error: err?.message || "Login failed",
        loading: false,
        isAuthenticated: false,
      });

      throw err;
    }
  },

  register: async (payload) => {
    set({ loading: true, error: null });

    try {
      const res = await authService.register(payload);

      const token = res?.token || res?.accessToken;
      const user = res?.user || null;

      if (!token) {
        throw new Error("Token not found in response");
      }

      localStorage.setItem("access_token", token);

      set({
        user,
        token,
        isAuthenticated: true,
        loading: false,
      });

      return res;
    } catch (err) {
      set({
        error: err?.message || "Register failed",
        loading: false,
        isAuthenticated: false,
      });
      throw err;
    }
  },

  logout: () => {
    localStorage.removeItem("access_token");
    set({
      user: null,
      token: null,
      isAuthenticated: false,
    });
  },
}));
