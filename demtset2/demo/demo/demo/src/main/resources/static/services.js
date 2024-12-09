document.addEventListener('DOMContentLoaded', () => {
    const addCarBtn = document.getElementById('add-car-btn');
    const addCarModal = document.getElementById('add-car-modal');
    const closeAddCar = document.getElementById('close-add-car');
    const addCarForm = document.getElementById('add-car-form');
    const carList = document.querySelector('#car-list tbody');

    // Open modal
    addCarBtn.addEventListener('click', () => {
        addCarModal.style.display = 'flex';
    });

    // Close modal
    closeAddCar.addEventListener('click', () => {
        addCarModal.style.display = 'none';
    });

    // Add new car
    addCarForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        // Get form data
        const modelName = document.getElementById('car-model').value;
        const productionYear = document.getElementById('car-year').value;
        const registrationNumber = document.getElementById('car-registration').value;

        const carData = {
            carModel: { modelName },
            productionYear: parseInt(productionYear),
            registrationNumber
        };

        try {
            const response = await fetch('/api/cars', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(carData)
            });

            if (response.ok) {
                const newCar = await response.json();
                addCarToTable(newCar);
                addCarModal.style.display = 'none';
                addCarForm.reset();
            } else {
                alert('Failed to add car');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while adding the car');
        }
    });

    // Fetch and display cars
    async function fetchCars() {
        try {
            const response = await fetch('/api/cars');
            const cars = await response.json();
            cars.forEach(addCarToTable);
        } catch (error) {
            console.error('Error:', error);
        }
    }

    function addCarToTable(car) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${car.carModel.modelName}</td>
            <td>${car.productionYear}</td>
            <td>${car.registrationNumber}</td>
            <td><button class="remove-btn">Usuń</button></td>
        `;
        carList.appendChild(row);

        const removeBtn = row.querySelector('.remove-btn');
        removeBtn.addEventListener('click', () => {
            carList.removeChild(row);
        });
    }

    fetchCars();
});