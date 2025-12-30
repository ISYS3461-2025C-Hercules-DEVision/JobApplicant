const API_BASE = "";

export async function adminLoginService({email,password,remember}){
    const res = await fetch (`${API_BASE}/login`, {
        method: "POST",
        headers: {
            "Content-Type" : "application/json",
        },
        credentials :"include",
        body: JSON.stringify({email,password,remember}),
    });
    if(!res.ok){
        let message = "Invalid Credentials";
        try{
            const err = await res.json();
            message = err?.messages || message;

        }catch{

        }
        throw new Error(message);
    }
    return res.json();
}