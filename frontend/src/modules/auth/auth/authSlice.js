import { createSlice } from "@reduxjs/toolkit";

const tokenFromStorage =
    localStorage.getItem("access_token") || localStorage.getItem("token"); // fallback
const userFromStorage = localStorage.getItem("user");

const initialState = {
    token: tokenFromStorage || null,
    user: userFromStorage ? JSON.parse(userFromStorage) : null,
    status: "idle",
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

            //  store in the new key
            localStorage.setItem("access_token", token);
            localStorage.setItem("user", JSON.stringify(user));

            //  cleanup legacy
            localStorage.removeItem("token");
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

            localStorage.removeItem("access_token");
            localStorage.removeItem("token"); // legacy cleanup
            localStorage.removeItem("user");
        },
    },
});

export const { authStart, authSuccess, authFail, logout } = authSlice.actions;
export default authSlice.reducer;
