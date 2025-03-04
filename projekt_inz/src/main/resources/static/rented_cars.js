document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('authToken');
    const rentedCarList = document.getElementById('rented-car-list').querySelector('tbody');
    const extendRentModal = document.getElementById('extend-rent-modal');
    const closeExtendRentModal = document.getElementById('close-extend-rent-modal');
    const currentEndDateInput = document.getElementById('current-end-date');
    const newEndDateInput = document.getElementById('new-end-date');
    const confirmExtendBtn = document.getElementById('confirm-extend');
    let currentVehicleId;
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
                openExtendRentModal(car.vehicleId, car.rentEndDate);
            });
            const cancelRentBtn = row.querySelector('.cancel-rent-btn');
            cancelRentBtn.addEventListener('click', () => {
                cancelRent(car.vehicleId);
            });
        });
    }

    function openExtendRentModal(vehicleId, currentEndDate) {
        currentVehicleId = vehicleId;
        currentEndDateInput.value = new Date(currentEndDate).toISOString().split('T')[0];
        newEndDateInput.value = '';
        extendRentModal.style.display = 'flex';
    }

    closeExtendRentModal.addEventListener('click', () => {
        extendRentModal.style.display = 'none';
    });

    confirmExtendBtn.addEventListener('click', async () => {
        const newEndDate = newEndDateInput.value;
        if (new Date(newEndDate) <= new Date(currentEndDateInput.value)) {
            showNotification('Nowa data zakończenia musi być późniejsza niż obecna data zakończenia.', false);
            return;
        }

        try {
            const response = await fetch(`/api/rented-cars/${currentVehicleId}/extend`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ newEndDate })
            });

            if (response.ok) {
                showNotification('Wypożyczenie przedłużone.', true);
                fetchRentedCars();
                extendRentModal.style.display = 'none';
            } else {
                const errorText = await response.text();
                showNotification(`Failed to extend rent: ${errorText}`, false);
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('An error occurred while extending the rent', false);
        }
    });

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