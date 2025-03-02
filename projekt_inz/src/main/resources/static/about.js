document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('login-btn');
    const userMenu = document.getElementById('user-menu');
    const userIcon = document.getElementById('user-icon');
    const userOptions = document.getElementById('user-options');
    const logoutBtn = document.getElementById('logout-btn');

    // Check if the user is logged in
    const token = localStorage.getItem('authToken');
    if (token) {
        loginBtn.style.display = 'none';
        userMenu.style.display = 'block';
    } else {
        loginBtn.style.display = 'block';
        userMenu.style.display = 'none';
    }

    // Toggle user options menu
    userIcon.addEventListener('click', (e) => {
        e.preventDefault();
        userOptions.style.display = userOptions.style.display === 'none' ? 'block' : 'none';
    });

    // Hide user options menu when clicking outside
    document.addEventListener('click', (e) => {
        if (!userOptions.contains(e.target) && e.target !== userIcon) {
            userOptions.style.display = 'none';
        }
    });

    // Handle logout
    logoutBtn.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('authToken');
        window.location.href = 'main.html';
    });
});