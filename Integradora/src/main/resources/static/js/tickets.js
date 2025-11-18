async function loadTickets() {
    try {
        const response = await fetchWithErrorHandling(`${API_BASE_URL}/tickets`);
        if (response.success) {
            AppState.ticketsData = response.tickets || [];
            renderTicketsTable(AppState.ticketsData);
            console.log(response.tickets);
        }
    } catch (error) {}
}

function renderTicketsTable(tickets) {
    const tbody = document.getElementById('tickets-table-body');
    if (!tbody) { return; }

    if (!tickets.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No hay tickets registrados</td></tr>';
        return;
    }

    tbody.innerHTML = tickets.map(ticket => `
        <tr>
            <td>${ticket.id}</td>
            <td>${ticket.plate}</td>
            <td>${formatDateTime(ticket.entryTime)}</td>
            <td>${formatDateTime(ticket.exitTime)}</td>
            <td>${formatCurrency(ticket.total)}</td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editTicket(${ticket.id})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="deleteTicket(${ticket.id})">Eliminar</button>
            </td>
        </tr>
    `).join('');
}

function filterTicketsTable() {
    const searchTerm = document.getElementById('tickets-search').value;
    const filtered = filterData(AppState.ticketsData, searchTerm, ['plate']);
    renderTicketsTable(filtered);
}

function openTicketModal(ticket = null) {
    const title = document.getElementById('ticket-modal-title');
    const form = document.getElementById('ticket-form');

    if (ticket) {
        title.textContent = 'Editar Ticket';
        document.getElementById('ticket-id').value = ticket.id;
        document.getElementById('ticket-plate').value = ticket.plate;
        document.getElementById('ticket-entry-time').value = ticket.entryTime ? ticket.entryTime.substring(0, 16) : '';
        document.getElementById('ticket-exit-time').value = ticket.exitTime ? ticket.exitTime.substring(0, 16) : '';
        document.getElementById('ticket-total').value = ticket.total || 0;
    } else {
        title.textContent = 'Nuevo Ticket';
        form.reset();
        document.getElementById('ticket-id').value = '';
    }

    openModal('ticket-modal');
}

function closeTicketModal() {
    closeModal('ticket-modal');
}

async function editTicket(id) {
    try {
        const response = await fetchWithErrorHandling(`${API_BASE_URL}/tickets/${id}`);
        if (response.success && response.ticket) {
            openTicketModal(response.ticket);
        } else {
            showToast(response.message || 'Ticket no encontrado', 'error');
        }
    } catch (error) {}
}

async function saveTicket(event) {
    event.preventDefault();

    const id = document.getElementById('ticket-id').value;
    const payload = {
        plate: document.getElementById('ticket-plate').value,
        entryTime: document.getElementById('ticket-entry-time').value,
        exitTime: document.getElementById('ticket-exit-time').value || null,
        total: parseFloat(document.getElementById('ticket-total').value) || 0
    };

    try {
        const url = id ? `${API_BASE_URL}/tickets/${id}` : `${API_BASE_URL}/tickets`;
        const method = id ? 'PUT' : 'POST';

        const response = await fetchWithErrorHandling(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.success) {
            showToast(response.message, 'success');
            closeTicketModal();
            loadTickets();
        }
    } catch (error) {}
}

async function deleteTicket(id) {
    showConfirm(
        '¿Eliminar este ticket?',
        async () => {
            try {
                const response = await fetchWithErrorHandling(`${API_BASE_URL}/tickets/${id}`, {
                    method: 'DELETE'
                });

                if (response.success) {
                    showToast(response.message, 'success');
                    loadTickets();
                }
            } catch (error) {}
        }
    );
}