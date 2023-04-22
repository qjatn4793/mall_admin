window.addEventListener('DOMContentLoaded', event => {
    // Simple-DataTables
    // https://github.com/fiduswriter/Simple-DataTables/wiki

    var productSelect = $("#product-select option:selected").val(); // 한 페이지에 나타낼 글 수
    var currentPage = 1; // 선택한 페이지

    product();

    $("#product-select").change(function(){
        productSelect = $("#product-select option:selected").val(); // 한 페이지에 나타낼 글 수
        currentPage = 1;
        product();
    });

    function product(searchItem, selectCategory){
        var str = "";
        var ajaxType = "";

        if(searchItem == undefined){
            searchItem = "";
        }

        if(selectCategory == undefined){
            selectCategory = "선택";
        }



        if (searchItem != "" || selectCategory != "선택"){
            ajaxType = "POST";
            var param;

            if(searchItem == ""){
                param = {"productCategory":selectCategory}
            }else {
                param = {"searchItem":searchItem, "productCategory":selectCategory};
            }
        }else {
            ajaxType = "GET";
        }

        $.ajax({
            type: ajaxType,
            url: "/productView",
            dataType: "JSON",
            contentType: "application/json",
            data: JSON.stringify(param),
            async: false,
            error: function () {
                alert('통신실패!!');
            },
            success: function (data) {
                var paging = "";

                var totalProduct = Object.keys(data).length;
                var endPage = Math.ceil(totalProduct / productSelect);

                var endNum = productSelect * currentPage; // 현재 페이지 상품 끝
                var prevNum = endNum - productSelect; // 현재 페이지 상품 시작

                for (let i = prevNum; endNum > i; i++) {
                /*for (var i = 0; Object.keys(data).length > i; i++) {*/
                    if (Object.keys(data).length > i) {
                        let productSeq = data[Object.keys(data)[i]].productSeq;
                        let productName = data[Object.keys(data)[i]].productName;
                        let productLikes = data[Object.keys(data)[i]].productLikes;
                        let productViews = data[Object.keys(data)[i]].productViews;
                        let productCategory = data[Object.keys(data)[i]].productCategory;
                        let regDate = data[Object.keys(data)[i]].regDate;
                        let modDate = data[Object.keys(data)[i]].modDate;
                        let productThumbImg = data[Object.keys(data)[i]].productThumbImg;

                        if(productCategory == null){
                            productCategory = "";
                        }

                        if (productThumbImg == "" || productThumbImg == null || productThumbImg == 0) {
                            productThumbImg = "https://dummyimage.com/450x300/dee2e6/6c757d.jpg";
                        } else {
                            productThumbImg = productThumbImg.replace("\\", "/");
                            productThumbImg = "common/img" + productThumbImg;
                        }
                        if(productName != null) { // 검색 결과가 있으면
                            str += "<div class='col mb-5'>" +
                                "<div class='card h-100'>" +
                                "<a href='#' class='badge bg-dark text-white position-absolute' style='top:0.5rem; right:0.5rem'>"+ productCategory +"</a>" +
                                "<a class='btn mt-auto' href='/detailProduct/" + productSeq + "'>" +
                                "<img class='card-img-top' src='" + productThumbImg + "' alt='...' />" +
                                "</a>" +
                                "<div class='card-body p-4'>" +
                                "<div class='text-center'>" +
                                "<a class='btn mt-auto' href='/detailProduct/" + productSeq + "'>" +
                                "<h5 class='fw-bolder'>" + productName + "</h5>" +
                                "</a><br>" +
                                "조회 수 : " + productViews +
                                "</div>" +
                                "</div>" +
                                "<div class='card-footer p-4 pt-0 border-top-0 bg-transparent'>" +
                                "<div class='text-center'><a class='btn btn-outline-dark mt-auto' href='/detailProduct/" + productSeq + "'>View options</a></div>" +
                                "</div>" +
                                "</div>" +
                                "</div>";

                            $(".dataTable-info").text("Showing " + (prevNum + 1) + " to " + endNum + " of"+ " entries");
                        }else { // 검색 결과가 없으면
                            str += "<div>검색결과가 없습니다.</div>"

                            $(".dataTable-info").text("");
                        }

                        $("div.main-product").html(str); // 상품들 실제로 뿌려주는 부분

                    }
                }

                paging += '<li class="start"><a href="#" data-page="<"><<</a></li>'; // 처음 버튼 생성 영역
                paging += '<li class="prev"><a href="#" data-page="<"><</a></li>'; // 이전 버튼 생성 영역

                // 페이징 버튼 생성 영역 시작
                if ( (currentPage + 3) >= endPage ) {

                    if (currentPage > 1){
                        for (let j = currentPage - ((currentPage + 5) - endPage); currentPage - 1 > j; j++){
                            if (j > -1) {
                            /*if (j != -1) {*/
                                paging += '<li class="paging"><a href="#" data-page="' + (j + 1) + '">' + (j + 1) + '</a></li>';
                            }
                        }
                    }
                    for (let j = currentPage; endPage + 1> j; j++) {
                        if (j == currentPage) {
                            paging += '<li class="active paging"><a href="#" data-page="' + j + '">' + j + '</a></li>';
                        } else {
                            paging += '<li class="paging"><a href="#" data-page="' + j + '">' + j + '</a></li>';
                        }
                    }
                }else {
                    if (currentPage < 3 ) {
                        if ((currentPage - 1) > 1) {
                            for (let j = currentPage - 3; currentPage - 1 > j; j++){
                                paging += '<li class="paging"><a href="#" data-page="' + (j + 1) + '">' + (j + 1) + '</a></li>';
                            }
                        }else {
                            if (currentPage > 1){
                                for (let j = currentPage - 2; currentPage - 1 > j; j++){
                                    paging += '<li class="paging"><a href="#" data-page="' + (j + 1) + '">' + (j + 1) + '</a></li>';
                                }
                            }
                        }
                        if (currentPage != 2) {
                            for (let j = currentPage; currentPage + 5 > j; j++) {
                                if(j == currentPage) {
                                    paging += '<li class="active paging"><a href="#" data-page="'+ j +'">'+ j +'</a></li>';
                                }else {
                                    paging += '<li class="paging"><a href="#" data-page="'+ j +'">'+ j +'</a></li>';
                                }
                            }
                        }else {
                            for (let j = currentPage; currentPage + 4 > j; j++) {
                                if( j == currentPage ) {
                                    paging += '<li class="active paging"><a href="#" data-page="'+ j +'">'+ j +'</a></li>';
                                }else {
                                    paging += '<li class="paging"><a href="#" data-page="'+ j +'">'+ j +'</a></li>';
                                }
                            }
                        }
                    }else {
                        for (let j = currentPage - 3; currentPage - 1 > j; j++){
                            paging += '<li class="paging"><a href="#" data-page="' + (j + 1) + '">' + (j + 1) + '</a></li>';
                        }

                        for (let j = currentPage; currentPage + 3 > j; j++) {
                            if(j == currentPage) {
                                paging += '<li class="active paging"><a href="#" data-page="'+ j +'">'+ j +'</a></li>';
                            }else {
                                paging += '<li class="paging"><a href="#" data-page="'+ j +'">'+ j +'</a></li>';
                            }
                        }
                    }
                }
                // 페이징 버튼 생성 영역 끝

                paging += '<li class="next"><a href="#" data-page=">">></a></li>'; // 다음 버튼 생성 영역
                paging += '<li class="end"><a href="#" data-page="<">>></a></li>'; // 마지막 버튼 생성 영역

                if(endPage != 1){
                    $(".dataTable-pagination-list").html(paging); // 페이징 버튼 실제로 뿌려주는 부분
                }else if(endPage == 1){
                    paging = "";
                    $(".dataTable-pagination-list").html(paging); // 페이징 버튼 실제로 뿌려주는 부분
                }


                $(".dataTable-pagination-list li.paging").click(function(){ // 페이징 번호 클릭 시

                    $(".dataTable-pagination-list li.active").removeClass("active");
                    $(this).addClass("active");

                    currentPage = $(".dataTable-pagination-list li.active a").text();
                    currentPage = parseInt(currentPage);
                    product(searchItem, selectCategory);
                });

                $(".dataTable-pagination-list li.next").click(function(){ // 다음 버튼 클릭 시

                    $(".dataTable-pagination-list li.active").removeClass("active");
                    $(this).addClass("active");

                    if(endPage != currentPage){
                        currentPage += 1;
                    }
                    product(searchItem, selectCategory);
                });

                $(".dataTable-pagination-list li.prev").click(function(){ // 이전 버튼 클릭 시

                    $(".dataTable-pagination-list li.active").removeClass("active");
                    $(this).addClass("active");

                    if(currentPage != 1) {
                        currentPage -= 1;
                    }
                    product(searchItem, selectCategory);
                });

                $(".dataTable-pagination-list li.start").click(function(){ // 처음 버튼 클릭 시

                    $(".dataTable-pagination-list li.active").removeClass("active");
                    $(this).addClass("active");

                    currentPage = 1;
                    product(searchItem, selectCategory);
                });

                $(".dataTable-pagination-list li.end").click(function(){ // 마지막 버튼 클릭 시

                    $(".dataTable-pagination-list li.active").removeClass("active");
                    $(this).addClass("active");

                    currentPage = endPage;
                    product(searchItem, selectCategory);
                });
            }
        });
    }

    $("#searchBtn").click(function (){ // 검색 버튼 클릭 시
        if($(".dataTable-input").val() == ""){
            alert("검색어를 입력해주세요");
            window.location.reload(true);
        }else {
            product($(".dataTable-input").val(), $("#productCategory option:selected").val());
        }
    });

    const datatablesSimple = document.getElementById('datatablesSimple');
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple);
    }

});
