window.addEventListener("load", function () {
    new Swiper(".mobile-sub-banner", {
        slidesPerView: 1,
        spaceBetween: 0,
        loop: true,
        centeredSlides: false,
        autoplay: {
            delay: 3000,       // ⏱️ 3초마다 전환
            disableOnInteraction: false, // 사용자 스와이프 후에도 계속 자동재생
        },
        pagination: {
            el: ".swiper-pagination",
            clickable: true,
            dynamicBullets: false,
            renderBullet: function (index, className) {
                return `<span class="${className}"></span>`;
            },
        },
    });

    new Swiper(".book-carousel", {
        slidesPerView: 5,
        spaceBetween: 1,
        navigation: {
            nextEl: ".book-carousel .swiper-button-next",
            prevEl: ".book-carousel .swiper-button-prev"
        },
        autoplay: {
            delay: 2000,       // ⏱️ 3초마다 전환
            disableOnInteraction: false, // 사용자 스와이프 후에도 계속 자동재생
        },
        breakpoints: {
            1024: {
                slidesPerView: 5,
            },
            768: {
                slidesPerView: 4,
            },
            480: {
                slidesPerView: 2,
            },
            360: {
                slidesPerView: 2,
            }
        },
        loop: true
    });


    new Swiper(".book-pick-carousel", {
        slidesPerView: 2,
        spaceBetween: 1,
        navigation: {
            nextEl: ".swiper-book-pick-button-next",
            prevEl: ".swiper-book-pick-button-prev"
        },
        breakpoints: {
            1024: {
                slidesPerView: 2,
            },
            360: {
                slidesPerView: 1,
            }

        },
        loop: true
    });


});
