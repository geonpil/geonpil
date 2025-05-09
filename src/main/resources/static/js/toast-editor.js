  document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("postForm");
    const hiddenContent = document.getElementById("content-hidden");
    const original = document.getElementById("original-content"); // 수정 모드에서만 존재

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

    // 수정 모드일 경우 content 채워넣기
    if (original) {
      editor.setHTML(original.value);
    }

    form.addEventListener('submit', function () {
      hiddenContent.value = editor.getHTML();
      console.log("📤 전송할 content:", hiddenContent.value);
    });
  });
