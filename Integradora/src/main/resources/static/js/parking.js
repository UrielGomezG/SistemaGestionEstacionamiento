async function loadParkingData() {
    try {
        const statsData = await fetchWithErrorHandling(`${API_BASE_URL}/cars/stats`);
        if (statsData.success) {
            const stats = statsData.stats || {};
            document.getElementById('occupied-spots').textContent = stats.occupiedSpots || 0;
            document.getElementById('available-spots').textContent = stats.availableSpots || 0;
            document.getElementById('waiting-count').textContent = stats.waitingCars || 0;
            document.getElementById('total-exits').textContent = stats.exitedCars || 0;
        }

        const parkingResponse = await fetchWithErrorHandling(`${API_BASE_URL}/cars/parking`);
        if (parkingResponse.success) {
            AppState.parkingData = parkingResponse.cars || [];
            renderParkingTable(AppState.parkingData);
        }
    } catch (error) {}
}

function renderParkingTable(cars) {
    const tbody = document.getElementById('parking-table-body');
    if (!tbody) { return; }

    if (!cars.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No hay vehículos en el estacionamiento</td></tr>';
        return;
    }

    tbody.innerHTML = cars.map(car => `
        <tr>
            <td>${car.plate}</td>
            <td>${car.make || '-'}</td>
            <td>${car.model || '-'}</td>
            <td>${car.color || '-'}</td>
            <td>${formatDateTime(car.entryTime)}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="registerExit('${car.plate}')">
                    Registrar Salida
                </button>
            </td>
        </tr>
    `).join('');
}

function filterParkingTable() {
    const searchTerm = document.getElementById('parking-search').value;
    const filtered = filterData(AppState.parkingData, searchTerm, ['plate', 'make', 'model']);
    renderParkingTable(filtered);
}

async function openParkingModal() {
    openModal('parking-modal');
    document.getElementById('parking-search-plate').value = '';
    document.getElementById('new-car-form').style.display = 'none';
    document.getElementById('parking-form').reset();
    AppState.selectedCarForParking = null;
    await loadAllCarsForParking();
}

function closeParkingModal() {
    closeModal('parking-modal');
    document.getElementById('parking-search-plate').value = '';
    document.getElementById('new-car-form').style.display = 'none';
    AppState.selectedCarForParking = null;
}

async function loadAllCarsForParking() {
    try {
        const response = await fetchWithErrorHandling(`${API_BASE_URL}/cars`);
        if (response.success) {
            AppState.allCarsForParking = response.cars || [];
            renderParkingCarsList(AppState.allCarsForParking);
        } else {
            showParkingCarsListError();
        }
    } catch (error) {
        showParkingCarsListError();
    }
}

function showParkingCarsListError() {
    const container = document.getElementById('parking-cars-list');
    if (container) {
        container.innerHTML = '<div class="empty-state">Error al cargar vehículos</div>';
    }
}

function renderParkingCarsList(cars) {
    const container = document.getElementById('parking-cars-list');
    if (!container) { return; }

    if (!cars.length) {
        container.innerHTML = '<div class="empty-state">No hay vehículos registrados</div>';
        return;
    }

    container.innerHTML = cars.map(car => {
        const isParked = car.inParking === true;
        const statusClass = isParked ? 'parked' : 'available';
        const statusText = isParked ? 'Estacionado' : 'Disponible';
        const selected = AppState.selectedCarForParking && AppState.selectedCarForParking.id === car.id ? 'selected' : '';
        const disabledAttr = isParked ? 'style="opacity:0.6;cursor:not-allowed;"' : '';
        const clickHandler = isParked ? '' : `onclick="selectCarForParking(${car.id})"`;
        const details = [car.make, car.model, car.color].filter(Boolean).join(' - ');

        return `
            <div class="car-item ${selected}" ${clickHandler} ${disabledAttr}>
                <div class="car-item-info">
                    <div class="car-item-plate">${car.plate}</div>
                    <div class="car-item-details">${details || 'Sin detalles'}</div>
                </div>
                <span class="car-item-status ${statusClass}">${statusText}</span>
            </div>
        `;
    }).join('');
}

function filterParkingCars() {
    const searchTerm = document.getElementById('parking-search-plate').value;
    const filtered = filterData(AppState.allCarsForParking, searchTerm, ['plate', 'make', 'model']);
    renderParkingCarsList(filtered);
}

function selectCarForParking(carId) {
    const car = AppState.allCarsForParking.find(item => item.id === carId);
    if (!car) { return; }
    if (car.inParking) {
        showToast('Este vehículo ya está estacionado', 'error');
        return;
    }

    AppState.selectedCarForParking = car;
    renderParkingCarsList(AppState.allCarsForParking);
    const footer = document.getElementById('parking-modal-footer');
    if (footer) {
        footer.innerHTML = `
            <button type="button" class="btn btn-secondary" onclick="closeParkingModal()">Cancelar</button>
            <button type="button" class="btn btn-primary" onclick="confirmParkingEntry()">
                Registrar Entrada de ${car.plate}
            </button>
        `;
    }
}

function showNewCarForm() {
    document.getElementById('new-car-form').style.display = 'block';
    document.getElementById('parking-cars-list').style.display = 'none';
    document.getElementById('parking-search-plate').style.display = 'none';
    document.querySelector('label[for="parking-search-plate"]').style.display = 'none';
    AppState.selectedCarForParking = null;
    updateParkingModalFooter('<button type="button" class="btn btn-secondary" onclick="closeParkingModal()">Cerrar</button>');
}

function hideNewCarForm() {
    document.getElementById('new-car-form').style.display = 'none';
    document.getElementById('parking-cars-list').style.display = 'block';
    document.getElementById('parking-search-plate').style.display = 'block';
    document.querySelector('label[for="parking-search-plate"]').style.display = 'block';
    document.getElementById('parking-form').reset();
    updateParkingModalFooter('<button type="button" class="btn btn-secondary" onclick="closeParkingModal()">Cerrar</button>');
}

function updateParkingModalFooter(html) {
    const footer = document.getElementById('parking-modal-footer');
    if (footer) {
        footer.innerHTML = html;
    }
}

async function confirmParkingEntry() {
    const car = AppState.selectedCarForParking;
    if (!car) {
        showToast('Selecciona un vehículo', 'error');
        return;
    }
    if (car.inParking) {
        showToast('Este vehículo ya está estacionado', 'error');
        return;
    }

    try {
        const response = await fetchWithErrorHandling(`${API_BASE_URL}/cars/entry`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(car)
        });

        if (response.success) {
            showToast(response.message, 'success');
            closeParkingModal();
            loadParkingData();
        }
    } catch (error) {}
}

async function saveParkingEntry(event) {
    event.preventDefault();

    const plate = document.getElementById('parking-plate').value;
    const make = document.getElementById('parking-make').value;
    const model = document.getElementById('parking-model').value;
    const color = document.getElementById('parking-color').value;

    const carPayload = { plate, make, model, color };

    try {
        const createResponse = await fetchWithErrorHandling(`${API_BASE_URL}/cars`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(carPayload)
        });

        if (!createResponse.success) {
            throw new Error(createResponse.message || 'Error al crear auto');
        }

        const entryResponse = await fetchWithErrorHandling(`${API_BASE_URL}/cars/entry`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(createResponse.car)
        });

        if (entryResponse.success) {
            showToast(entryResponse.message, 'success');
            closeParkingModal();
            loadParkingData();
        }
    } catch (error) {}
}

async function registerExit(plate) {
    showConfirm(
        `¿Registrar salida del vehículo con placa ${plate}?`,
        async () => {
            try {
                const response = await fetchWithErrorHandling(`${API_BASE_URL}/cars/exit/${plate}`, {
                    method: 'POST'
                });

                if (response.success) {
                    showToast(response.message, 'success');
                    loadParkingData();
                }
            } catch (error) {}
        }
    );
}