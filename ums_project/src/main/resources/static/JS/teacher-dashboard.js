// Toggle dropdown menu
const menuBtn = document.querySelector('.menu-btn');
const menuContent = document.querySelector('.menu-content');

menuBtn.addEventListener('click', () => {
    menuContent.style.display = menuContent.style.display === 'block' ? 'none' : 'block';
});

// Click outside to close
window.addEventListener('click', function(e){
    if (!menuBtn.contains(e.target) && !menuContent.contains(e.target)) {
        menuContent.style.display = 'none';
    }
});
