import {useEffect} from "react";
import {useDispatch} from "react-redux";
import {authSuccess} from "../modules/auth/auth/authSlice.js";

function AuthLoader() {
    const dispatch = useDispatch();

    useEffect(() => {
        const token = localStorage.getItem('token');
        const userStr = localStorage.getItem('user');

        if (token && userStr) {
            try {
                const user = JSON.parse(userStr);
                dispatch(authSuccess({ token, user }));
                console.log("Auth restored:", user.fullName);
            } catch (e) {
                console.error("Invalid stored auth data");
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
        }
    }, [dispatch]);

    return null; // Renders nothing
}

export default AuthLoader;