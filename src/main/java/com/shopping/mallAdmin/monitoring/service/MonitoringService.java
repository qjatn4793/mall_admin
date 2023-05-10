package com.shopping.mallAdmin.monitoring.service;

import com.jcraft.jsch.*;
import com.shopping.mallAdmin.setting.service.SettingService;
import com.shopping.mallAdmin.setting.vo.SettingVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Service("monitoringService")
public class MonitoringService {

    @Autowired
    SettingService settingService;

    public List<Map<String, String>> totalCheck(@RequestBody SettingVo settingVo) throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        List<Map<String, String>> result = new ArrayList<>();

        SettingVo settingVo2 = settingService.getSetting(settingVo.getSystemSeq());

        String host = settingVo2.getSystemHost();
        String user = settingVo2.getSystemUser();
        String password = settingVo2.getSystemPassword();
        int port = Integer.parseInt(settingVo2.getSystemPort());
        String os = settingVo2.getSystemOs();

        if (user == null || host == null || password == null || port == 0) {
            System.out.println("user, host, password, port 값이 비어있다.");
            return null;
        }

        if (os.equals("LINUX")) {
            try {
                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                // cpu
                //String cpuInfo = executeCommand(session, "top -bn1 | grep load | awk '{printf \"%.2f\", $(NF-2)}'");
                String cpuInfo = executeCommand(session, "ps -eo pid,%cpu --sort=-%cpu | head -n 2 | awk 'NR==2 {printf \"%.1f\", $2}'");

                // mem
                String sMemInfo = executeCommand(session, "free -m | awk 'NR==2{printf \"%s/%sMB|%.2f\", $3,$2,$3*100/$2 }'");
                String memInfo = sMemInfo.split("\\|")[1];

                // disk
                String diskInfo = executeCommand(session, "df -h | awk -v RS='\\r?\\n' '{print $5\",\"}' | sed '1d'");
                String[] diskInfoArr = diskInfo.split("%,");

                Map<String, String> map = new HashMap<>();

                int maxDiskUsage = 0;
                for (int i = 0; i < diskInfoArr.length; i++) {
                    int usagePercentage = Integer.parseInt(diskInfoArr[i]);
                    if (usagePercentage > maxDiskUsage) {
                        maxDiskUsage = usagePercentage;
                    }
                }

                map.put("cpu", cpuInfo + "");
                map.put("mem", memInfo + "");
                map.put("disk", maxDiskUsage + "");
                result.add(map);

            } catch (JSchException e) {
                //e.printStackTrace();
                System.out.println("서버 접속 실패");
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        } else if (os.equals("WINDOW")) {

            try {
                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                Map<String, String> map = new HashMap<>();

                String cpuInfo = win_executeCommand(session, "wmic cpu get loadpercentage");
                String memInfo = win_executeCommand(session, "wmic OS get FreePhysicalMemory,TotalVisibleMemorySize /Value");
                String diskInfo = win_executeCommand(session, "wmic logicaldisk get size,freespace,caption");

                Pattern pattern = Pattern.compile("\\d+");
                Matcher cpuMatcher = pattern.matcher(cpuInfo);
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


                String[] diskLines = diskInfo.split("\n");
                double maxUsage = 0.0;
                String maxUsageDrive = "";
                for (int i = 1; i < diskLines.length; i++) {
                    String[] values = diskLines[i].trim().split("\\s+");
                    if (values.length == 3) {
                        String drive = values[0];
                        long size = Long.parseLong(values[1]);
                        long freeSpace = Long.parseLong(values[2]);
                        double usage = (freeSpace - size) / (double) freeSpace;
                        if (usage > maxUsage) {
                            maxUsage = usage;
                            maxUsageDrive = drive;
                        }
                    }
                }
                double maxUsagePercent = maxUsage * 100.0;

                map.put("cpu", cpuInfo);
                map.put("mem", String.format("%.2f", memoryUsage));
                map.put("disk", String.format("%.2f", maxUsagePercent));
                result.add(map);

            } catch (JSchException e) {
                //e.printStackTrace();
                System.out.println("서버 접속 실패");
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }

        } else {
            System.out.println("컨트롤러 잘못된정보");
        }

        return result;
    }

    public List<Map<String, String>> diskCheck(@RequestBody SettingVo settingVo) throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        List<Map<String, String>> result = new ArrayList<>();

        SettingVo settingVo2 = settingService.getSetting(settingVo.getSystemSeq());

        String host = settingVo2.getSystemHost();
        String user = settingVo2.getSystemUser();
        String password = settingVo2.getSystemPassword();
        int port = Integer.parseInt(settingVo2.getSystemPort());
        String os = settingVo2.getSystemOs();

        if (user == null || host == null || password == null || port == 0) {
            System.out.println("user, host, password, port 값이 비어있다.");
            return null;
        }

        if (os.equals("LINUX")) {
            try {
                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                String diskName = executeCommand(session, "df -h | awk -v RS='\\r?\\n' '{print $1\",\"}' | sed '1d'");
                String diskInfo = executeCommand(session, "df -h | awk -v RS='\\r?\\n' '{print $5\",\"}' | sed '1d'");

                String[] diskNameArr = diskName.split(",");
                String[] diskInfoArr = diskInfo.split("%,");

                for (int i = 0; i < diskNameArr.length; i++) {
                    Map<String, String> map = new HashMap<>();
                    map.put("diskName", diskNameArr[i]);
                    map.put("diskInfo", diskInfoArr[i]);
                    result.add(map);
                }

            } catch (JSchException e) {
                //e.printStackTrace();
                System.out.println("서버 접속 실패");
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        } else if (os.equals("WINDOW")) {

            try {
                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                String diskInfo = win_executeCommand(session, "wmic logicaldisk get size,freespace,caption");

                String[] disks = diskInfo.split("\n");
                for (int i = 1; i < disks.length; i++) {
                    String[] diskInfoArr = disks[i].trim().split("\\s+");
                    if (diskInfoArr.length == 3) {
                        Map<String, String> map = new HashMap<>();
                        map.put("diskName", diskInfoArr[0]);
                        long size = Long.parseLong(diskInfoArr[2]);
                        long freeSpace = Long.parseLong(diskInfoArr[1]);
                        int usedPercentage = (int) ((size - freeSpace) * 100 / size);

                        map.put("diskInfo", usedPercentage + "");
                        result.add(map);
                    }
                }

            } catch (JSchException e) {
                //e.printStackTrace();
                System.out.println("서버 접속 실패");
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }

        } else {
            System.out.println("컨트롤러 잘못된정보");
        }

        return result;
    }


    public Map<String, String> serviceCheck(String systemSeq) throws Exception{

        JSch jsch = new JSch();
        Session session = null;
        Map<String, String> map = new HashMap<>();

        SettingVo settingVo = settingService.getSetting(systemSeq);

        String host = settingVo.getSystemHost();
        String user = settingVo.getSystemUser();
        String password = settingVo.getSystemPassword();
        int port = Integer.parseInt(settingVo.getSystemPort());
        String os = settingVo.getSystemOs();

        if (user == null || host == null || password == null || port == 0) {
            System.out.println("user, host, password, port 값이 비어있다.");
            return null;
        }

        if(os.equals("LINUX")) {
            try {
                /*
                * List를 사용하여 각 서비스의 이름을 한 곳에 모았고,
                * for 루프를 사용하여 반복 작업을 수행하도록 변경했음.
                * 또한 map.put 문을 루프 안에 배치하여 중복을 제거하였음.
                * */

                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                String[] systemServices = {settingVo.getSystemService1(), settingVo.getSystemService2(), settingVo.getSystemService3()};
                String[] serviceResults = {"0", "0", "0"};
                String[] serviceStatuses = {"active: active", "active: failed", "active: inactive"};

                for (int i = 0; i < systemServices.length; i++) {
                    if (systemServices[i] != null) {
                        String command = "sudo systemctl status " + systemServices[i];
                        String result = executeCommand(session, command);

                        for (int j = 0; j < serviceStatuses.length; j++) {
                            if (result.toLowerCase().contains(serviceStatuses[j])) {
                                serviceResults[i] = (j == 0) ? "2" : "1";
                                break;
                            }
                        }
                    }
                }

                for (int i = 0; i < serviceResults.length; i++) {
                    map.put("serviceResult" + (i+1) + "-" + systemSeq, serviceResults[i]);
                }

            } catch (JSchException e) {
                //e.printStackTrace();
                System.out.println("서버 접속 실패");
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        }else if (os.equals("WINDOW")){

            try {
                /*
                * 배열을 사용하여 코드 중복을 제거하고,
                * for 루프를 사용하여 코드를 단순화함.
                * serviceStatuses 배열을 사용하여 result 문자열에서 서비스 상태를 판별하고,
                * serviceResults 배열에 결과 값을 저장.
                * 이후에는 결과 값을 map에 저장.
                * */

                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                List<String> services = new ArrayList<String>(Arrays.asList(
                        settingVo.getSystemService1(),
                        settingVo.getSystemService2(),
                        settingVo.getSystemService3()
                ));

                for (int i = 0; i < services.size(); i++) {
                    String serviceName = services.get(i);
                    if (serviceName != null) {
                        String serviceResult = win_executeCommand(session, "sc query " + serviceName);

                        serviceResult = serviceResult.replaceAll("\\r\\n|\\r|\\n", "");

                        if (!serviceName.equals("")) {
                            if (serviceResult.toLowerCase().contains("running")) {
                                serviceResult = "2";
                            } else if (serviceResult.toLowerCase().contains("stopped") ||
                                    serviceResult.toLowerCase().contains("paused") ||
                                    serviceResult.toLowerCase().contains("unknown") ||
                                    serviceResult.toLowerCase().contains("start_pending") ||
                                    serviceResult.toLowerCase().contains("stop_pending") ||
                                    serviceResult.toLowerCase().contains("pause_pending") ||
                                    serviceResult.toLowerCase().contains("continue_pending")) {
                                serviceResult = "1";
                            } else {
                                serviceResult = "0";
                            }
                        }else {
                            serviceResult = "0";
                        }

                        map.put("serviceResult" + (i + 1) + "-" + systemSeq, serviceResult);
                    }
                }
            } catch (JSchException e) {
                //e.printStackTrace();
                System.out.println("서버 접속 실패");
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }

        }else {
            System.out.println("컨트롤러 잘못된정보");
        }
        return map;
    }

    public int serviceControl(String systemNum, String systemSeq, String systemControl) throws Exception{

        JSch jsch = new JSch();
        Session session = null;

        SettingVo settingVo = settingService.getSetting(systemSeq);

        String host = settingVo.getSystemHost();
        String user = settingVo.getSystemUser();
        String password = settingVo.getSystemPassword();
        int port = Integer.parseInt(settingVo.getSystemPort());
        String os = settingVo.getSystemOs();

        if (user == null || host == null || password == null || port == 0) {
            System.out.println("user, host, password, port 값이 비어있다.");
            return 0;
        }

        if(os.equals("LINUX")) {
            try {
                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                String serviceResult;

                if (systemNum.equals("1")) {
                    serviceResult = executeCommand(session, "sudo systemctl "  + systemControl + " "+ settingVo.getSystemService1());

                    if (serviceResult.contains("Failed to start " + settingVo.getSystemService1() + ".service")) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
                if (systemNum.equals("2")) {
                    serviceResult = executeCommand(session, "sudo systemctl "  + systemControl + " "+ settingVo.getSystemService2());

                    if (serviceResult.contains("Failed to start " + settingVo.getSystemService2() + ".service")) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
                if (systemNum.equals("3")) {
                    serviceResult = executeCommand(session, "sudo systemctl "  + systemControl + " "+ settingVo.getSystemService3());

                    if (serviceResult.contains("Failed to start " + settingVo.getSystemService3() + ".service")) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
            } catch (JSchException e) {
                e.printStackTrace();
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        }else if (os.equals("WINDOW")){
            try {
                session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                String serviceResult;

                if (systemNum.equals("1")) {
                    serviceResult = win_executeCommand(session, "sc "  + systemControl + " "+ settingVo.getSystemService1());

                    if (serviceResult.contains("1060 :")) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
                if (systemNum.equals("2")) {
                    serviceResult = win_executeCommand(session, "sc "  + systemControl + " "+ settingVo.getSystemService2());

                    if (serviceResult.contains("1060 :")) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
                if (systemNum.equals("3")) {
                    serviceResult = win_executeCommand(session, "sc "  + systemControl + " "+ settingVo.getSystemService3());

                    if (serviceResult.contains("1060 :")) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
            } catch (JSchException e) {
                e.printStackTrace();
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        }else {
            System.out.println("컨트롤러 잘못된정보");
        }

        return 0;
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
