let startDate, endDate, today;

function initContestDateFields() {
    startDate = document.getElementById("startDate");
    endDate = document.getElementById("endDate");

    if (!startDate || !endDate) return;

    // 오늘 날짜 전역에 할당
    today = new Date();

    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;

    // 내일 날짜 계산
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    const tmm = String(tomorrow.getMonth() + 1).padStart(2, '0');
    const tdd = String(tomorrow.getDate()).padStart(2, '0');
    const tomorrowStr = `${yyyy}-${tmm}-${tdd}`;

    // 날짜 필드 설정
    startDate.value = todayStr;
    endDate.setAttribute("min", tomorrowStr);
}