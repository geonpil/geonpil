function showToast(message, isCancel = false) {
    const toast = document.getElementById("toast");
    toast.textContent = message;

    toast.classList.remove("cancel"); // 기존 클래스 제거
    if (isCancel) toast.classList.add("cancel"); // 취소인 경우 색상 다르게

    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show", "cancel");
    }, 2000);
}