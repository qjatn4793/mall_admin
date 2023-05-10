 (function($){

 "use strict"; // Start of use strict

 var params = new URLSearchParams(window.location.search);
 var systemSeq = params.get("systemSeq");

 var SufeeAdmin = {

    cpuLoad: function(){
        let cpuData = [];
        let memData = [];
        let txData = [];
        let rxData = [];
        let totalCounts = 50;
        let cpuInfo;
        let memInfo;
        let txInfo;
        let rxInfo;

        //let systemSeq = data[i];
        let serverUrl = "59.12.242.207:8888";
        let webSocketURL = "ws://" + serverUrl + "/webSocket/" + systemSeq;
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

            cpuInfo = obj.cpuInfo;
            memInfo = obj.memInfo;
            txInfo = obj.txInfo;
            rxInfo = obj.rxInfo;

            if (cpuData.length > 0) {
                cpuData = cpuData.slice(1);
            }

            if (memData.length > 0) {
                memData = memData.slice(1);
            }

            if (txData.length > 0) {
                txData = txData.slice(1);
            }

            if (rxData.length > 0) {
                rxData = rxData.slice(1);
            }

            /*실시간 차트 영역*/
            while (cpuData.length < totalCounts && memData.length < totalCounts && txData.length < totalCounts && rxData.length < totalCounts) {

                cpuData.push(cpuInfo);
                memData.push(memInfo);
                txData.push(txInfo);
                rxData.push(rxInfo);

                var cpuRes = [];
                var memRes = [];
                var txRes = [];
                var rxRes = [];

                var success = [];

                for (var i = 0; i < cpuData.length; ++i) {
                    cpuRes.push([i, cpuData[i]]);
                }

                for (var i = 0; i < memData.length; ++i) {
                    memRes.push([i, memData[i]]);
                }

                for (var i = 0; i < txData.length; ++i) {
                    txRes.push([i, txData[i]]);
                }

                for (var i = 0; i < rxData.length; ++i) {
                    rxRes.push([i, rxData[i]]);
                }

                success.push("cpuRes", cpuRes);
                success.push("memRes", memRes);
                success.push("txRes", txRes);
                success.push("rxRes", rxRes);

                // 배열 길이가 totalCounts를 초과하면, 가장 오래된 데이터 삭제
                if (cpuRes.length > totalCounts) cpuRes.shift();
                if (memRes.length > totalCounts) memRes.shift();
                if (txRes.length > totalCounts) txRes.shift();
                if (rxRes.length > totalCounts) rxRes.shift();

                /*외부 cpu 사용률 (%)*/
                var plot = $.plot("#cpu-load-" + systemSeq, [{
                    label: "cpu 사용률",
                    data: success[1],
                    color: "#fba448",
                }], {
                    xaxis: {show: false},
                    colors: ["#fba448"],
                    grid: {hoverable: false/*, color: "transparent"*/, borderColor: "#ffffff"},
                    tooltip: true,
                    legend: {
                        position: "ne",
                        show: true,
                        labelFormatter: function(label) {
                            return '<span style="color:#545454">'+ label + '</span>';
                        }
                    }
                });

                // 그래프 그리기 완료 후 로딩 문구 숨기기
                $('#cpuLoading-' + systemSeq).remove();

                /*외부 mem 사용률 (%)*/
                var plot2 = $.plot("#mem-load-" + systemSeq, [{
                    label: "Memory 사용률",
                    data: success[3],
                    color: "#3f46fb",
                }], {
                    xaxis: {show: false},
                    colors: ["#3f46fb"],
                    grid: {hoverable: false/*, color: "transparent"*/, borderColor: "#ffffff"},
                    tooltip: true,
                    legend: {
                        position: "ne",
                        show: true,
                        labelFormatter: function(label) {
                            return '<span style="color:#545454">'+ label + '</span>';
                        }
                    }
                });

                // 그래프 그리기 완료 후 로딩 문구 숨기기
                $('#memLoading-' + systemSeq).remove();

                /*외부 net 사용률 (Kbps)*/
                var plot3 = $.plot("#net-load-" + systemSeq, [{
                    label: "InBound",
                    data: success[7],
                    color: "#fb2e26"
                },{
                    label: "OutBound",
                    data: success[5],
                    color: "#fba11d"
                }], {
                    xaxis: {show: false},
                    colors: ["#fba11d", "#fb2e26"],
                    grid: {hoverable: false/*, color: "transparent"*/, borderColor: "#ffffff"},
                    tooltip: true,
                    legend: {
                        position: "ne",
                        show: true,
                        labelFormatter: function(label) {
                            return '<span style="color:#545454">'+ label + '</span>';
                        }
                    }
                });

                // 그래프 그리기 완료 후 로딩 문구 숨기기
                $('#netLoading-' + systemSeq).remove();

                plot.setData([success[1]]);
                plot2.setData([success[3]]);
                plot3.setData([success[5], success[7]]);

                plot.draw();
                plot2.draw();
                plot3.draw();
            }
        }
    },
    barChart: function(){
        $.ajax({
            url: "/monitoring/diskCheck",
            type: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({"systemSeq": systemSeq}),
            success: function (data) {

                let dataList = data;
                let labels = [];
                let values = [];

                for (var i = 0; i < dataList.length; i++) {
                    labels.push(dataList[i].diskName);
                    values.push(dataList[i].diskInfo);
                }

                let ctx = document.getElementById("barChart-" + systemSeq);
                let height = 300;

                ctx.height = 170;
                var myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [
                            {
                                label: "DISK 사용률 (%)",
                                data: values,
                                borderColor: "rgba(0, 194, 146, 0.9)",
                                borderWidth: "0",
                                backgroundColor: "rgba(0, 194, 146, 0.5)"
                            }
                        ]
                    },
                    options: {
                        maintainAspectRatio: false,
                        responsive: true,
                        height: height,
                        scales: {
                            xAxes: [{
                                ticks: {
                                    autoSkip: true,
                                    maxRotation: 80,
                                    maxBarThickness: 5
                                }
                            }],
                            yAxes: [{
                                ticks: {
                                    beginAtZero: true
                                }
                            }]
                        }
                    }
                });

                // 그래프 그리기 완료 후 로딩 문구 숨기기
                $('#diskLoading-' + systemSeq).remove();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("AJAX Error: " + textStatus + ", " + errorThrown);
            }
        });
    },
     pieFlot: function(){

        var cpu;
        var mem;

         $.ajax({
             url: "/monitoring/totalCheck",
             type: "POST",
             dataType: "json",
             contentType: "application/json; charset=utf-8",
             data: JSON.stringify({"systemSeq": systemSeq}),
             success: function (data) {
                 cpu = data[0].cpu;
                 mem = data[0].mem;

                 var cpu_data = [
                     {
                         label: "여유",
                         data: 100 - cpu,
                         color: "#ff8b00"
                     },
                     {
                         label: "사용량",
                         data: cpu,
                         color: "#ffb74d"
                     }
                 ];

                 var plotObj = $.plot( $( "#flot-pie" ), cpu_data, {
                     series: {
                         pie: {
                             show: true,
                             radius: 1,
                             label: {
                                 show: false,

                             }
                         }
                     },
                     grid: {
                         hoverable: true
                     },
                     tooltip: {
                         show: true,
                         content: "%p.0%, %s, n=%n", // show percentages, rounding to 2 decimal places
                         shifts: {
                             x: 20,
                             y: 0
                         },
                         defaultTheme: false
                     }
                 } );

                 var mem_data = [
                     {
                         label: "여유",
                         data: 100 - mem,
                         color: "#63a8ea"
                     },
                     {
                         label: "사용량",
                         data: mem,
                         color: "#b2ebf2"
                     }
                 ];

                 var plotObj2 = $.plot( $( "#flot-pie2" ), mem_data, {
                     series: {
                         pie: {
                             show: true,
                             radius: 1,
                             label: {
                                 show: false,

                             }
                         }
                     },
                     grid: {
                         hoverable: true
                     },
                     tooltip: {
                         show: true,
                         content: "%p.0%, %s, n=%n", // show percentages, rounding to 2 decimal places
                         shifts: {
                             x: 20,
                             y: 0
                         },
                         defaultTheme: false
                     }
                 } );
             }
         });
     }
};

$(document).ready(function() {
    SufeeAdmin.cpuLoad();
    SufeeAdmin.pieFlot();
    SufeeAdmin.barChart();

    /*$("#diskCheckClick").click(function () {
        SufeeAdmin.barChart();
    });*/
});
})(jQuery);
