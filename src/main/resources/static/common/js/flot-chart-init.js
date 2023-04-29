 (function($){

 "use strict"; // Start of use strict

 var SufeeAdmin = {

    cpuLoad: function(){
        jQuery.ajax({
            type: 'GET',
            url: '/setting/get_seq.do',
            dataType: "json",
            success: function (data) {

                var length = Object.keys(data).length;

                for (var i = 0; i < length; i++) {
                    let txData = [];
                    let rxData = [];
                    let totalCounts = 50;
                    let txInfo;
                    let rxInfo;

                    let systemSeq = data[i];
                    let webSocketURL = "ws://localhost:8080/webSocket/" + systemSeq;
                    let webSocket = new WebSocket(webSocketURL);

                    webSocket.onopen = function (event) {
                        console.log(systemSeq + " 연결이 열렸습니다.");
                    };

                    window.addEventListener("beforeunload", function (event) {
                        console.log(systemSeq + " 연결이 닫혔습니다.");
                        webSocket.close();
                    });

                    webSocket.onmessage = function (result) {

                        var obj = JSON.parse(result.data);
                        txInfo = obj.txInfo;
                        rxInfo = obj.rxInfo;

                        if (txData.length > 0) {
                            txData = txData.slice(1);
                        }

                        if (rxData.length > 0) {
                            rxData = rxData.slice(1);
                        }

                        /*실시간 차트 영역*/
                        while (txData.length < totalCounts && rxData.length < totalCounts) {
                            txData.push(txInfo);
                            rxData.push(rxInfo);

                            var txRes = [];
                            var rxRes = [];

                            var success = [];

                            for (var i = 0; i < txData.length; ++i) {
                                txRes.push([i, txData[i]]);
                            }

                            for (var i = 0; i < rxData.length; ++i) {
                                rxRes.push([i, rxData[i]]);
                            }

                            success.push("txRes", txRes);
                            success.push("rxRes", rxRes);

                            // 배열 길이가 totalCounts를 초과하면, 가장 오래된 데이터 삭제
                            if (txRes.length > totalCounts) txRes.shift();
                            if (rxRes.length > totalCounts) rxRes.shift();

                            /*외부 net 사용률 (Kbps)*/
                            var plot3 = $.plot("#net-load-" + systemSeq, [{
                                label: "InBound",
                                data: success[3],
                                color: "#fb2e26"
                            },{
                                label: "OutBound",
                                data: success[1],
                                color: "#fba11d"
                            }], {
                                xaxis: {show: false},
                                colors: ["#fba11d", "#fb2e26"],
                                grid: {hoverable: false, color: "transparent"},
                                tooltip: true,
                                legend: {
                                    position: "ne",
                                    show: true,
                                    labelFormatter: function(label) {
                                        return '<span style="color:#545454">'+ label + '</span>';
                                    }
                                },
                                yaxes: [{show: true}]
                            });

                            // 그래프 그리기 완료 후 로딩 문구 숨기기
                            $('#netLoading-' + systemSeq).remove();
                            plot3.setData([success[1], success[3]]);
                            plot3.draw();
                        }

                    }
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("정상적으로 소켓이 생성되지 않았습니다.");
            }
        });
    },
    barChart: function(){

        jQuery.ajax({
            type: 'GET',
            url: '/setting/get_seq.do',
            dataType: "json",
            success: function (data) {

                var myChart;

                const keys = data;
                const length = Object.keys(keys).length;

                for (let i = 0; i < length; i++) {
                    const key = keys[i];

                    $.ajax({
                        url: "/monitoring/totalCheck",
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({"systemSeq": key}),
                        success: function (data) {
                            let ctx = document.getElementById("barChart-" + key);

                            //let status = document.getElementById("serverStatus-" + key);
                            let cpu = data[0].cpu;
                            let mem = data[0].mem;
                            let disk = data[0].disk;
                            let status = "";
                            if (cpu >= 90 || mem >= 90 || disk >= 90) {
                                status = '<div style="width:100%;" class="btn btn-danger">위험</div>';
                            } else if (cpu >= 80 || mem >= 80 || disk >= 80) {
                                status = '<div style="width:100%;" class="btn btn-warning">경고</div>';
                            } else if (cpu >= 70 || mem >= 70 || disk >= 70) {
                                status = '<div style="width:100%;" class="btn btn-success">주의</div>';
                            } else {
                                status = '<div style="width:100%;" class="btn btn-primary">정상</div>';
                            }

                            $("#serverStatus-" + key).html(status);

                            ctx.height = 290;
                            myChart = new Chart(ctx, {
                                type: 'bar',
                                data: {
                                    labels: ["CPU", "MEM", "DISK"],
                                    datasets: [
                                        {
                                            label: ["CPU 사용률 (%)", "MEM 사용률 (%)", "DISK 사용률 (%)"],
                                            data: [data[0].cpu, data[0].mem, data[0].disk],
                                            borderColor: ["rgba(0, 194, 146, 0.9)", "rgba(255, 99, 132, 0.9)", "rgba(54, 162, 235, 0.9)"],
                                            borderWidth: "0",
                                            backgroundColor: ["rgba(0, 194, 146, 0.5)", "rgba(255, 99, 132, 0.5)", "rgba(54, 162, 235, 0.5)"]
                                        },
                                    ]
                                },
                                options: {
                                    maintainAspectRatio: false,
                                    responsive: false,
                                    scales: {
                                        xAxes: [{
                                            gridLines: {
                                                color: 'rgba(0, 0, 0, 0)' // x축 그리드 색상을 투명으로 설정
                                            },
                                            ticks: {
                                                autoSkip: true,
                                                maxRotation: 80,
                                                maxBarThickness: 5
                                            }
                                        }],
                                        yAxes: [{
                                            display: false,
                                            gridLines: {
                                                color: 'rgba(0, 0, 0, 0)' // y축 그리드 색상을 투명으로 설정
                                            },
                                            ticks: {
                                                max: 100,
                                                beginAtZero: true
                                            }
                                        }],
                                    },
                                    legend: {
                                        display: false
                                    },
                                    tooltips: {
                                        enabled: true,
                                        mode: 'index',
                                        callbacks: {
                                            label: function(tooltipItem, data) {
                                                // 툴팁에서 데이터 값만 표시
                                                return tooltipItem.yLabel + '%';
                                            }
                                        }
                                    }
                                }
                            });

                            // 그래프 그리기 완료 후 로딩 문구 숨기기
                            $('#diskLoading-' + key).remove();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.log("AJAX Error: " + textStatus + ", " + errorThrown);
                        }
                    });
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("비정상 오류");
            }
        });
    }
};

$(document).ready(function() {
    SufeeAdmin.cpuLoad();
    //SufeeAdmin.pieFlot();
    SufeeAdmin.barChart();

    /*$("#diskCheckClick").click(function () {
        SufeeAdmin.barChart();
    });*/
});
})(jQuery);
