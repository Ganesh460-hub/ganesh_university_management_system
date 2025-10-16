// static/JS/subject-results.js - Teacher Results JavaScript
document.addEventListener('DOMContentLoaded', function() {
    initializeGradeCalculators();
    setupFormValidation();
    setupResetButton();
});

function initializeGradeCalculators() {
    // Auto-calculate grades when marks are entered
    const marksInputs = document.querySelectorAll('.marks-input');
    marksInputs.forEach(input => {
        input.addEventListener('input', function() {
            calculateGrade(this);
        });

        // Calculate grade if value already exists (on page load)
        if (input.value) {
            calculateGrade(input);
        }
    });
}

function calculateGrade(input) {
    const marks = parseInt(input.value);
    const studentIndex = input.getAttribute('data-student-index');
    const gradeDisplay = document.getElementById('grade-' + studentIndex);
    const statusDisplay = document.getElementById('status-' + studentIndex);

    if (isNaN(marks) || marks < 0 || marks > 100) {
        resetGradeDisplay(gradeDisplay, statusDisplay);
        return;
    }

    let grade, status, gradeClass, statusClass;

    if (marks >= 90) {
        grade = 'A';
        status = 'PASS';
        gradeClass = 'grade-A';
        statusClass = 'text-success';
    } else if (marks >= 80) {
        grade = 'B';
        status = 'PASS';
        gradeClass = 'grade-B';
        statusClass = 'text-success';
    } else if (marks >= 70) {
        grade = 'C';
        status = 'PASS';
        gradeClass = 'grade-C';
        statusClass = 'text-success';
    } else if (marks >= 60) {
        grade = 'D';
        status = 'PASS';
        gradeClass = 'grade-D';
        statusClass = 'text-success';
    } else if (marks >= 40) {
        grade = 'E';
        status = 'PASS';
        gradeClass = 'grade-D';
        statusClass = 'text-success';
    } else {
        grade = 'F';
        status = 'FAIL';
        gradeClass = 'grade-F';
        statusClass = 'text-danger';
    }

    updateGradeDisplay(gradeDisplay, grade, gradeClass);
    updateStatusDisplay(statusDisplay, status, statusClass);
}

function resetGradeDisplay(gradeDisplay, statusDisplay) {
    gradeDisplay.textContent = '-';
    gradeDisplay.className = 'grade-display';
    statusDisplay.textContent = '-';
    statusDisplay.className = 'status-display';
}

function updateGradeDisplay(display, grade, className) {
    display.textContent = grade;
    display.className = 'grade-display ' + className;
}

function updateStatusDisplay(display, status, className) {
    display.textContent = status;
    display.className = 'status-display ' + className;
}

function setupResetButton() {
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function() {
            resetAllGrades();
        });
    }
}

function resetAllGrades() {
    document.querySelectorAll('.marks-input').forEach(input => {
        input.value = '';
        const studentIndex = input.getAttribute('data-student-index');
        const gradeDisplay = document.getElementById('grade-' + studentIndex);
        const statusDisplay = document.getElementById('status-' + studentIndex);
        resetGradeDisplay(gradeDisplay, statusDisplay);
    });
}

function setupFormValidation() {
    const form = document.getElementById('resultsForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            const marksInputs = document.querySelectorAll('.marks-input');
            let allValid = true;
            let emptyFields = 0;

            marksInputs.forEach(input => {
                const marks = parseInt(input.value);
                if (isNaN(marks)) {
                    emptyFields++;
                    input.classList.add('is-invalid');
                } else if (marks < 0 || marks > 100) {
                    allValid = false;
                    input.classList.add('is-invalid');
                } else {
                    input.classList.remove('is-invalid');
                }
            });

            if (!allValid) {
                e.preventDefault();
                alert('Please enter valid marks (0-100) for all students.');
                return;
            }

            if (emptyFields === marksInputs.length) {
                e.preventDefault();
                alert('Please enter marks for at least one student.');
                return;
            }
        });
    }
}