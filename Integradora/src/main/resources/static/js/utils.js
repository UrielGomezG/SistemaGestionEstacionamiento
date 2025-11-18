function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) { return; }

    toast.textContent = message;
    toast.className = `toast ${type} show`;

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function formatDateTime(value) {
    if (!value) { return '-'; }
    try {
        const date = new Date(value);
        return date.toLocaleString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (error) {
        console.error('Error formateando fecha:', error);
        return '-';
    }
}

function formatCurrency(amount) {
    if (amount === null || amount === undefined) { return '-'; }
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'MXN'
    }).format(amount);
}

async function fetchWithErrorHandling(url, options = {}) {
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            const errorMessage = `Error HTTP: ${response.status} ${response.statusText}`;
            throw new Error(errorMessage);
        }
        return await response.json();
    } catch (error) {
        handleApiError(error);
        throw error;
    }
}

function handleApiError(error, defaultMessage = 'Error en la operación') {
    console.error('API Error:', error);
    const message = error?.message || defaultMessage;
    showToast(message, 'error');
}

function filterData(data, searchTerm, fields) {
    if (!searchTerm) { return data; }

    const term = searchTerm.toLowerCase();
    return data.filter(item =>
        fields.some(field => {
            const value = item[field];
            return value && value.toString().toLowerCase().includes(term);
        })
    );
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

function attachModalOutsideClickHandlers() {
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function (event) {
            if (event.target === this) {
                this.classList.remove('active');
            }
        });
    });
}

function showConfirm(message, onConfirm, onCancel = null) {
    const confirmModal = document.getElementById('confirm-modal');
    const confirmMessage = document.getElementById('confirm-message');
    const confirmBtn = document.getElementById('confirm-btn');
    const cancelBtn = document.getElementById('confirm-cancel-btn');

    if (!confirmModal || !confirmMessage || !confirmBtn || !cancelBtn) {
        console.error('Modal de confirmación no encontrado');
        return;
    }

    confirmMessage.textContent = message;
    confirmModal.classList.add('active');

    // Remover listeners anteriores
    const newConfirmBtn = confirmBtn.cloneNode(true);
    const newCancelBtn = cancelBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
    cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);

    // Agregar nuevos listeners
    newConfirmBtn.addEventListener('click', () => {
        confirmModal.classList.remove('active');
        if (onConfirm) onConfirm();
    });

    newCancelBtn.addEventListener('click', () => {
        confirmModal.classList.remove('active');
        if (onCancel) onCancel();
    });
}

document.addEventListener('DOMContentLoaded', attachModalOutsideClickHandlers);