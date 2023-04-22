/*!
    * Start Bootstrap - SB Admin v7.0.4 (https://startbootstrap.com/template/sb-admin)
    * Copyright 2013-2021 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
    */
    // 
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

});

//로그인
function userLogin(){

    var userId = $("#userId").val();
    var userPw = $("#userPw").val();
    var referrer = document.referrer;

    // 특정 문자열 발견시 공백으로 치환 : userId
    userId = userId.replace(/\</g,'');
    userId = userId.replace(/\>/g,'');
    userId = userId.replace(/\#/g,'');
    userId = userId.replace(/\$/g,'');
    userId = userId.replace(/\@/g,'');
    userId = userId.replace(/\^/g,'');
    userId = userId.replace(/\&/g,'');
    userId = userId.replace(/\*/g,'');
    userId = userId.replace(/\(/g,'');
    userId = userId.replace(/\)/g,'');
    userId = userId.replace(/\;/g,'');
    userId = userId.replace(/\=/g,'');
    userId = userId.replace(/\'/g,'');
    userId = userId.replace(/\"/g,'');


    $.ajax({
        type : "POST",
        url : "/login",
        dataType : "text",
        contentType: "application/json",
        data: JSON.stringify({
            "userId" : userId,
            "userPw" : userPw,
            "status" : 1
        }),
        async : false,
        error : function(){
            alert('로그인을 실패하였습니다. ID 또는 PW를 확인해주세요');
        },
        success : function(data){
            if(data == "1"){
                if(referrer != ""){
                    sessionStorage.setItem("userId", userId);
                    location.replace(referrer);
                }else {
                    sessionStorage.setItem("userId", userId);
                    location.replace("/");
                }
            }else if(data == "2") {
                alert('블랙리스트로 등록된 사용자 입니다. \n고객센터로 연락주시길 바랍니다.');
                location.replace("/login");
            }else{
                alert('로그인을 실패하였습니다. ID 또는 PW를 확인해주세요');
                location.replace("/login");
            }
        }
    });
}


//로그아웃
function userLogout(){

    var url = $(location).attr('href');

    $.ajax({
        type : "DELETE",
        url : "/login",
        dataType : "text",
        async : false,
        error : function(){
            alert('통신실패!!');
        },
        success : function(data){
            if(data == "logout"){
                alert("로그아웃 성공");

                if(url != ""){
                    sessionStorage.clear();
                    location.replace(url);
                }else {
                    sessionStorage.clear();
                    location.replace("/");
                }
            }else {
                alert("로그아웃 실패");

                location.replace("/");
            }
        }
    });
}

//관리자 로그인
function adminLogin(){

    var adminId = $("#adminId").val();
    var adminPw = $("#adminPw").val();

    // 특정 문자열 발견시 공백으로 치환 : userId
    adminId = adminId.replace(/\</g,'');
    adminId = adminId.replace(/\>/g,'');
    adminId = adminId.replace(/\#/g,'');
    adminId = adminId.replace(/\$/g,'');
    adminId = adminId.replace(/\@/g,'');
    adminId = adminId.replace(/\^/g,'');
    adminId = adminId.replace(/\&/g,'');
    adminId = adminId.replace(/\*/g,'');
    adminId = adminId.replace(/\(/g,'');
    adminId = adminId.replace(/\)/g,'');
    adminId = adminId.replace(/\;/g,'');
    adminId = adminId.replace(/\=/g,'');
    adminId = adminId.replace(/\'/g,'');
    adminId = adminId.replace(/\"/g,'');


    $.ajax({
        type : "POST",
        url : "/admin",
        dataType : "text",
        contentType: "application/json",
        data: JSON.stringify({
            "adminId" : adminId,
            "adminPw" : adminPw,
            "status" : 1
        }),
        async : false,
        error : function(){
            alert('로그인을 실패하였습니다. ID 또는 PW를 확인해주세요');
        },
        success : function(data){
            if(data == "1"){
                location.replace("/");
            }else {
                alert("로그인을 실패하였습니다. ID 또는 PW를 확인해주세요");

                location.replace("/");
            }
        }
    });
}

//관리자 로그아웃
function adminLogout(){
    $.ajax({
        type : "DELETE",
        url : "/admin",
        dataType : "text",
        async : false,
        error : function(){
            alert('통신실패!!');
        },
        success : function(data){
            if(data == "adminLogout"){
                alert("로그아웃 성공");

                location.replace("/admin");
            }else {
                alert("로그아웃 실패");

                location.replace("/admin");
            }
        }
    });
}