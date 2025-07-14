export function initNicknameChange () {
    // 닉네임 변경 위함
    const nicknameInput = document.getElementById("nickname");
    const checkDiv = document.getElementById("nicknameCheck");
    const submitNicknameBtn = document.getElementById("changeNicknameBtn");


    function isValidNickname(nickname) {
        const regex = /^[가-힣a-zA-Z0-9]{2,12}$/;
        return regex.test(nickname);
    }


    nicknameInput.addEventListener("input", function () {
        const nickname = nicknameInput.value;

        if (nickname.length < 2 || nickname.length > 12) {
            checkDiv.textContent = "2자 이상 12자 이하로 입력해주세요.";
            checkDiv.className = "hint invalid";
            submitNicknameBtn.disabled = true;
            return;
        }

        if(!isValidNickname(nickname)) {
            checkDiv.textContent = "닉네임은 한글, 영문, 숫자만 가능합니다.";
            checkDiv.className = "hint invalid";
            submitNicknameBtn.disabled = true;
            return;
        }

        csrfFetch("/api/users/check-nickname?nickname=" + encodeURIComponent(nickname))
            .then(res => res.json())
            .then(data => {
                if (data.exists) {
                    checkDiv.textContent = "이미 사용 중인 닉네임입니다.";
                    checkDiv.className = "hint invalid";
                    submitNicknameBtn.disabled = true;
                } else {
                    checkDiv.textContent = "사용 가능한 닉네임입니다.";
                    checkDiv.className = "hint valid";
                    submitNicknameBtn.disabled = false;
                }
            });
    });

    document.getElementById("changeNicknameBtn").addEventListener("click", function (e) {
        e.preventDefault();

        csrfFetch("/api/users/nickname", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ nickname: nicknameInput.value })
        })
            .then(res => res.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    location.reload(); // 변경된 닉네임 반영
                }
            });
    });
}

