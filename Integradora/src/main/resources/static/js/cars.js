async function loadCars() {
    try {
        const response = await fetchWithErrorHandling(`${API_BASE_URL}/cars`);
        if (response.success) {
            AppState.carsData = response.cars || [];
            renderCarsTable(AppState.carsData);
        }
    } catch (error) {}
}

function renderCarsTable(cars) {
    const tbody = document.getElementById('cars-table-body');
    if (!tbody) { return; }

    if (!cars.length) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No hay autos registrados</td></tr>';
        return;
    }

    tbody.innerHTML = cars.map(car => `
        <tr>
            <td>${car.plate}</td>
            <td>${car.make || '-'}</td>
            <td>${car.model || '-'}</td>
            <td>${car.color || '-'}</td>
            <td>${formatDateTime(car.entryTime)}</td>
            <td>${formatDateTime(car.exitTime)}</td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editCar(${car.id})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="deleteCar(${car.id}, '${car.plate}')">Eliminar</button>
            </td>
        </tr>
    `).join('');
}

function filterCarsTable() {
    const searchTerm = document.getElementById('cars-search').value;
    const filtered = filterData(AppState.carsData, searchTerm, ['plate', 'make', 'model']);
    renderCarsTable(filtered);
}

function openCarModal(car = null) {
    const title = document.getElementById('car-modal-title');
    const form = document.getElementById('car-form');

    if (car) {
        title.textContent = 'Editar Auto';
        document.getElementById('car-id').value = car.id;
        document.getElementById('car-plate').value = car.plate;
        document.getElementById('car-make').value = car.make || '';
        document.getElementById('car-model').value = car.model || '';
        document.getElementById('car-color').value = car.color || '';
    } else {
        title.textContent = 'Nuevo Auto';
        form.reset();
        document.getElementById('car-id').value = '';
    }

    openModal('car-modal');
}

function closeCarModal() {
    closeModal('car-modal');
}

async function editCar(id) {
    try {
        const response = await fetchWithErrorHandling(`${API_BASE_URL}/cars/id/${id}`);
        if (response.success && response.car) {
            openCarModal(response.car);
        } else {
            showToast(response.message || 'Auto no encontrado', 'error');
        }
    } catch (error) {}
}

async function saveCar(event) {
    event.preventDefault();

    const id = document.getElementById('car-id').value;
    const payload = {
        plate: document.getElementById('car-plate').value,
        make: document.getElementById('car-make').value,
        model: document.getElementById('car-model').value,
        color: document.getElementById('car-color').value
    };

    try {
        const url = id ? `${API_BASE_URL}/cars/${id}` : `${API_BASE_URL}/cars`;
        const method = id ? 'PUT' : 'POST';

        const response = await fetchWithErrorHandling(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.success) {
            showToast(response.message, 'success');
            closeCarModal();
            loadCars();
            if (document.getElementById('parking-modal').classList.contains('active')) {
                loadAllCarsForParking();
            }
        }
    } catch (error) {}
}

async function deleteCar(id, plate) {
    showConfirm(
        `¿Eliminar el auto con placa ${plate}?`,
        async () => {
            try {
                const response = await fetchWithErrorHandling(`${API_BASE_URL}/cars/${id}`, {
                    method: 'DELETE'
                });

                if (response.success) {
                    showToast(response.message, 'success');
                    loadCars();
                }
            } catch (error) {}
        }
    );
}