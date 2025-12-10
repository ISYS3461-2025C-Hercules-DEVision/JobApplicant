document.getElementById("fetchBtn").addEventListener("click", async () => {
    try {
        const response = await fetch("http://localhost:8080/api/greeting");
        const text = await response.text();
        document.getElementById("message").textContent = text;
    } catch (error) {
        document.getElementById("message").textContent = "Could not connect to backend.";
        console.error("Error:", error);
    }
});