function showSection(sectionName, event) {
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });

    const target = document.getElementById(`${sectionName}-section`);
    if (target) {
        target.classList.add('active');
    }

    if (event) {
        document.querySelectorAll('.sidebar li').forEach(li => li.classList.remove('active'));
        const parentLi = event.target.closest('li');
        if (parentLi) {
            parentLi.classList.add('active');
        }
    }

    switch (sectionName) {
        case 'parking':
            loadParkingData();
            break;
        case 'cars':
            loadCars();
            break;
        case 'tickets':
            loadTickets();
            break;
        default:
            break;
    }
}

function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.toggle('close');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    showSection('parking');
});