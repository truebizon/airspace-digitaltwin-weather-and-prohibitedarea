package com.hitachi.droneroute.wdl.service.jmbsc.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.dto.jmbsc.CoordinatePointRepValue;
import com.hitachi.droneroute.wdl.dto.jmbsc.Grib2;
import com.hitachi.droneroute.wdl.dto.jmbsc.MultiSection;
import com.hitachi.droneroute.wdl.dto.jmbsc.Section4;
import com.hitachi.droneroute.wdl.dto.nec.JmbscWeatherRegisterRequest;
import com.hitachi.droneroute.wdl.dto.nec.JmbscWeatherRegisterRequestWeather;

/** Grib2からリクエストへの変換サービスの親クラス. */
public abstract class Grib2ConvertServiceImpl {

    /** ロガー. */
    // private static final Logger LOG =
    // LoggerFactory.getLogger(Grib2ConvertServiceImpl.class);

    /** 予報日時形式. */
    @Value("${rest.nec.forecast-date-time-format}")
    private String dateTimeFormat;

    /**
     * 実行.
     *
     * @param grib GRIB2
     * @param yml  GRIB2の設定を含むYAML
     * @return リクエスト
     */
    public JmbscWeatherRegisterRequest execute(Grib2 grib, Grib2Yml yml) {
        List<JmbscWeatherRegisterRequestWeather> weathers = new ArrayList<>();
        grib.getMultiSections().forEach(multiSection -> {
            multiSection.getTrimmedPointRepValuesList()
                    .getSeparatedRepValuesList()
                    .forEach(trimmedPointRepValues -> {
                        JmbscWeatherRegisterRequestWeather weather =
                                createWeather(grib, multiSection,
                                        trimmedPointRepValues);
                        weathers.add(weather);
                    });
        });
        JmbscWeatherRegisterRequest result =
                new JmbscWeatherRegisterRequest(weathers);
        return result;
    }

    /**
     * 気象情報を作成.
     *
     * @param grib         GRIB2
     * @param multiSection 複数節
     * @param repValues    座標点の代表値リスト
     * @return 気象情報
     */
    JmbscWeatherRegisterRequestWeather createWeather(Grib2 grib,
            MultiSection multiSection,
            List<CoordinatePointRepValue> repValues) {

        ZonedDateTime refDateTime = grib.getSection1().getRefDateTime();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(dateTimeFormat);
        String timeStart = refDateTime
                .plusMinutes(multiSection.getSection4().getForecastTime())
                .format(formatter);
        String timeEnd =
                createTimeEnd(timeStart, formatter, multiSection.getSection4());
        int scaleFactor = grib.getSection3().getCoordinateScaleFactor();

        BigDecimal lonStart = repValues.get(0).getLon();
        BigDecimal latStart = repValues.get(0).getLat();
        BigDecimal lonInterval =
                BigDecimal.valueOf(grib.getSection3().getLonInterval())
                        .scaleByPowerOfTen(scaleFactor);
        BigDecimal latInterval =
                BigDecimal.valueOf(grib.getSection3().getLatInterval())
                        .scaleByPowerOfTen(scaleFactor);

        int lonCount = createLonCount(repValues);
        int lanCount = repValues.size() / lonCount;
        List<BigDecimal> values = createValues(repValues);
        JmbscWeatherRegisterRequestWeather result =
                new JmbscWeatherRegisterRequestWeather(
                        getType(multiSection.getSection4()), timeStart, timeEnd,
                        lonStart, latStart, lonInterval, latInterval, lonCount,
                        lanCount, values);
        return result;
    }

    /**
     * 終了日時を作成.
     *
     * @param timeStart 開始日時
     * @param formatter 日時フォーマッタ
     * @param section4  第4節
     * @return 終了日時
     */
    String createTimeEnd(String timeStart, DateTimeFormatter formatter,
            Section4 section4) {
        return section4.getEndDateTime().format(formatter);
    }

    /**
     * 経度の格子点数を作成.
     *
     * @param repValues 座標点の代表値リスト
     * @return 経度の格子点数
     */
    int createLonCount(List<CoordinatePointRepValue> repValues) {
        // 初めての緯度が異なる座標点のインデックスを返す
        BigDecimal firstLat = repValues.get(0).getLat();
        for (int i = 0; i < repValues.size(); i++) {
            if (!firstLat.equals(repValues.get(i).getLat())) {
                return i;
            }
        }
        return repValues.size();
    }

    /**
     * 値リストを作成.
     *
     * @param repValues 座標点の代表値リスト
     * @return 値リスト
     */
    List<BigDecimal> createValues(List<CoordinatePointRepValue> repValues) {
        List<BigDecimal> values = new ArrayList<>();
        repValues.forEach(repValue -> {
            if (repValue.getValue() != null) {
                values.add(repValue.getValue());
            } else {
                values.add(null);
            }
        });
        return values;
    }

    /**
     * 種別を取得.
     *
     * @param section4 第4節
     * @return 種別
     */
    protected abstract String getType(Section4 section4);
}
