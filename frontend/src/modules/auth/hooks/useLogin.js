
import { useDispatch, useSelector } from "react-redux";
import { authService } from "../services/authService.js";
import { authStart, authSuccess, authFail } from "../auth/authSlice.js";
import {useNavigate} from "react-router-dom";

export function useLogin() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const auth = useSelector((state) => state.auth);

    const login = async (payload) => {
        try {
            dispatch(authStart());

            const data = await authService.login(payload);

            // Backend AuthResponse mapping
            const token = data.token;
            const user = {
                userId: data.userId,
                applicantId: data.applicantId,
                email: data.email,
                fullName: data.fullName,
            };

            if (!token) throw new Error("Token missing from login response");

            dispatch(authSuccess({ token, user }));
            return data;
        } catch (err) {
            const status = err?.response?.status;

            if (status === 403) {
                dispatch(authFail("Your account has been banned."));
                navigate("/BannedAccount");
                return;
            }
            const message =
                err?.response?.data?.message ||
                err?.message ||
                "Login failed";
            dispatch(authFail(err.message));
            throw err;
        }
    };

    return {
        login,
        loading: auth.status === "loading",
        error: auth.error,
        token: auth.token,
        user: auth.user,
    };
}
