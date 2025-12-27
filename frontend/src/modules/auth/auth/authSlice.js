import { createSlice } from "@reduxjs/toolkit";

const tokenFromStorage = localStorage.getItem("token");
const userFromStorage = localStorage.getItem("user");

const initialState = {
    token: tokenFromStorage || null,
    user: userFromStorage ? JSON.parse(userFromStorage) : null,
    status: "idle", // idle | loading | succeeded | failed
    error: null,
};

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        authStart: (state) => {
            state.status = "loading";
            state.error = null;
        },
        authSuccess: (state, action) => {
            const { token, user } = action.payload;

            state.token = token;
            state.user = user;
            state.status = "succeeded";
            state.error = null;

            localStorage.setItem("token", token);
            localStorage.setItem("user", JSON.stringify(user));
        },
        authFail: (state, action) => {
            state.status = "failed";
            state.error = action.payload || "Something went wrong";
        },
        logout: (state) => {
            state.token = null;
            state.user = null;
            state.status = "idle";
            state.error = null;

            localStorage.removeItem("token");
            localStorage.removeItem("user");
        },
    },
});

export const { authStart, authSuccess, authFail, logout } = authSlice.actions;
export default authSlice.reducer;
