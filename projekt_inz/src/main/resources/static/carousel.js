document.addEventListener('DOMContentLoaded', function () {
    const carousel = document.querySelector('.carousel');
    const prevButton = document.querySelector('.carousel__prev');
    const nextButton = document.querySelector('.carousel__next');
    const slides = document.querySelectorAll('.carousel__slide');
    let currentIndex = 0;
    let autoScrollInterval;

    function updateCarousel() {
        const slideWidth = slides[0].offsetWidth;
        carousel.style.transform = `translateX(-${currentIndex * slideWidth}px)`;
    }

    // Funkcja do uruchamiania automatycznego przesuwania
    function startAutoScroll() {
        autoScrollInterval = setInterval(function () {
            if (currentIndex < slides.length - 1) {
                currentIndex++;
            } else {
                currentIndex = 0;
            }
            updateCarousel();
        }, 3000); // Co 3 sekundy
    }

    // Funkcja do zatrzymywania automatycznego przesuwania
    function stopAutoScroll() {
        clearInterval(autoScrollInterval);
    }

    nextButton.addEventListener('click', function () {
        stopAutoScroll(); // Zatrzymaj auto-scroll przy kliknięciu
        if (currentIndex < slides.length - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateCarousel();
        startAutoScroll(); // Uruchom auto-scroll ponownie
    });

    prevButton.addEventListener('click', function () {
        stopAutoScroll(); // Zatrzymaj auto-scroll przy kliknięciu
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = slides.length - 1;
        }
        updateCarousel();
        startAutoScroll(); // Uruchom auto-scroll ponownie
    });

    // Rozpocznij automatyczne przewijanie po załadowaniu strony
    startAutoScroll();
});

