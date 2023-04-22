window.addEventListener('DOMContentLoaded', event => {

    $.ajax({
        type : "GET",
        url : "/board",
        dataType : "JSON",
        async : false,
        error : function(){
            alert('통신실패!!');
        },
        success : function(data){

            var str = "";

            for(var i = 0; Object.keys(data).length > i; i++){

                var boardSeq = data[Object.keys(data)[i]].boardSeq;
                var boardTitle = data[Object.keys(data)[i]].boardTitle;
                var boardContents = data[Object.keys(data)[i]].boardContents;
                var boardWriter = data[Object.keys(data)[i]].boardWriter;
                var boardLikes = data[Object.keys(data)[i]].boardLikes;
                var boardViews = data[Object.keys(data)[i]].boardViews;
                var regDate = data[Object.keys(data)[i]].regDate;
                var modDate = data[Object.keys(data)[i]].modDate;

                str += "<tr>" +
                    "<td onclick=location.href='/detailBoard/"+ boardSeq + "'>" + boardSeq + "</td>" +
                    "<td onclick=location.href='/detailBoard/"+ boardSeq + "' style='width:20%;'>" + boardTitle + "</td>" +
                    "<td onclick=location.href='/detailBoard/"+ boardSeq + "' style='width:30%;'>" + boardContents + "</td>" +
                    "<td>" + boardLikes + "</td>" +
                    "<td>" + boardViews + "</td>" +
                    "<td>" + boardWriter + "</td>" +
                    "<td>" + regDate + "</td>" +
                    /*"<td>" + modDate + "</td>" +*/
                    "</tr>";

                $("tbody.board").html(str);
            }
        }
    });

    const datatablesSimple = document.getElementById('datatablesSimple');
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple);
    }

});
