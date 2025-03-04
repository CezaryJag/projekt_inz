document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('login-btn');
    const loginModal = document.getElementById('login-modal');
    const closeLoginBtn = document.getElementById('close-login');
    const registerForm = document.getElementById('register-form');
    const loginForm = document.getElementById('login-form');
    const resetPasswordForm = document.getElementById('reset-password-form');
    const showRegisterLink = document.getElementById('show-register');
    const showLoginLink = document.getElementById('show-login');
    const servicesBtn = document.getElementById('services-btn');
    const forgotPasswordLink = document.getElementById('show_forgot');
    const backToLoginLink = document.getElementById('back-to-login');
    const servicesDropdown = document.getElementById('services-dropdown');

    // Open login modal
    loginBtn.addEventListener('click', () => {
        loginModal.style.display = 'flex';
        loginForm.classList.add('active');
        registerForm.classList.remove('active');
        resetPasswordForm.classList.remove('active');
    });

    // Close login modal
    closeLoginBtn.addEventListener('click', () => {
        loginModal.style.display = 'none';
    });

    // Toggle between login and register forms
    showRegisterLink.addEventListener('click', () => {
        loginForm.classList.remove('active');
        registerForm.classList.add('active');
        resetPasswordForm.classList.remove('active');
    });

    showLoginLink.addEventListener('click', () => {
        registerForm.classList.remove('active');
        loginForm.classList.add('active');
        resetPasswordForm.classList.remove('active');
    });

    // Open reset password form
    forgotPasswordLink.addEventListener('click', (e) => {
        e.preventDefault();
        loginForm.classList.remove('active');
        resetPasswordForm.classList.add('active');
    });

    // Back to login from reset password form
    backToLoginLink.addEventListener('click', (e) => {
        e.preventDefault();
        resetPasswordForm.classList.remove('active');
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
                const name = data.user.name;
                const surname = data.user.surname;
                const id = data.user.id;
                if (token) {
                    localStorage.setItem('authToken', token);
                    //localStorage.setItem('name',name);
                    localStorage.setItem('name',`${name} ${surname}`);
                    localStorage.setItem('id',`${id}`);
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

    document.querySelector('#sendResetLink').addEventListener('click', function () {
        const email = document.querySelector('#reset-email').value; // Pobiera wartość e-maila

        if (email) { // Sprawdzenie czy email nie jest pusty
            fetch(`/api/auth/reset-password?email=${encodeURIComponent(email)}`, {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        alert('Reset link sent to your email.');
                    } else {
                        alert('An error occurred. Please try again.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred. Please try again.');
                });
        } else {
            alert('Please enter a valid email address.');
        }
    });
});

servicesBtn.addEventListener('click', (event) => {
    event.preventDefault();
    servicesDropdown.style.display = servicesDropdown.style.display === 'none' ? 'block' : 'none';
});

servicesDropdown.addEventListener('click', (event) => {
    if (event.target.tagName === 'A') {
        event.preventDefault(); // Prevent navigation
        const groupId = event.target.dataset.groupId;
        if (groupId === 'main') {
            fetchCars();
        } else {
            fetchCarsByGroup(groupId);
        }
    }
});

async function fetchGroups() {
    try {
        const response = await fetch('/car-groups', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const groups = await response.json();
            populateGroupDropdown(groups);
        } else {
            console.error('Failed to fetch groups');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function populateGroupDropdown(groups) {
    servicesDropdown.innerHTML = '<a href="#" data-group-id="main">Main</a>';
    groups.forEach(group => {
        const link = document.createElement('a');
        link.href = '#';
        link.dataset.groupId = group.groupId;
        link.textContent = group.groupName;
        servicesDropdown.appendChild(link);
    });
}

fetchGroups();