package com.hitachi.droneroute.wdl.service.jmbsc.impl;

import com.hitachi.droneroute.wdl.config.yml.ProvideContentLfmYml;
import com.hitachi.droneroute.wdl.dto.jmbsc.Section4;
import com.hitachi.droneroute.wdl.service.jmbsc.Grib2ConvertService;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 局地数値予報モデル変換サービス. */
@Service
@RequiredArgsConstructor
public class LfmConvertServiceImpl extends Grib2ConvertServiceImpl
        implements Grib2ConvertService {

    /** ロガー. */
    // private static final Logger LOG =
    // LoggerFactory.getLogger(SrfConvertServiceImpl.class);

    /** 種別：東西風速. */
    @Value("${rest.nec.type.ugrd}")
    private String ugrd;

    /** 種別：南北風速. */
    @Value("${rest.nec.type.vgrd}")
    private String vgrd;

    /** 局地数値予報モデルで提供する情報のYAML. */
    private final ProvideContentLfmYml yml;

    @Override
    protected String getType(Section4 section4) {
        if (section4.getParameterNumber() == yml.getParameterNumbers().get(0)) {
            return ugrd;
        } else if (section4.getParameterNumber() == yml.getParameterNumbers()
                .get(1)) {
            return vgrd;
        } else {
            throw new IllegalArgumentException(
                    "想定外の第4節のパラメータ番号:" + section4.getParameterNumber());
        }
    }

    @Override
    String createTimeEnd(String timeStart, DateTimeFormatter formatter,
            Section4 section4) {
        return timeStart;
    }
}
