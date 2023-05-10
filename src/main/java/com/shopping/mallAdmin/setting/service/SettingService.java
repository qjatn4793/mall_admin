package com.shopping.mallAdmin.setting.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.shopping.mallAdmin.setting.mapper.SettingMapper;
import com.shopping.mallAdmin.setting.vo.SettingVo;

import com.shopping.mallAdmin.util.EncryptUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class SettingService {

    @Autowired
    private SettingMapper settingMapper;

    public List<SettingVo> getSettingList() {

        List<SettingVo> settingList = settingMapper.getSettingList();

        return settingList;
    }

    public List<SettingVo> getServiceList() {

        List<SettingVo> serviceList = settingMapper.getServiceList();

        return serviceList;
    }

    public int insertSystem(SettingVo settingVo) throws Exception{

        JSch jsch = new JSch();
        Session session = null;
        EncryptUtil encryptUtil = new EncryptUtil();

        String host = settingVo.getSystemHost();
        String user = settingVo.getSystemUser();
        String password = settingVo.getSystemPassword();
        int port = Integer.parseInt(settingVo.getSystemPort());

        if (user == null || host == null || password == null || port == 0) {
            System.out.println("user, host, password, port 값이 비어있다.");

            return 0;
        }

        try {
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            settingVo.setSystemPassword(encryptUtil.encrypt(settingVo.getSystemPassword()));
            return settingMapper.insertSystem(settingVo);

        }catch (JSchException e) {
            //e.printStackTrace();
            return 0;
        }finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public int updateSystem(SettingVo settingVo) throws Exception{

        JSch jsch = new JSch();
        Session session = null;
        EncryptUtil encryptUtil = new EncryptUtil();

        String host = settingVo.getSystemHost();
        String user = settingVo.getSystemUser();
        String password = settingVo.getSystemPassword();
        int port = Integer.parseInt(settingVo.getSystemPort());

        if (user == null || host == null || password == null || port == 0) {
            System.out.println("user, host, password, port 값이 비어있다.");

            return 0;
        }

        try {
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            settingVo.setSystemPassword(encryptUtil.encrypt(settingVo.getSystemPassword()));
            return settingMapper.updateSystem(settingVo);

        }catch (JSchException e) {
            //e.printStackTrace();
            return 0;
        }finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public int deleteSystem(String settingSeq) throws Exception{

        return settingMapper.deleteSystem(settingSeq);
    }

    public Map<Integer, Integer> getSeq() {
        List<SettingVo> settingList = settingMapper.getSeq();
        Map<Integer, Integer> seqMap = new HashMap<>();
        for (int i = 0; i < settingList.size(); i++) {
            seqMap.put(i, Integer.parseInt(settingList.get(i).getSystemSeq()));
        }
        return seqMap;
    }

    public SettingVo getSetting(String settingSeq) throws Exception{

        EncryptUtil encryptUtil = new EncryptUtil();

        SettingVo settingVo = settingMapper.getSetting(settingSeq);

        if (settingVo.getSystemPassword() != null) {
            settingVo.setSystemPassword(encryptUtil.decrypt(settingVo.getSystemPassword()));
        }

        return settingVo;
    }

}
