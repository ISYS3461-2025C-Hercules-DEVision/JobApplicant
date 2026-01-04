import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../../modules/auth/auth/authSlice.js";
import authAdminReducer from "../../modules/admin/AdminAuth/AdminAuthSlice.js";

export const store = configureStore({
    reducer: {
        auth: authReducer,
        adminAuth: authAdminReducer,
    },
});