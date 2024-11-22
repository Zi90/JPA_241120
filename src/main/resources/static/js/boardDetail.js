console.log("board detail.js in!!");

document.getElementById('listBtn').addEventListener('click', ()=>{
    location.href = "/board/list";
});

document.getElementById('modBtn').addEventListener('click',()=>{
    document.getElementById('title').readOnly=false;
    document.getElementById('content').readOnly=false;

    // 버튼 생성
    let modBtn = document.createElement("button");
    modBtn.setAttribute("type","submit");
    modBtn.setAttribute("id","regBtn");
    modBtn.classList.add("btn","btn-outline-warning");
    modBtn.innerText="Submit";

    // 추가
    document.getElementById("modForm").appendChild(modBtn);
    document.getElementById("modBtn").remove();
    document.getElementById("delBtn").remove();

    // file -> fileUpload 버튼 disabled = false
    document.getElementById('trigger').disabled = false;

    let fileDelBtn = document.querySelectorAll(".file-x");
    console.log(fileDelBtn);
    for(let delBtn of fileDelBtn){
        delBtn.disabled = false;
    }
    
});

document.addEventListener('click',(e)=>{
    if(e.target.classList.contains('file-x')){
        console.log(e.target);
        let uuid = e.target.dataset.uuid;
        fileRemoveToServer(uuid).then(result => {
            if(result > 0){
                alert("파일 삭제 성공");
                e.target.closest('li').remove();
            }
        })
    }
});

// 비동기 데이터 보내기
async function fileRemoveToServer(uuid) {
    try{
        const url = '/board/file/'+uuid;
        const config = {
            method:"delete"
        }
        const resp = await fetch(url, config);
        const result = await resp.text();
        return result;
    }catch(error){
        console.log(error);
    }
}