import { createSlice } from "@reduxjs/toolkit";

const tokenFromStorage =
    localStorage.getItem("admin_access_token") ||
    sessionStorage.getItem("admin_access_token") ||
    localStorage.getItem("admin_token") ||
    sessionStorage.getItem("admin_token");

const adminFromStorage = localStorage.getItem("admin_user");

const initialState = {
    token: tokenFromStorage || null,
    admin: adminFromStorage ? JSON.parse(adminFromStorage) : null,
    status: "idle",
    error: null,
};

const adminAuthSlice = createSlice({
    name: "adminAuth",
    initialState,
    reducers: {
        adminAuthStart: (state) => {
            state.status = "loading";
            state.error = null;
        },

        adminAuthSuccess: (state, action) => {
            const { token, admin, remember = true } = action.payload;

            state.token = token;
            state.admin = admin;
            state.status = "succeeded";
            state.error = null;

            const storage = remember ? localStorage : sessionStorage;
            storage.setItem("admin_access_token", token);

            if (admin) localStorage.setItem("admin_user", JSON.stringify(admin));

            // legacy cleanup
            localStorage.removeItem("admin_token");
            sessionStorage.removeItem("admin_token");
        },

        adminAuthFail: (state, action) => {
            state.status = "failed";
            state.error = action.payload || "Something went wrong";
        },

        adminLogout: (state) => {
            state.token = null;
            state.admin = null;
            state.status = "idle";
            state.error = null;

            localStorage.removeItem("admin_access_token");
            sessionStorage.removeItem("admin_access_token");
            localStorage.removeItem("admin_user");

            // legacy cleanup
            localStorage.removeItem("admin_token");
            sessionStorage.removeItem("admin_token");
        },
    },
});

export const { adminAuthStart, adminAuthSuccess, adminAuthFail, adminLogout } =
    adminAuthSlice.actions;

export default adminAuthSlice.reducer;
