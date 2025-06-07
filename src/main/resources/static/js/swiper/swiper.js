 window.addEventListener("load", function () {
    const swiper = new Swiper(".mobile-sub-banner", {
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

    // ✅ Swiper 수동 업데이트 (DOM 로딩 지연 대응)
    setTimeout(() => {
    swiper.update();
}, 200); // DOM, 이미지 레이아웃 반영 후
});
