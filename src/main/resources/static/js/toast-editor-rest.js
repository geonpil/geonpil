document.addEventListener("DOMContentLoaded", function () {
  const editor = new toastui.Editor({
    el: document.querySelector('#editor'),
    height: '400px',
    initialEditType: 'wysiwyg',
    previewStyle: 'vertical',
    placeholder: '내용을 입력하세요...',
    toolbarItems: [
      ['heading', 'bold', 'italic', 'strike'],
      ['hr', 'quote'],
      ['ul', 'ol', 'task'],
      ['table', 'link', 'image'],
      ['code', 'codeblock']
    ],
    hooks: {
      addImageBlobHook: async (blob, callback) => {
        const formData = new FormData();
        formData.append('image', blob);
        try {
          const res = await fetch('/upload-image', {
            method: 'POST',
            body: formData
          });
          const result = await res.json();
          callback(result.url, '업로드 이미지');
        } catch (e) {
          alert("이미지 업로드 실패");
        }
      }
    }
  });

  window.submitToastEditor = function (data) {
    data.content = editor.getHTML();

    fetch("/api/contest", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    })
      .then(res => {
        if (res.ok) {
          alert("등록 완료");

          window.location.href = "/contest/list?boardCode=" + data.boardCode;
        } else {
          alert("등록 실패");
        }
      })
      .catch(err => {
        console.error(err);
        alert("에러 발생");
      });
  };
});
