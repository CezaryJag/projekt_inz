document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('login-btn');
    const loginModal = document.getElementById('login-modal');
    const closeLoginBtn = document.getElementById('close-login');
    const registerForm = document.getElementById('register-form');
    const loginForm = document.getElementById('login-form');
    const showRegisterLink = document.getElementById('show-register');
    const showLoginLink = document.getElementById('show-login');
    const servicesBtn = document.getElementById('services-btn');

    // Open login modal
    loginBtn.addEventListener('click', () => {
        loginModal.style.display = 'flex';
        loginForm.classList.add('active');
        registerForm.classList.remove('active');
    });

    // Close login modal
    closeLoginBtn.addEventListener('click', () => {
        loginModal.style.display = 'none';
    });

    // Toggle between login and register forms
    showRegisterLink.addEventListener('click', () => {
        loginForm.classList.remove('active');
        registerForm.classList.add('active');
    });

    showLoginLink.addEventListener('click', () => {
        registerForm.classList.remove('active');
        loginForm.classList.add('active');
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;
        const passwordConfirm = document.getElementById('register-password-confirm').value;
        const firstname = document.getElementById('firstname').value;
        const lastname = document.getElementById('lastname').value;
        const phone = document.getElementById('phone').value;

        // Check if passwords match
        if (password !== passwordConfirm) {
            alert('Passwords do not match');
            return;
        }

        const userData = {
            email,
            password,
            name: firstname,
            surname: lastname,
            phone
        };

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            if (response.ok) {
                alert('Registration successful');
                loginForm.classList.add('active');
                registerForm.classList.remove('active');
            } else {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const error = await response.json();
                    alert(error.message || 'An error occurred during registration');
                } else {
                    const error = await response.text();
                    alert(error || 'An error occurred during registration');
                }
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred during registration');
        }
    });

    // Handle login
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('login-email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`/api/auth/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
                method: 'POST',
            });

            if (response.ok) {
                const data = await response.json();
                const token = data.token;

                if (token) {
                    localStorage.setItem('authToken', token);
                    alert('Login successful');
                    loginModal.style.display = 'none';
                }
            } else {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const error = await response.json();
                    alert(error.message || 'Invalid email or password');
                } else {
                    const error = await response.text();
                    alert(error || 'Invalid email or password');
                }
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred during login');
        }
    });

    servicesBtn.addEventListener('click', async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('authToken');
        if (token) {
            try {
                const response = await fetch('/api/auth/verify-token', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (response.ok) {
                    window.location.href = 'services.html';
                } else {
                    alert('Invalid or expired token. Please log in again.');
                    localStorage.removeItem('authToken');
                    const loginModal = document.getElementById('login-modal');
                    const loginForm = document.getElementById('login-form');
                    const registerForm = document.getElementById('register-form');

                    loginModal.style.display = 'flex';
                    loginForm.classList.add('active');
                    registerForm.classList.remove('active');
                    loginForm.reset();
                }
            } catch (error) {
                console.error('Error:', error);
                alert('An error occurred while verifying the token.');
            }
        } else {
            alert('You must be logged in to access this page.');
            const loginModal = document.getElementById('login-modal');
            const loginForm = document.getElementById('login-form');
            const registerForm = document.getElementById('register-form');

            loginModal.style.display = 'flex';
            loginForm.classList.add('active');
            registerForm.classList.remove('active');
            loginForm.reset();
        }
    });
});