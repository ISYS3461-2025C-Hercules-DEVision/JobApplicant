import { createSlice } from "@reduxjs/toolkit";

const tokenFromStorage =
    localStorage.getItem("admin_token") || sessionStorage.getItem("admin_token");

const initialState = {
    token: tokenFromStorage || null,
    admin:  null,
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
            const { token, admin } = action.payload;

            state.token = token;
            state.admin = admin;
            state.status = "succeeded";
            state.error = null;
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
            localStorage.removeItem("admin_token");
            sessionStorage.removeItem("admin_token");
        },
    },
});

export const { adminAuthStart, adminAuthSuccess, adminAuthFail, adminLogout } =
    adminAuthSlice.actions;

export default adminAuthSlice.reducer;
