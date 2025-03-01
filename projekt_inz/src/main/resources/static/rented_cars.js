document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('authToken');
    const rentedCarList = document.getElementById('rented-car-list').querySelector('tbody');
    const rentModal = document.getElementById('rent-modal');
    const closeRentModal = document.getElementById('close-rent-modal');
    const rentStartInput = document.getElementById('rent-start');
    const rentEndInput = document.getElementById('rent-end');
    const confirmRentBtn = document.getElementById('confirm-rent');

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
                displayRentedCars(rentedCars);
            } else {
                alert('Failed to fetch rented cars.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while fetching rented cars');
        }
    }

    function displayRentedCars(cars) {
        rentedCarList.innerHTML = '';
        cars.forEach(car => {
            const row = document.createElement('tr');
            row.dataset.carId = car.vehicleId;
            row.innerHTML = `
            <td>${car.car ? car.car.carModel.modelName : 'Unknown Model'}</td>
            <td>${car.car ? car.car.registrationNumber : 'Unknown Registration'}</td>
            <td>${new Date(car.rentDate).toLocaleDateString()}</td>
            <td>${new Date(car.rentEndDate).toLocaleDateString()}</td>
            <td>
                <button class="extend-rent-btn" data-id="${car.vehicleId}">Przedłuż</button>
                <button class="cancel-rent-btn" data-id="${car.vehicleId}">Zrezygnuj</button>
            </td>
        `;
            rentedCarList.appendChild(row);

            const extendRentBtn = row.querySelector('.extend-rent-btn');
            extendRentBtn.addEventListener('click', () => {
                extendRent(car.vehicleId);
            });

            const cancelRentBtn = row.querySelector('.cancel-rent-btn');
            cancelRentBtn.addEventListener('click', () => {
                cancelRent(car.vehicleId);
            });
        });
    }

    async function extendRent(vehicleId) {
        try {
            const response = await fetch(`/api/rented-cars/${vehicleId}/extend`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert('Wypożyczenie przedłużone.');
                fetchRentedCars();
            } else {
                alert('Failed to extend rent.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while extending the rent');
        }
    }

    async function cancelRent(vehicleId) {
        try {
            const response = await fetch(`/api/rented-cars/${vehicleId}/cancel`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                showNotification('Wypożyczenie anulowane.', true);
                fetchRentedCars();
            } else {
                showNotification('Failed to cancel rent.', false);
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('An error occurred while canceling the rent', false);
        }
    }

    function showNotification(message, isSuccess) {
        const notification = document.createElement('div');
        notification.textContent = message;
        notification.style.position = 'fixed';
        notification.style.top = '0';
        notification.style.left = '50%';
        notification.style.transform = 'translateX(-50%)';
        notification.style.backgroundColor = isSuccess ? 'green' : 'red';
        notification.style.color = 'white';
        notification.style.padding = '10px';
        notification.style.zIndex = '1000';
        document.body.appendChild(notification);

        setTimeout(() => {
            document.body.removeChild(notification);
        }, 5000);
    }

    fetchRentedCars();
});