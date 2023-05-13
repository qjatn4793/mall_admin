package com.shopping.mallAdmin.monitoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.shopping.mallAdmin.setting.service.SettingService;
import com.shopping.mallAdmin.setting.vo.SettingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    SettingService settingService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{

        /*확인용*/
        System.out.println("연결확인용");
        String sessionId = session.getId(); // 세션 ID
        URI uri = session.getUri(); // 연결된 URI
        HttpHeaders headers = session.getHandshakeHeaders(); // 연결 시 사용된 헤더
        Map<String, Object> attributes = session.getAttributes(); // 세션의 속성 맵
        Principal principal = session.getPrincipal(); // 연결된 사용자 주체
        InetSocketAddress remoteAddress = session.getRemoteAddress(); // 원격 주소
        InetSocketAddress localAddress = session.getLocalAddress(); // 로컬 주소
        StandardWebSocketSession standardSession = (StandardWebSocketSession) session; // 특정 구현체에 대한 형변환

        // 세션의 속성 맵에 저장된 정보 확인
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ": " + value);
        }

        System.out.println("Session ID: " + sessionId);
        System.out.println("URI: " + uri);
        System.out.println("Handshake Headers: " + headers);
        System.out.println("Principal: " + principal);
        System.out.println("Remote Address: " + remoteAddress);
        System.out.println("Local Address: " + localAddress);
        System.out.println("연결확인용");
        /*확인용*/

        // WebSocket 연결이 성공하였을 때 실행되는 코드

        System.out.println("웹소켓 연결시도");

        Map<Integer, Integer> seqMap = settingService.getSeq();

        for (Integer settingSeq : seqMap.values()) {
            if(session.getUri().getPath().equals("/webSocket/" + settingSeq)) {

                SettingVo settingVo = settingService.getSetting(settingSeq.toString());

                String host = settingVo.getSystemHost();
                String user = settingVo.getSystemUser();
                String password = settingVo.getSystemPassword();
                int port = Integer.parseInt(settingVo.getSystemPort());
                String os = settingVo.getSystemOs();

                JSch jsch = new JSch();
                Session sshSession = null;
                Map<String, String> result = new HashMap<>();

                String cpuInfo = "";
                String memInfo = "";
                String txInfo = "";
                String rxInfo = "";

                double txRate = 0.0;
                double rxRate = 0.0;

                if(user == null || host == null || password == null || port == 0) {
                    System.out.println("user, host, password, port 값이 비어있다.");
                }else {
                    if(os.equals("LINUX")) {
                        try {
                            // SSH 세션 생성
                            sshSession = jsch.getSession(user, host, port);
                            sshSession.setPassword(password);
                            sshSession.setConfig("StrictHostKeyChecking", "no");
                            sshSession.connect();

                            //String netStat = executeCommand(sshSession, "ifconfig | head -n 1 | awk -F ':' '{print $1}'");

                            // 1초마다 CPU 상태 체크 명령 실행
                            while (true) {
                                ChannelExec channel = (ChannelExec) sshSession.openChannel("exec");
                                //channel.setCommand("top -bn1 | grep load | awk '{printf \"%.2f\", $(NF-2)}'");
                                channel.connect();

                                //netStat = executeCommand(sshSession, "ifconfig | awk '/^[^ ]/{dev=$1} /inet / && dev != \"lo:\" {print \"Network interface: %s\\n\", dev; exit}'");

                                //cpuInfo = executeCommand(sshSession, "top -bn1 | grep load | awk '{printf \"%.2f\", $(NF-2)}'");
                                cpuInfo = executeCommand(sshSession, "ps -eo pid,%cpu --sort=-%cpu | head -n 2 | awk 'NR==2 {printf \"%.1f\", $2}'");

                                String sMemInfo = executeCommand(sshSession, "free -m | awk 'NR==2{printf \"%s/%sMB|%.2f\", $3,$2,$3*100/$2 }'");
                                memInfo = sMemInfo.split("\\|")[1];

                                String[] sNetInfo = executeCommand(sshSession, "cat /proc/net/dev | grep ens33 | awk '{print $2\" \"$10}'").split("\\s+");

                                if (sNetInfo[0].equals("")) {
                                    sNetInfo = executeCommand(sshSession, "cat /proc/net/dev | grep eth0 | awk '{print $2\" \"$10}'").split("\\s+");
                                    /*if (sNetInfo[0].equals(" ")) {
                                        sNetInfo = executeCommand(sshSession, "cat /proc/net/dev | grep eth1 | awk '{print $2\" \"$10}'").split("\\s+");
                                    }*/
                                }

                                if (!sNetInfo[0].isEmpty() && !sNetInfo[1].isEmpty()) {
                                    // 이전 데이터가 없을 경우 현재값을 저장
                                    if (txInfo.isEmpty() && rxInfo.isEmpty()) {
                                        txInfo = sNetInfo[0];
                                        rxInfo = sNetInfo[1];
                                    } else {
                                        // 이전값과 현재값의 차이를 이용하여 초당 전송량과 수신량을 계산
                                        double txDiff = Double.parseDouble(sNetInfo[0]) - Double.parseDouble(txInfo);
                                        double rxDiff = Double.parseDouble(sNetInfo[1]) - Double.parseDouble(rxInfo);

                                        txRate = txDiff / 5 * 8 / 1000;
                                        rxRate = rxDiff / 5 * 8 / 1000;

                                        txInfo = sNetInfo[0];
                                        rxInfo = sNetInfo[1];
                                    }
                                }

                                // 명령 실행 결과를 WebSocket 클라이언트로 전송
                                result.put("cpuInfo", cpuInfo);
                                result.put("memInfo", memInfo);
                                result.put("txInfo", txRate + "");
                                result.put("rxInfo", rxRate + "");

                                //System.out.println("내부" + result);

                                ObjectMapper objectMapper = new ObjectMapper();
                                String json = objectMapper.writeValueAsString(result);
                                session.sendMessage(new TextMessage(json));

                                // SSH 채널 닫기
                                channel.disconnect();

                                // 0.1초간 대기
                                Thread.sleep(10);
                            }
                        } catch (InterruptedException e) {
                            // 스레드가 interrupt 되었다면, while 문을 빠져나온다.
                            System.out.println("Interrupted");
                        } catch (Exception e) {
                            // 예외 처리
                            //e.printStackTrace();
                        } finally {
                            // SSH 연결 종료
                            sshSession.disconnect();
                        }
                    }else if (os.equals("WINDOW")) {

                        try {
                            // SSH 세션 생성
                            sshSession = jsch.getSession(user, host, port);
                            sshSession.setPassword(password);
                            sshSession.setConfig("StrictHostKeyChecking", "no");
                            sshSession.connect();

                            ObjectMapper objectMapper = new ObjectMapper();

                            // 1초마다 CPU 상태 체크 명령 실행
                            while (true) {
                                ChannelExec channel = (ChannelExec) sshSession.openChannel("exec");
                                channel.connect();

                                Pattern pattern = Pattern.compile("\\d+");

                                cpuInfo = win_executeCommand(sshSession, "wmic cpu get loadpercentage");
                                memInfo = win_executeCommand(sshSession, "wmic OS get FreePhysicalMemory,TotalVisibleMemorySize /Value");
                                String netInfo = win_executeCommand(sshSession, "netstat -e");

                                Matcher cpuMatcher = pattern.matcher(cpuInfo);
                                Matcher netMatcher = pattern.matcher(netInfo);

                                if (cpuMatcher.find()) {
                                    cpuInfo = cpuMatcher.group();
                                }

                                String[] lines = memInfo.split("\n");
                                long totalMemory = 0;
                                long freeMemory = 0;
                                for (String line : lines) {
                                    String[] parts = line.split("=");
                                    if (parts.length == 2) {
                                        String key = parts[0].trim();
                                        String value = parts[1].trim();
                                        if (key.equalsIgnoreCase("TotalVisibleMemorySize")) {
                                            totalMemory = Long.parseLong(value);
                                        } else if (key.equalsIgnoreCase("FreePhysicalMemory")) {
                                            freeMemory = Long.parseLong(value);
                                        }
                                    }
                                }

                                // 메모리 사용량 계산
                                long usedMemory = totalMemory - freeMemory;
                                double memoryUsage = (double) usedMemory / totalMemory * 100.0;

                                if (netMatcher.find()) {
                                    long bytesSent = Long.parseLong(netMatcher.group());
                                    if (netMatcher.find()) {
                                        long bytesReceived = Long.parseLong(netMatcher.group());
                                        if (txInfo.isEmpty() && rxInfo.isEmpty()) {
                                            txInfo = bytesSent + "";
                                            rxInfo = bytesReceived + "";
                                        } else {
                                            double win_txDiff = Double.parseDouble(bytesSent+"") - Double.parseDouble(txInfo);
                                            double win_rxDiff = Double.parseDouble(bytesReceived+"") - Double.parseDouble(rxInfo);

                                            txRate = win_txDiff / 5 * 8 / 1000;
                                            rxRate = win_rxDiff / 5 * 8 / 1000;

                                            txInfo = bytesSent + "";
                                            rxInfo = bytesReceived + "";
                                        }

                                        txInfo = bytesSent + "";
                                        rxInfo = bytesReceived + "";
                                    }
                                }

                                result.put("cpuInfo", cpuInfo);
                                result.put("memInfo", memoryUsage + "");
                                result.put("txInfo", txRate + "");
                                result.put("rxInfo", rxRate + "");

                                // 명령 실행 결과를 WebSocket 클라이언트로 전송
                                String json = objectMapper.writeValueAsString(result);
                                session.sendMessage(new TextMessage(json));

                                // SSH 채널 닫기
                                channel.disconnect();

                                // 0초간 대기
                                Thread.sleep(0);
                            }
                        }catch (InterruptedException e) {
                            // 스레드가 interrupt 되었다면, while 문을 빠져나온다.
                            System.out.println("Interrupted");
                        } catch (Exception e) {
                            // 예외 처리
                            //e.printStackTrace();
                        } finally {
                            // SSH 연결 종료
                            sshSession.disconnect();
                        }
                    }else {
                        System.out.println("잘못된 정보");
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            session.close();
        } catch (IOException e) {
            System.out.println(e + ": WebSocket session close error");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        // WebSocket 연결이 에러가 발생하였을 때 실행되는 코드
        // 예외 처리
        //exception.printStackTrace();
        System.out.println("웹소켓 연결해제");
    }

    private static String executeCommand(Session session, String command) {
        StringBuilder outputBuffer = new StringBuilder();
        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuffer.append(line);
            }
            reader.close();
            channel.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return outputBuffer.toString();
    }

    private static String win_executeCommand(Session session, String command) throws JSchException, IOException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setErrStream(System.err);
        InputStream inputStream = channel.getInputStream();
        channel.connect();
        byte[] buffer = new byte[1024];
        StringBuilder stringBuilder = new StringBuilder();
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, bytesRead));
        }
        channel.disconnect();
        return stringBuilder.toString();
    }

}