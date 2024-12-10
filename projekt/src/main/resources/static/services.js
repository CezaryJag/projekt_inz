document.addEventListener('DOMContentLoaded', () => {
    const addCarBtn = document.getElementById('add-car-btn');
    const addCarModal = document.getElementById('add-car-modal');
    const closeAddCar = document.getElementById('close-add-car');
    const addCarForm = document.getElementById('add-car-form');
    const editCarModal = document.getElementById('edit-car-modal');
    const closeEditCar = document.getElementById('close-edit-car');
    const editCarForm = document.getElementById('edit-car-form');
    const carList = document.querySelector('#car-list tbody');

    let currentEditCarId = null;

    // Fetch options for car models, car types, gearbox types, gear counts, and fuel types
    async function fetchOptions() {
        try {
            const [modelsResponse, gearboxTypesResponse, gearCountsResponse, fuelTypesResponse] = await Promise.all([
                fetch('/api/car_models'),
                fetch('/api/gearbox1'),
                fetch('/api/gearbox2'),
                fetch('/api/fuel_types')
            ]);

            if (!modelsResponse.ok || !gearboxTypesResponse.ok || !gearCountsResponse.ok || !fuelTypesResponse.ok) {
                throw new Error('One or more endpoints returned 404');
            }

            const [models, gearboxTypes, gearCounts, fuelTypes] = await Promise.all([
                modelsResponse.json(),
                gearboxTypesResponse.json(),
                gearCountsResponse.json(),
                fuelTypesResponse.json()
            ]);

            populateSelect('car-model', models, 'model_id', 'model_name');
            populateSelect('car-type', models, 'model_id', 'car_type');
            populateSelect('gearbox-type', gearboxTypes, 'gearbox_id', 'gear_type');
            populateSelect('gear-count', gearCounts, 'gearbox_id', 'gear_count');
            populateSelect('fuel-type', fuelTypes, 'fuel_id', 'fuel_type');
            populateSelect('edit-car-model', models, 'model_id', 'model_name');
            populateSelect('edit-car-type', models, 'model_id', 'car_type');
            populateSelect('edit-gearbox-type', gearboxTypes, 'gearbox_id', 'gear_type');
            populateSelect('edit-gear-count', gearCounts, 'gearbox_id', 'gear_count');
            populateSelect('edit-fuel-type', fuelTypes, 'fuel_id', 'fuel_type');
        } catch (error) {
            console.error('Error fetching options:', error);
        }
    }

    function populateSelect(selectId, options, valueKey, textKey) {
        const select = document.getElementById(selectId);
        if (!Array.isArray(options)) {
            console.error(`Expected an array for ${selectId}, but got:`, options);
            return;
        }
        select.innerHTML = options.map(option => `<option value="${option[valueKey]}">${option[textKey]}</option>`).join('');
    }

    // Open add modal
    addCarBtn.addEventListener('click', () => {
        addCarModal.style.display = 'flex';
    });

    // Close add modal
    closeAddCar.addEventListener('click', () => {
        addCarModal.style.display = 'none';
    });

    // Close edit modal
    closeEditCar.addEventListener('click', () => {
        editCarModal.style.display = 'none';
    });

    // Add new car
    addCarForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        // Get form data
        const carData = {
            modelId: document.getElementById('car-model').value,
            carType: document.getElementById('car-type').value,
            productionYear: parseInt(document.getElementById('car-year').value),
            registrationNumber: document.getElementById('car-registration').value,
            gearboxTypeId: document.getElementById('gearbox-type').value,
            gearCountId: document.getElementById('gear-count').value,
            fuelTypeId: document.getElementById('fuel-type').value,
            maintenanceDate: document.getElementById('maintenanceDate').value,
            maintenanceDetails: document.getElementById('maintenanceDetails').value,
            policyNumber: document.getElementById('policyNumber').value,
            insuranceCompany: document.getElementById('insuranceCompany').value
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

    // Edit car
    editCarForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        // Get form data
        const carData = {
            modelId: document.getElementById('edit-car-model').value,
            carType: document.getElementById('edit-car-type').value,
            productionYear: parseInt(document.getElementById('edit-car-year').value),
            registrationNumber: document.getElementById('edit-car-registration').value,
            gearboxTypeId: document.getElementById('edit-gearbox-type').value,
            gearCountId: document.getElementById('edit-gear-count').value,
            fuelTypeId: document.getElementById('edit-fuel-type').value,
            maintenanceDate: document.getElementById('edit-maintenanceDate').value,
            maintenanceDetails: document.getElementById('edit-maintenanceDetails').value,
            policyNumber: document.getElementById('edit-policyNumber').value,
            insuranceCompany: document.getElementById('edit-insuranceCompany').value
        };

        try {
            const response = await fetch(`/api/cars/${currentEditCarId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(carData)
            });

            if (response.ok) {
                const updatedCar = await response.json();
                updateCarInTable(updatedCar);
                editCarModal.style.display = 'none';
                editCarForm.reset();
            } else {
                alert('Failed to update car');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while updating the car');
        }
    });

    // Fetch and display cars
    async function fetchCars() {
        try {
            const response = await fetch('/api/cars');
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const cars = await response.json();
            if (!Array.isArray(cars)) {
                throw new TypeError('Expected an array of cars');
            }
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
            <td>
                <button class="edit-btn" data-id="${car.vehicleId}">Edytuj</button>
                <button class="remove-btn" data-id="${car.vehicleId}">Usuń</button>
            </td>
        `;
        carList.appendChild(row);

        const removeBtn = row.querySelector('.remove-btn');
        removeBtn.addEventListener('click', async () => {
            try {
                const response = await fetch(`/api/cars/${car.vehicleId}`, {
                    method: 'DELETE'
                });

                if (response.ok) {
                    carList.removeChild(row);
                } else {
                    alert('Failed to delete car');
                }
            } catch (error) {
                console.error('Error:', error);
                alert('An error occurred while deleting the car');
            }
        });

        const editBtn = row.querySelector('.edit-btn');
        editBtn.addEventListener('click', () => {
            currentEditCarId = car.vehicleId;
            document.getElementById('edit-car-model').value = car.carModel.modelId;
            document.getElementById('edit-car-type').value = car.carModel.carType;
            document.getElementById('edit-car-year').value = car.productionYear;
            document.getElementById('edit-car-registration').value = car.registrationNumber;
            document.getElementById('edit-gearbox-type').value = car.gearboxType.gearboxId;
            document.getElementById('edit-gear-count').value = car.gearCount.gearboxId;
            document.getElementById('edit-fuel-type').value = car.fuelType.fuelId;
            document.getElementById('edit-maintenanceDate').value = car.maintenanceDate;
            document.getElementById('edit-maintenanceDetails').value = car.maintenanceDetails;
            document.getElementById('edit-policyNumber').value = car.policyNumber;
            document.getElementById('edit-insuranceCompany').value = car.insuranceCompany;
            editCarModal.style.display = 'flex';
        });
    }

    function updateCarInTable(car) {
        const rows = carList.querySelectorAll('tr');
        rows.forEach(row => {
            if (row.querySelector('.edit-btn').dataset.id == car.vehicleId) {
                row.innerHTML = `
                    <td>${car.carModel.modelName}</td>
                    <td>${car.productionYear}</td>
                    <td>${car.registrationNumber}</td>
                    <td>
                        <button class="edit-btn" data-id="${car.vehicleId}">Edytuj</button>
                        <button class="remove-btn" data-id="${car.vehicleId}">Usuń</button>
                    </td>
                `;
            }
        });
    }

    fetchOptions();
    fetchCars();
});