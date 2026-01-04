// src/components/AuthInitializer.jsx
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { authSuccess } from '../modules/auth/auth/authSlice';

function AuthInitializer() {
    const dispatch = useDispatch();

    useEffect(() => {
        const token = localStorage.getItem('token');
        const userStr = localStorage.getItem('user');

        if (token && userStr) {
            try {
                const user = JSON.parse(userStr);
                dispatch(authSuccess({ token, user }));
                console.log("Auth restored from localStorage:", user.fullName);
            } catch (e) {
                console.error("Invalid data in localStorage");
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
        }
    }, [dispatch]);

    return null; // renders nothing
}

export default AuthInitializer;