document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('login-btn');
    const loginModal = document.getElementById('login-modal');
    const closeLoginBtn = document.getElementById('close-login');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const showRegisterLink = document.getElementById('show-register');
    const showLoginLink = document.getElementById('show-login');

    // Show login modal
    loginBtn.addEventListener('click', () => {
        loginModal.style.display = 'flex';
        loginForm.classList.add('active');
        registerForm.classList.remove('active');
    });

    // Close login modal
    closeLoginBtn.addEventListener('click', () => {
        loginModal.style.display = 'none';
    });

    // Close modal on outside click
    window.addEventListener('click', (e) => {
        if (e.target === loginModal) {
            loginModal.style.display = 'none';
        }
    });

    // Show register form
    showRegisterLink.addEventListener('click', (e) => {
        e.preventDefault();
        loginForm.classList.remove('active');
        registerForm.classList.add('active');
    });

    // Show login form
    showLoginLink.addEventListener('click', (e) => {
        e.preventDefault();
        registerForm.classList.remove('active');
        loginForm.classList.add('active');
    });
});
document.getElementById('register-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const user = {
        name: document.getElementById('firstname').value,
        surname: document.getElementById('lastname').value,
        email: document.getElementById('register-login').value,
        password: document.getElementById('register-password').value,
        role: "user", // Domyślna rola
        phoneNumber: document.getElementById('phone').value
    };

    const response = await fetch('http://localhost:8080/api/users/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    });

    const result = await response.text();
    alert(result);
});

document.getElementById('login-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const email = document.getElementById('login').value;
    const password = document.getElementById('password').value;

    const response = await fetch(`/api/users/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
        method: 'POST'
    });

    if (response.ok) {
        const user = await response.json();
        alert(`Witaj, ${user.name}!`);
    } else {
        alert('Błędny email lub hasło.');
    }
});

