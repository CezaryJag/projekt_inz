document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('authToken');
    const notificationsList = document.getElementById('notifications-list');

    if (!token) {
        alert('You must be logged in to access this data.');
        window.location.href = 'main.html';
        return;
    }

    async function fetchRentedCars() {
        try {
            const response = await fetch('/api/rented-cars', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const rentedCars = await response.json();
                displayNotifications(rentedCars);
            } else {
                alert('Failed to fetch rented cars.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while fetching rented cars');
        }
    }

    function displayNotifications(cars) {
        notificationsList.innerHTML = '';
        const now = new Date();
        const fiveDaysFromNow = new Date();
        fiveDaysFromNow.setDate(now.getDate() + 5);

        cars.forEach(car => {
            const rentEndDate = new Date(car.rentEndDate);
            if (rentEndDate <= fiveDaysFromNow && rentEndDate >= now) {
                const notification = document.createElement('button');
                notification.className = 'notification';
                notification.innerHTML = `
                <p>Wypożyczenie samochodu o numerze rejestracyjnym ${car.car ? car.car.registrationNumber : 'Unknown Registration'} i modelu ${car.car ? car.car.carModel.modelName : 'Unknown Model'} wygasa dnia ${rentEndDate.toLocaleDateString()}.</p>
            `;
                notification.addEventListener('click', () => {
                    window.location.href = 'rented_cars.html';
                });
                notificationsList.appendChild(notification);
            }
        });
    }

    fetchRentedCars();
});