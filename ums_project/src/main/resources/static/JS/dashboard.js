document.addEventListener('DOMContentLoaded', () => {
    const fabBtn = document.getElementById('fabBtn');
    const fabOptions = document.getElementById('fabOptions');

    fabBtn.addEventListener('click', () => {
        fabOptions.classList.toggle('show');
    });
});


document.addEventListener('DOMContentLoaded', function() {
    // Automatically color code all attendance percentages
    const attendanceElements = document.querySelectorAll('[data-attendance-percentage]');

    attendanceElements.forEach(element => {
        const percentage = parseFloat(element.getAttribute('data-attendance-percentage'));
        let colorClass = '';

        if (percentage <= 50) colorClass = 'attendance-low';
        else if (percentage <= 75) colorClass = 'attendance-medium';
        else if (percentage >= 90) colorClass = 'attendance-high';
        else colorClass = 'attendance-good';

        element.classList.add(colorClass);
    });
});