function openImageModal(img) {
  const modal = document.getElementById("imageModal");
  const modalImg = document.getElementById("expandedImg");
  modal.style.display = "block";
  modalImg.src = img.src;
}

function closeImageModal() {
  document.getElementById("imageModal").style.display = "none";
}

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
  if (event.key === "Escape") {
      closeImageModal();
  }
});

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
  const modal = document.getElementById("imageModal");
  if (event.target === modal) {
      modal.style.display = "none";
  }
}
