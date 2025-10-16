document.addEventListener("DOMContentLoaded", function() {
    const saveBtn = document.getElementById("saveAttendanceBtn");
    const alertMsg = document.getElementById("alertMsg");

    saveBtn.addEventListener("click", function() {
        const date = document.querySelector('input[name="attendanceDate"]').value;
        const checkboxes = document.querySelectorAll('input[type="checkbox"][name^="attendance["]');
        const attendanceMap = {};

        // collect attendance map
        checkboxes.forEach(cb => {
            const id = cb.name.replace('attendance[', '').replace(']', '');
            attendanceMap[id] = cb.checked;
        });

        // Get teacher ID from hidden input (you must have <input id="teacherEmployeeId" value="...">)
        const teacherEmployeeId = document.getElementById("teacherEmployeeId").value;

        // send AJAX
        fetch("/teacher/save-attendance-ajax", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                attendanceDate: date,
                attendanceMap: attendanceMap,
                teacherEmployeeId: teacherEmployeeId
            })
        })
        .then(res => res.json())
        .then(data => {
            alertMsg.style.display = "block";
            alertMsg.className = "alert alert-success";
            alertMsg.innerText = data.message;

            // refresh graph
            updateAttendanceChart();
        })
        .catch(err => {
            alertMsg.style.display = "block";
            alertMsg.className = "alert alert-danger";
            alertMsg.innerText = "Error: " + err;
        });
    });

    updateAttendanceChart(); // load chart initially
});

function updateAttendanceChart() {
    const date = document.querySelector('input[name="attendanceDate"]').value;

    fetch(`/teacher/attendance/data?date=${date}`)
        .then(res => res.json())
        .then(data => {
            const ctx = document.getElementById('attendanceChart').getContext('2d');

            // Destroy previous chart if exists
            if (window.attendanceChartInstance) window.attendanceChartInstance.destroy();

            window.attendanceChartInstance = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.students,
                    datasets: [{
                        label: 'Present',
                        data: data.present,
                        backgroundColor: 'rgba(75, 192, 192, 0.6)'
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { stepSize: 1 }
                        }
                    }
                }
            });
        });
}
