async function register() {
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!firstName || !lastName || !email || !username || !password) {
        alert("Please fill in all fields.");
        return;
    }

    const userData = {
        firstName: firstName,
        lastName: lastName,
        userName: username,
        email: email,
        password: password
    };

    try {
        const response = await fetch("", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        const result = await response.text();

        if (response.ok) {
            alert(result);
            window.location.href = "index.html";
        } else {
            alert("Error: " + result);
        }
    } catch (error) {
        console.error("Registration error:", error);
        alert("Could not connect to the server.");
    }
}