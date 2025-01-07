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
    const addToGroupBtn = document.getElementById('add-to-group-btn');
    const createGroupModal = document.getElementById('create-group-modal');
    const closeCreateGroup = document.getElementById('close-create-group');
    const createGroupForm = document.getElementById('create-group-form');
    const servicesBtn = document.getElementById('services-btn');
    const servicesDropdown = document.getElementById('services-dropdown');
    const token = localStorage.getItem('authToken');
    const groupSelect = document.getElementById('group-select');

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

    closeCreateGroup.addEventListener('click', () => {
        createGroupModal.style.display = 'none';
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
                alert('Failed to add car.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while adding the car');
        }
    });

    function displayGroups(groups) {
        const groupList = document.getElementById('group-list').querySelector('tbody');
        groupList.innerHTML = '';
        groups.forEach(group => {
            const row = document.createElement('tr');
            row.dataset.groupId = group.groupId;
            row.innerHTML = `
            <td>${group.groupName}</td>
            <td>
                <button class="manage-btn" data-id="${group.groupId}">Manage</button>
                <button class="remove-group-btn" data-id="${group.groupId}">Usuń</button>
            </td>
        `;
            groupList.appendChild(row);
        });

        document.querySelectorAll('.manage-btn').forEach(button => {
            button.addEventListener('click', (event) => {
                const groupId = event.target.dataset.id;
                fetchCarsByGroup(groupId);
            });
        });

        document.querySelectorAll('.remove-group-btn').forEach(button => {
            button.addEventListener('click', async (event) => {
                const groupId = event.target.dataset.id;
                if (!token) {
                    alert('You must be logged in to manage groups.');
                    window.location.href = 'main.html';
                    return;
                }

                try {
                    const response = await fetch(`/car-groups/${groupId}`, {
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
                        event.target.closest('tr').remove();
                    } else {
                        alert('Failed to remove group.');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('An error occurred while removing the group');
                }
            });
        });
    }

    function addCarToTable(car) {
        const carList = document.querySelector('#car-list tbody');
        const row = document.createElement('tr');
        row.dataset.carId = car.vehicleId;
        row.innerHTML = `
        <td><input type="checkbox" class="select-car-checkbox"></td>
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
                alert('You must be logged in to remove a car.');
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
                    row.remove();
                } else {
                    alert('Failed to remove car.');
                }
            } catch (error) {
                console.error('Error:', error);
                alert('An error occurred while removing the car');
            }
        });
    }

    document.getElementById('group-search').addEventListener('input', () => {
        const searchTerm = document.getElementById('group-search').value.toLowerCase();
        const rows = document.querySelectorAll('#group-list tbody tr');
        rows.forEach(row => {
            const groupName = row.querySelector('td').textContent.toLowerCase();
            if (groupName.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });

    async function fetchColors() {
        try {
            const response = await fetch('/colors');
            if (response.ok) {
                const colors = await response.json();
                populateDatalist('filter-color-options', colors.map(color => color.colorName));
                populateDatalist('add-car-color-options', colors.map(color => color.colorName));
                populateDatalist('details-car-color-options', colors.map(color => color.colorName));
            } else {
                console.error('Failed to fetch colors');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    fetchColors();

    async function fetchCarModels() {
        try {
            const response = await fetch('/car-models');
            if (response.ok) {
                const carModels = await response.json();
                populateDatalist('filter-car-model-options', carModels.map(model => model.modelName));
                populateDatalist('add-car-model-options', carModels.map(model => model.modelName));
                populateDatalist('details-car-model-options', carModels.map(model => model.modelName));
            } else {
                console.error('Failed to fetch car models');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    function populateDatalist(datalistId, options) {
        const datalist = document.getElementById(datalistId);
        datalist.innerHTML = '';
        options.forEach(option => {
            const optionElement = document.createElement('option');
            optionElement.value = option;
            datalist.appendChild(optionElement);
        });
    }

    function validateInput(inputId, datalistId) {
        const input = document.getElementById(inputId);
        const datalist = document.getElementById(datalistId);
        const options = Array.from(datalist.options).map(option => option.value);

        input.addEventListener('input', () => {
            if (!options.includes(input.value)) {
                input.setCustomValidity('Please select a valid option from the list.');
            } else {
                input.setCustomValidity('');
            }
        });
    }
    validateInput('filter-color', 'filter-color-options');
    validateInput('filter-car-model', 'filter-car-model-options');
    validateInput('car-color', 'add-car-color-options');
    validateInput('car-model', 'add-car-model-options');
    validateInput('details-color', 'details-car-color-options');
    validateInput('details-model', 'details-car-model-options');

    fetchCarModels();

    servicesBtn.addEventListener('click', (event) => {
        event.preventDefault();
        servicesDropdown.style.display = servicesDropdown.style.display === 'none' ? 'block' : 'none';
    });

    servicesDropdown.addEventListener('click', (event) => {
        if (event.target.tagName === 'A') {
            const groupId = event.target.dataset.groupId;
            if (groupId === 'main') {
                fetchCars();
                document.querySelector('.car-list-container').style.display = 'block';
                document.querySelector('.group-list-container').style.display = 'none';
            } else if (event.target.id === 'groups-link') {
                fetchGroups();
                document.querySelector('.car-list-container').style.display = 'none';
                document.querySelector('.group-list-container').style.display = 'block';
            }
            servicesDropdown.style.display = 'none';
        }
    });



    async function fetchCarsByGroup(groupId) {
        if (!token) {
            alert('You must be logged in to access this data.');
            window.location.href = 'main.html';
            return;
        }
        try {
            const response = await fetch(`/car-groups/${groupId}/cars`, {
                method: 'GET',
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
                const cars = await response.json();
                displayCarsInGroup(cars, groupId);
                document.querySelector('.car-list-container').style.display = 'block';
                document.querySelector('.group-list-container').style.display = 'none';
            } else {
                alert('Failed to fetch cars.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while fetching cars');
        }
    }
    function displayCarsInGroup(cars, groupId) {
        carList.innerHTML = '';
        cars.forEach(car => {
            const row = document.createElement('tr');
            row.dataset.carId = car.vehicleId;
            row.innerHTML = `
            <td><input type="checkbox" class="select-car-checkbox"></td>
            <td>${car.carModel.modelName}</td>
            <td>${car.registrationNumber}</td>
            <td>${car.productionYear}</td>
            <td>${car.status}</td>
            <td>
                <button class="details-btn" data-id="${car.vehicleId}">Details</button>
                <button class="remove-from-group-btn" data-id="${car.vehicleId}" data-group-id="${groupId}">Usuń z grupy</button>
            </td>
        `;
            carList.appendChild(row);

            const detailsBtn = row.querySelector('.details-btn');
            detailsBtn.addEventListener('click', () => {
                openDetailsModal(car);
            });

            const removeFromGroupBtn = row.querySelector('.remove-from-group-btn');
            removeFromGroupBtn.addEventListener('click', async () => {
                if (!token) {
                    alert('You must be logged in to remove a car from the group.');
                    window.location.href = 'main.html';
                    return;
                }

                try {
                    const response = await fetch(`/car-groups/${groupId}/cars/${car.vehicleId}`, {
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
                        row.remove();
                    } else {
                        alert('Failed to remove car from group.');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('An error occurred while removing the car from the group');
                }
            });
        });
    }

    async function fetchCars(filters = {}) {
        if (!token) {
            alert('You must be logged in to access this data.');
            window.location.href = 'main.html';
            return;
        }
        try {
            const queryString = new URLSearchParams(filters).toString();
            const response = await fetch(`/cars/filter?${queryString}`, {
                method: 'GET',
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
                const cars = await response.json();
                displayCars(cars);
            } else {
                alert('Failed to fetch cars.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while fetching cars');
        }
    }

    function displayCars(cars) {
        carList.innerHTML = '';
        cars.forEach(car => {
            const row = document.createElement('tr');
            row.dataset.carId = car.vehicleId;
            row.innerHTML = `
                <td><input type="checkbox" class="select-car-checkbox"></td>
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
                        row.remove();
                    } else {
                        alert('Failed to remove car.');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('An error occurred while removing the car');
                }
            });
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
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(carData)
            });

            if (response.ok) {
                const updatedCar = await response.json();
                const row = carList.querySelector(`tr[data-car-id="${updatedCar.vehicleId}"]`);
                row.querySelector('td:nth-child(2)').textContent = updatedCar.carModel.modelName;
                row.querySelector('td:nth-child(3)').textContent = updatedCar.registrationNumber;
                row.querySelector('td:nth-child(4)').textContent = updatedCar.productionYear;
                row.querySelector('td:nth-child(5)').textContent = updatedCar.status;
                detailsModal.style.display = 'none';
            } else {
                alert('Failed to update car.');
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

    const filterElements = document.querySelectorAll('.filter-group');
    filterElements.forEach(element => {
        element.addEventListener('input', () => {
            const filters = getFilters();
            fetchCars(filters);
        });
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

    createGroupBtn.addEventListener('click', () => {
        createGroupModal.style.display = 'flex';
    });

    createGroupForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const groupName = document.getElementById('group-name').value;
        const selectedCarIds = Array.from(document.querySelectorAll('.select-car-checkbox:checked'))
            .map(checkbox => checkbox.closest('tr').dataset.carId);

        try {
            const response = await fetch('/car-groups', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ groupName })
            });

            if (response.ok) {
                const newGroup = await response.json();
                const groupSelect = document.getElementById('group-select');
                const option = document.createElement('option');
                option.value = newGroup.groupId;
                option.textContent = newGroup.groupName;
                groupSelect.appendChild(option);

                const groupId = newGroup.groupId;
                try {
                    const response = await fetch(`/car-groups/${groupId}/cars`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        },
                        body: JSON.stringify(selectedCarIds)
                    });

                    if (response.ok) {
                        alert('Cars added to group successfully.');
                    } else {
                        alert('Failed to add cars to group.');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('An error occurred while adding cars to the group');
                }
                createGroupModal.style.display = 'none';
                createGroupForm.reset();
            } else {
                alert('Failed to create group.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while creating the group');
        }
    });



    addToGroupBtn.addEventListener('click', async () => {
        const selectedCarIds = Array.from(document.querySelectorAll('.select-car-checkbox:checked'))
            .map(checkbox => checkbox.closest('tr').dataset.carId);

        const groupId = document.getElementById('group-select').value;
        if (!groupId) {
            alert('Proszę wybrać grupę.');
            return;
        }

        if (!token) {
            alert('You must be logged in to add cars to a group.');
            window.location.href = 'main.html';
            return;
        }

        try {
            const response = await fetch(`/car-groups/${groupId}/cars`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(selectedCarIds)
            });

            if (response.status === 401) {
                alert('Session expired or invalid token. Please log in again.');
                localStorage.removeItem('authToken');
                window.location.href = 'main.html';
                return;
            }

            if (response.ok) {
                alert('Cars added to group successfully.');
            } else {
                alert('Failed to add cars to group.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while adding cars to the group');
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
                displayGroups(groups);
            } else {
                console.error('Failed to fetch groups');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    function populateGroupDropdown(groups) {
        servicesDropdown.innerHTML = `
        <a href="#" data-group-id="main">Main</a>
        <a href="#" id="groups-link">Groups</a>
    `;
        groupSelect.innerHTML = '<option value="">Wybierz grupę</option>';
        groups.forEach(group => {
            const option = document.createElement('option');
            option.value = group.groupId;
            option.textContent = group.groupName;
            groupSelect.appendChild(option);
        });
    }
    fetchGroups();
    fetchCars();
});