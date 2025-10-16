// static/js/student-results.js - Student Results JavaScript
document.addEventListener('DOMContentLoaded', function() {
    initializeStudentResults();
    setupSemesterNavigation();
    setupProgressAnimations();
    setupPrintResults();
});

function initializeStudentResults() {
    console.log('Student Results initialized');

    // Add loading animations
    addLoadingAnimations();

    // Setup tooltips if any
    setupTooltips();

    // Setup any charts or visualizations
    setupProgressBars();
}

function setupSemesterNavigation() {
    const semesterButtons = document.querySelectorAll('.semester-btn:not(.disabled)');

    semesterButtons.forEach(btn => {
        btn.addEventListener('mouseenter', function() {
            if (!this.classList.contains('active')) {
                this.style.transform = 'translateY(-2px) scale(1.02)';
                this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
            }
        });

        btn.addEventListener('mouseleave', function() {
            if (!this.classList.contains('active')) {
                this.style.transform = 'translateY(0) scale(1)';
                this.style.boxShadow = 'none';
            }
        });

        btn.addEventListener('click', function(e) {
            if (this.classList.contains('disabled')) {
                e.preventDefault();
                showSemesterNotAvailableMessage();
            }
        });
    });
}

function setupProgressAnimations() {
    // Animate progress bars on scroll
    const progressBars = document.querySelectorAll('.progress-bar');

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const progressBar = entry.target;
                const width = progressBar.style.width || progressBar.getAttribute('aria-valuenow') + '%';

                // Reset and animate
                progressBar.style.width = '0%';
                setTimeout(() => {
                    progressBar.style.transition = 'width 1.5s ease-in-out';
                    progressBar.style.width = width;
                }, 300);

                observer.unobserve(progressBar);
            }
        });
    }, { threshold: 0.5 });

    progressBars.forEach(bar => observer.observe(bar));
}

function setupProgressBars() {
    // Initialize any additional progress visualizations
    const cgpaProgress = document.querySelector('.cgpa-display .progress-bar');
    if (cgpaProgress) {
        const currentValue = cgpaProgress.getAttribute('aria-valuenow') || '0';
        cgpaProgress.style.width = '0%';

        setTimeout(() => {
            cgpaProgress.style.transition = 'width 2s ease-out';
            cgpaProgress.style.width = currentValue + '%';
        }, 1000);
    }
}

function addLoadingAnimations() {
    // Add subtle animations to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';

        setTimeout(() => {
            card.style.transition = 'all 0.6s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 150);
    });
}

function setupTooltips() {
    // Initialize Bootstrap tooltips if needed
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

function setupPrintResults() {
    // Add print functionality
    const printButton = document.getElementById('printResults');
    if (printButton) {
        printButton.addEventListener('click', function() {
            printSemesterResults();
        });
    }
}

function printSemesterResults() {
    const printContent = document.querySelector('main').innerHTML;
    const originalContent = document.body.innerHTML;

    document.body.innerHTML = `
        <!DOCTYPE html>
        <html>
        <head>
            <title>Academic Results - Semester Report</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                .card { border: 1px solid #ddd; margin-bottom: 20px; padding: 15px; }
                .table { width: 100%; border-collapse: collapse; }
                .table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                .text-success { color: #28a745; }
                .text-danger { color: #dc3545; }
                .badge { padding: 4px 8px; border-radius: 4px; color: white; }
                .bg-success { background-color: #28a745; }
                .bg-danger { background-color: #dc3545; }
                .bg-warning { background-color: #ffc107; }
                .text-center { text-align: center; }
                @media print {
                    .no-print { display: none; }
                    .sidebar { display: none; }
                }
            </style>
        </head>
        <body>
            <div class="no-print" style="margin-bottom: 20px;">
                <button onclick="window.print()" class="btn btn-primary">Print Now</button>
                <button onclick="closePrint()" class="btn btn-secondary">Close</button>
            </div>
            ${printContent}
            <script>
                function closePrint() {
                    document.body.innerHTML = \`${originalContent}\`;
                    window.location.reload();
                }
            <\/script>
        </body>
        </html>
    `;

    window.print();
}

function showSemesterNotAvailableMessage() {
    // Create a toast notification
    const toast = document.createElement('div');
    toast.className = 'alert alert-warning alert-dismissible fade show position-fixed';
    toast.style.cssText = `
        top: 20px;
        right: 20px;
        z-index: 1050;
        min-width: 300px;
    `;
    toast.innerHTML = `
        <strong>Results Not Available</strong>
        <p class="mb-0">This semester's results are not available yet. Please check back later.</p>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(toast);

    // Auto remove after 5 seconds
    setTimeout(() => {
        if (toast.parentNode) {
            toast.remove();
        }
    }, 5000);
}

// Export functions for potential module usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initializeStudentResults,
        setupSemesterNavigation,
        setupProgressAnimations,
        printSemesterResults
    };
}