import {useMemo, useState} from "react";
import {adminLoginService} from "../../services/AdminLoginService/AdminLoginService.jsx";

export default function useAdminLogin(){
    const [email,setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [remember,setRemember] = useState(true);
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [status, setStatus] = useState({type: "", msg: ""});

    const year = useMemo(()=> new Date().getFullYear(), []);
    const validate  = () => {
        const e = email.trim();
        const p = password.trim();
        if (!e || !/^\S+@\S+\.\S+$/.test(e)) {
            return "Please enter a valid admin email address.";
        }
        if (p.length < 6) {
            return "Password must be at least 6 characters.";
        }
        return "";
    };
    const onSubmit = async (ev) =>{
        ev.preventDefault();
        setStatus({type: "", msg: ""});

        const err = validate();
        if (err){
            setStatus({type: "err", msg:err});
            return;
        }
        setLoading(true);
        try {
            const data = await adminLoginService(email,password,remember);
            //LocalStorage.setItem("admin_token", data.token);
            setStatus({type: "ok", msg:"Login Successful. Redirecting",});

        }catch(e){
            setStatus({type: "err", msg:e.message || "Login Fail, try again",})
        } finally {
            setLoading(false);
        }
    };
    return{year, email,setEmail, password,setPassword, showPassword, setShowPassword, loading, status, onSubmit}
}