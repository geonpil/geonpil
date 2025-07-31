(function(){
  // 드롭다운 토글
  document.addEventListener('click', e=>{
    const toggler = e.target.closest('.author-name');
    const all = document.querySelectorAll('.author-dropdown');
    all.forEach(el=>el.classList.remove('open'));

    if(toggler){
      e.preventDefault();
      toggler.parentElement.classList.toggle('open');
      // Prevent other click handlers on the document from running so the menu stays open
      e.stopImmediatePropagation();
    }
  });
  // 바깥 클릭 시 드롭다운 닫기
  document.addEventListener('click', ()=> {
    document.querySelectorAll('.author-dropdown')
            .forEach(el=>el.classList.remove('open'));
  });
})();