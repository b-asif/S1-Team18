document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('.register-form');

    if (!form) {
        return;
    }

    form.addEventListener('submit', function (event) {
        const firstName = document.getElementById('firstName').value.trim();
        const lastName = document.getElementById('lastName').value.trim();
        const email = document.getElementById('email').value.trim();
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        ['firstName', 'lastName', 'email', 'username', 'password'].forEach(function (id) {
            const input = document.getElementById(id);
            const error = document.getElementById(id + 'Error');

            if (input) {
                input.classList.remove('field-invalid');
            }

            if (error) {
                error.textContent = '';
                error.classList.remove('visible');
            }
        });

        let valid = true;

        function markError(fieldId, message) {
            const input = document.getElementById(fieldId);
            const error = document.getElementById(fieldId + 'Error');

            if (input) {
                input.classList.add('field-invalid');
            }

            if (error) {
                error.textContent = message;
                error.classList.add('visible');
            }

            valid = false;
        }

        if (!firstName) {
            markError('firstName', 'First name is required.');
        }

        if (!lastName) {
            markError('lastName', 'Last name is required.');
        }

        if (!email) {
            markError('email', 'Email address is required.');
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            markError('email', 'Enter a valid email address.');
        }

        if (!username) {
            markError('username', 'Username is required.');
        }

        if (!password) {
            markError('password', 'Password is required.');
        } else if (password.length < 6) {
            markError('password', 'Password must be at least 6 characters.');
        }

        if (!valid) {
            event.preventDefault();
        }
    });
});