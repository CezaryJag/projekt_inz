document.addEventListener('DOMContentLoaded', () => {
    const addCarBtn = document.getElementById('add-car-btn');
    const addCarModal = document.getElementById('add-car-modal');
    const closeAddCar = document.getElementById('close-add-car');
    const addCarForm = document.getElementById('add-car-form');
    const carList = document.querySelector('#car-list tbody');
    const detailsModal = document.getElementById('details-modal');
    const closeDetails = document.getElementById('close-details');
    const detailsForm = document.getElementById('details-form');
    const createGroupBtn = document.getElementById('create-group-btn');
    const createGroupModal = document.getElementById('create-group-modal');
    const closeCreateGroup = document.getElementById('close-create-group');
    const createGroupForm = document.getElementById('create-group-form');
    const addToGroupBtn = document.getElementById('add-to-group-btn');
    const selectAllCarsCheckbox = document.getElementById('select-all-cars');
    const token = localStorage.getItem('authToken');

    // Open modal
    addCarBtn.addEventListener('click', () => {
        addCarModal.style.display = 'flex';
    });

    // Close modal
    closeAddCar.addEventListener('click', () => {
        addCarModal.style.display = 'none';
    });

    closeDetails.addEventListener('click', () => {
        detailsModal.style.display = 'none';
    });

    // Open create group modal
    createGroupBtn.addEventListener('click', () => {
        createGroupModal.style.display = 'flex';
    });

    // Close create group modal
    closeCreateGroup.addEventListener('click', () => {
        createGroupModal.style.display = 'none';
    });

    // Create new group
    createGroupForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const groupName = document.getElementById('group-name').value;

        try {
            const response = await fetch('/cars/groups', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ groupName })
            });

            if (response.ok) {
                alert('Group created successfully');
                createGroupModal.style.display = 'none';
                createGroupForm.reset();
            } else {
                alert('Failed to create group');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while creating the group');
        }
    });

    // Add selected cars to group
    addToGroupBtn.addEventListener('click', async () => {
        const selectedCarIds = Array.from(document.querySelectorAll('.select-car-checkbox:checked'))
            .map(checkbox => checkbox.dataset.carId);

        if (selectedCarIds.length === 0) {
            alert('No cars selected');
            return;
        }

        const groupId = prompt('Enter the group ID to add the selected cars to:');
        if (!groupId) {
            return;
        }

        try {
            const response = await fetch(`/cars/groups/${groupId}/add-cars`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(selectedCarIds)
            });

            if (response.ok) {
                alert('Cars added to group successfully');
            } else {
                alert('Failed to add cars to group');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while adding cars to the group');
        }
    });

    // Select all cars
    selectAllCarsCheckbox.addEventListener('change', () => {
        const checkboxes = document.querySelectorAll('.select-car-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = selectAllCarsCheckbox.checked;
        });
    });

    // Add new car
    addCarForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        // Validate form data
        if (!validateForm(addCarForm)) {
            return;
        }

        // Get form data
        const carData = getCarFormData(addCarForm);

        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('You must be logged in to add a car.');
            window.location.href = 'main.html';
            return;
        }

        try {
            const response = await fetch('/cars', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(carData)
            });

            if (response.status === 401) {
                alert('Session expired or invalid token. Please log in again.');
                localStorage.removeItem('authToken');
                window.location.href = 'main.html';
                return;
            }
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

    async function fetchColors() {
        try {
            const response = await fetch('/colors');
            if (response.ok) {
                const colors = await response.json();
                populateColorDropdowns(colors);
            } else {
                console.error('Failed to fetch colors');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    function populateColorDropdowns(colors) {
        const colorDropdowns = document.querySelectorAll('#car-color, #details-color, #filter-color');
        colorDropdowns.forEach(dropdown => {
            dropdown.innerHTML = '<option value="">Wybierz kolor</option>';
            colors.forEach(color => {
                const option = document.createElement('option');
                option.value = color.colorName;
                option.textContent = color.colorName;
                dropdown.appendChild(option);
            });
        });
    }

    fetchColors();

    async function fetchCarModels() {
        try {
            const response = await fetch('/car-models');
            if (response.ok) {
                const carModels = await response.json();
                populateCarModelDropdowns(carModels);
            } else {
                console.error('Failed to fetch car models');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    function populateCarModelDropdowns(carModels) {
        const carModelDropdowns = document.querySelectorAll('#car-model, #details-model, #filter-car-model');
        carModelDropdowns.forEach(dropdown => {
            dropdown.innerHTML = '<option value="">Wybierz model</option>';
            carModels.forEach(carModel => {
                const option = document.createElement('option');
                option.value = carModel.modelName;
                option.textContent = carModel.modelName;
                dropdown.appendChild(option);
            });
        });
    }

    fetchCarModels();

    // Fetch and display cars
    async function fetchCars(filters = {}) {
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('You must be logged in to access this data.');
            window.location.href = 'main.html';
            return;
        }
        try {
            // Convert filters to query string
            const queryString = new URLSearchParams(filters).toString();
            const response = await fetch(`/cars/filter?${queryString}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log('Filters applied:', filters);

            if (response.status === 401) {
                alert('Session expired or invalid token. Please log in again.');
                localStorage.removeItem('authToken');
                window.location.href = 'main.html';
                return;
            }

            if (response.ok) {
                const cars = await response.json();
                carList.innerHTML = ''; // Clear the table before adding new rows
                cars.forEach(addCarToTable);
            } else {
                alert('Failed to fetch cars.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while fetching cars');
        }
    }

    function addCarToTable(car) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><input type="checkbox" class="select-car-checkbox" data-car-id="${car.vehicleId}"></td>
            <td>${car.carModel.modelName}</td>
            <td>${car.registrationNumber}</td>
            <td>${car.productionYear}</td>
            <td>${car.status}</td>
            <td>
                <button class="details-btn" data-id="${car.vehicleId}">Details</button>
                <button class="remove-btn" data-id="${car.vehicleId}">Usuń</button>
            </td>
        `;
        carList.appendChild(row);

        const detailsBtn = row.querySelector('.details-btn');
        detailsBtn.addEventListener('click', () => {
            openDetailsModal(car);
        });

        const removeBtn = row.querySelector('.remove-btn');
        removeBtn.addEventListener('click', async () => {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('You must be logged in to add a car.');
                window.location.href = 'main.html';
                return;
            }

            try {
                const response = await fetch(`/cars/${car.vehicleId}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                if (response.status === 401) {
                    alert('Session expired or invalid token. Please log in again.');
                    localStorage.removeItem('authToken');
                    window.location.href = 'main.html';
                    return;
                }
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
    }

    function openDetailsModal(car) {
        document.getElementById('details-id').value = car.vehicleId;
        document.getElementById('details-model').value = car.carModel.modelName;
        document.getElementById('details-year').value = car.productionYear;
        document.getElementById('details-registration').value = car.registrationNumber;
        document.getElementById('details-milage').value = car.milage;
        document.getElementById('details-color').value = car.color.colorName;
        document.getElementById('details-gear-type').value = car.gearType;
        document.getElementById('details-gear-count').value = car.gearCount;
        document.getElementById('details-status').value = car.status;

        if (car.maintenance) {
            document.getElementById('details-maintenance-date').value = formatDate(car.maintenance.maintenanceDate);
            document.getElementById('details-maintenance-end-date').value = formatDate(car.maintenance.maintenanceEndDate);
            document.getElementById('details-maintenance-cost').value = car.maintenance.cost;
            document.getElementById('details-maintenance-details').value = car.maintenance.details;
        } else {
            document.getElementById('details-maintenance-date').value = '';
            document.getElementById('details-maintenance-end-date').value = '';
            document.getElementById('details-maintenance-cost').value = '';
            document.getElementById('details-maintenance-details').value = '';
        }

        detailsModal.style.display = 'flex';
    }

    function formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
    }

    detailsForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        // Validate form data
        if (!validateForm(detailsForm)) {
            return;
        }

        const vehicleId = document.getElementById('details-id').value;
        const carData = getCarFormData(detailsForm);

        try {
            const response = await fetch(`/cars/${vehicleId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(carData)
            });

            if (response.ok) {
                detailsModal.style.display = 'none';
                fetchCars();
            } else {
                alert('Failed to update car');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while updating the car');
        }
    });

    function getCarFormData(form) {
        return {
            carModel: { modelName: form.querySelector('#car-model, #details-model').value },
            productionYear: parseInt(form.querySelector('#car-year, #details-year').value),
            registrationNumber: form.querySelector('#car-registration, #details-registration').value,
            milage: parseInt(form.querySelector('#car-milage, #details-milage').value),
            color: { colorName: form.querySelector('#car-color, #details-color').value },
            gearType: form.querySelector('#car-gear-type, #details-gear-type').value,
            gearCount: parseInt(form.querySelector('#car-gear-count, #details-gear-count').value),
            status: form.querySelector('#car-status, #details-status').value,
            maintenance: {
                maintenanceDate: form.querySelector('#maintenance-date, #details-maintenance-date').value,
                maintenanceEndDate: form.querySelector('#maintenance-end-date, #details-maintenance-end-date').value,
                cost: parseInt(form.querySelector('#maintenance-cost, #details-maintenance-cost').value),
                details: form.querySelector('#maintenance-details, #details-maintenance-details').value
            }
        };
    }

    function validateForm(form) {
        const registrationNumber = form.querySelector('#car-registration, #details-registration').value;
        const productionYear = form.querySelector('#car-year, #details-year').value;
        const maintenanceDate = form.querySelector('#maintenance-date, #details-maintenance-date').value;
        const maintenanceEndDate = form.querySelector('#maintenance-end-date, #details-maintenance-end-date').value;

        if (registrationNumber.length < 5 || registrationNumber.length > 7) {
            alert('Numer rejestracyjny musi mieć od 5 do 7 znaków.');
            return false;
        }

        if (!/^\d{4}$/.test(productionYear)) {
            alert('Rok produkcji musi być 4-cyfrowy.');
            return false;
        }

        if (maintenanceDate && maintenanceEndDate && new Date(maintenanceDate) > new Date(maintenanceEndDate)) {
            alert('Data rozpoczęcia konserwacji nie może być późniejsza niż data zakończenia.');
            return false;
        }

        return true;
    }

    document.getElementById('apply-filters-btn').addEventListener('click', () => {
        const filters = getFilters();
        fetchCars(filters);
    });

    function getFilters() {
        const filters = {};
        const yearFrom = document.getElementById('filter-year-from').value;
        const yearTo = document.getElementById('filter-year-to').value;
        const milageFrom = document.getElementById('filter-milage-from').value;
        const milageTo = document.getElementById('filter-milage-to').value;
        const color = document.getElementById('filter-color').value;
        const status = document.getElementById('filter-status').value;
        const gearType = document.getElementById('filter-gearbox-type').value;
        const gearCount = document.getElementById('filter-gearbox-count').value;
        const carModel = document.getElementById('filter-car-model').value;
        if (yearFrom) filters.yearFrom = yearFrom;
        if (yearTo) filters.yearTo = yearTo;
        if (milageFrom) filters.milageFrom = milageFrom;
        if (milageTo) filters.milageTo = milageTo;
        if (color) filters.color = color;
        if (status) filters.status = status;
        if (gearType) filters.gearboxType = gearType;
        if (gearCount) filters.gearboxCount = gearCount;
        if (carModel) filters.carModel = carModel;

        return filters;
    }

    fetchCars();
});