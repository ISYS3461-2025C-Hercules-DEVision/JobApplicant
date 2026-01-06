import { useDispatch, useSelector } from "react-redux";
import { authService } from "../services/authService.js";
import { authStart, authSuccess, authFail } from "../auth/authSlice.js";
import { useNavigate } from "react-router-dom";

export function useLogin() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const auth = useSelector((state) => state.auth);

    const login = async (payload) => {
        try {
            dispatch(authStart());

            const data = await authService.login(payload);

            // Backend returns accessToken
            const token = data.accessToken;

            const user = {
                userId: data.userId,
                applicantId: data.applicantId,
                email: data.email,
                fullName: data.fullName,
                status: data.status,
            };

            if (!token) throw new Error("Access token missing from login response");

            //  block banned
            if (user.status === false) {
                dispatch(authFail("Your account has been banned."));
                navigate("/BannedAccount", {replace: true});
                return null;
            }

            dispatch(authSuccess({token, user}));
            return data;
        } catch (err) {
            // Your request() throws Error(message) so err.message is what you have
            const msg = err?.message || "Login failed";
            dispatch(authFail(msg));
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