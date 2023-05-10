package com.shopping.mallAdmin.setting.mapper;

import com.shopping.mallAdmin.setting.vo.SettingVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SettingMapper {

    List<SettingVo> getSettingList();

    List<SettingVo> getServiceList();

    int insertSystem(SettingVo settingVo);

    int updateSystem(SettingVo settingVo);

    int deleteSystem(String settingSeq);

    List<SettingVo> getSeq();

    SettingVo getSetting(String settingSeq);

}
