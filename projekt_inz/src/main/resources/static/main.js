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
    const userMenu = document.getElementById('user-menu');
    const userIcon = document.getElementById('user-icon');
    const userOptions = document.getElementById('user-options');
    const logoutBtn = document.getElementById('logout-btn');

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

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('login-email').value;
        const password = document.getElementById('password').value;
        const loginError = document.getElementById('login-error');

        loginError.style.display = 'none'; // Hide previous error

        try {
            const response = await fetch(`/api/auth/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
                method: 'POST',
            });

            if (response.ok) {
                const data = await response.json();
                const token = data.token;
                const name = data.name;
                const surname = data.surname;
                const id = data.id;

                if (token) {
                    localStorage.setItem('authToken', token);
                    localStorage.setItem('name', `${name} ${surname}`);
                    localStorage.setItem('id', `${id}`);
                    loginModal.style.display = 'none';
                    loginBtn.style.display = 'none';
                    userMenu.style.display = 'block';
                }
            } else {
                const error = await response.json();
                loginError.textContent = error.message;
                loginError.style.display = 'block';
            }
        } catch (error) {
            loginError.textContent = 'An error occurred during login';
            loginError.style.display = 'block';
        }
    });

    // Handle logout
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('name');
        localStorage.removeItem('id');
        loginBtn.style.display = 'block';
        userMenu.style.display = 'none';
    });

    // Toggle user options
    userIcon.addEventListener('click', (e) => {
        e.preventDefault();
        userOptions.style.display = userOptions.style.display === 'none' ? 'block' : 'none';
    });

    // Check if user is already logged in
    if (localStorage.getItem('authToken')) {
        loginBtn.style.display = 'none';
        userMenu.style.display = 'block';
    }


    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;
        const passwordConfirm = document.getElementById('register-password-confirm').value;
        const firstname = document.getElementById('firstname').value;
        const lastname = document.getElementById('lastname').value;
        const phone = document.getElementById('phone').value;
        const accessKey = document.getElementById('access-key').value; // Ensure this line is present
        const registerError = document.getElementById('register-error');
        const notification = document.createElement('div');

        registerError.style.display = 'none';

        if (password !== passwordConfirm) {
            registerError.textContent = 'Passwords do not match';
            registerError.style.display = 'block';
            return;
        }

        const userData = {
            email,
            password,
            name: firstname,
            surname: lastname,
            phoneNumber: phone,
            accessKey // Ensure this line is present
        };

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            if (!response.ok) {
                const error = await response.json();
                registerError.textContent = error.message;
                registerError.style.display = 'block';
            }

            if (response.ok) {
                loginModal.style.display = 'none';
                notification.textContent = 'Registration completed, please confirm your email';
                notification.style.position = 'fixed';
                notification.style.top = '0';
                notification.style.left = '50%';
                notification.style.transform = 'translateX(-50%)';
                notification.style.backgroundColor = 'green';
                notification.style.color = 'white';
                notification.style.padding = '10px';
                notification.style.zIndex = '1000';
                document.body.appendChild(notification);

                setTimeout(() => {
                    document.body.removeChild(notification);
                }, 5000);
            }
        } catch (error) {
            console.error('Error:', error);
            registerError.textContent = 'An error occurred during registration';
            registerError.style.display = 'block';
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