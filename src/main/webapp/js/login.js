async function login() {
    const identifier = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const errorEl = document.getElementById('formError');

    function showError(msg) {
        errorEl.textContent = msg;
        errorEl.classList.add('visible');
    }

    errorEl.textContent = '';
    errorEl.classList.remove('visible');

    if (!identifier || !password) {
        showError('Please fill in all fields.');
        return;
    }

    try {
        const response = await fetch('login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                identifier: identifier,
                password: password
            })
        });

        const data = await response.json();

        if (!response.ok) {
            showError(data.message || 'Invalid credentials.');
            return;
        }

        window.location.href = 'view/users.jsp';
    } catch (error) {
        showError('Server error. Please try again.');
        console.error(error);
    }
}