function register() {

    let first = document.getElementById("firstName").value;
    let last = document.getElementById("lastName").value;
    let email = document.getElementById("email").value;
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    if (first === "" || last === "" || email === "" || username === "" || password === "") {
        alert("Please fill out all fields");
        return;
    }

    alert("Registration submitted");

    // later:
    // fetch("/register", { ... })
}