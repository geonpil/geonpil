export function initPasswordChange () {

    // 비밀번호 체크 위함
    const newPw = document.getElementById("newPassword");
    const confirmPw = document.getElementById("confirmPassword");
    const pwHint = document.getElementById("passwordValidation");
    const confirmHint = document.getElementById("confirmValidation");
    const submitPasswordBtn = document.getElementById("changePasswordBtn");





    document.querySelector("#changePasswordBtn").addEventListener("click", function () {
        const currentPassword = document.querySelector("#currentPassword").value;
        const newPassword = document.querySelector("#newPassword").value;
        const confirmPassword = document.querySelector("#confirmPassword").value;

        fetch("/api/users/password", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                currentPassword,
                newPassword,
                confirmPassword
            })
        })
            .then(res => res.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '/logout';
                    document.body.appendChild(form);
                    form.submit();
                }
            });
    });


    function validate() {
        const pw = newPw.value;
        const confirm = confirmPw.value;

        const isValidPw = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(pw);
        const isConfirmed = pw === confirm;

        // 비밀번호 유효성
        if (isValidPw) {
            pwHint.textContent = "사용 가능한 비밀번호입니다.";
            pwHint.className = "hint valid";
        } else {
            pwHint.textContent = "영문과 숫자를 포함한 8자 이상이어야 합니다.";
            pwHint.className = "hint invalid";
        }

        // 확인 비밀번호 체크
        if (confirm.length > 0) {
            if (isConfirmed) {
                confirmHint.textContent = "비밀번호가 일치합니다.";
                confirmHint.className = "hint valid";
            } else {
                confirmHint.textContent = "비밀번호가 일치하지 않습니다.";
                confirmHint.className = "hint invalid";
            }
        } else {
            confirmHint.textContent = "";
        }

        // 버튼 활성화 조건
        submitPasswordBtn.disabled = !(isValidPw && isConfirmed);
    }

    newPw.addEventListener("input", validate);
    confirmPw.addEventListener("input", validate);
}