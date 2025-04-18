package com.hitachi.droneroute.wdl.service.jmbsc.impl;

import com.hitachi.droneroute.wdl.dto.jmbsc.Section4;
import com.hitachi.droneroute.wdl.service.jmbsc.Grib2ConvertService;
import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 降水ナウキャスト変換サービス. */
@Service
@RequiredArgsConstructor
public class NowcConvertServiceImpl extends Grib2ConvertServiceImpl
        implements Grib2ConvertService {

    /** ロガー. */
    // private static final Logger LOG =
    // LoggerFactory.getLogger(NowcConvertServiceImpl.class);

    /** 種別：降水量実況. */
    @Value("${rest.nec.type.apcp-live}")
    private String type;

    @Override
    protected String getType(Section4 section4) {
        return type;
    }

}
