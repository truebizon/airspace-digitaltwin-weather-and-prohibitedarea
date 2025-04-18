package com.hitachi.droneroute.wdl.dto.nec;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * NECコンソへの気象業務支援センターの気象情報登録APIリクエストパラメータの気象.
 * 
 * @author isao.kaneko
 */
@Getter
@ToString
public class JmbscWeatherRegisterRequestWeather {

    /** 種別. */
    private String type;

    /** 予報開始日時. */
    private String timeStart;

    /** 予報終了日時. */
    private String timeEnd;

    /** 最初の格子点の経度. */
    private BigDecimal lonStart;

    /** 最初の格子点の緯度. */
    private BigDecimal latStart;

    /** 経度の格子点間隔. */
    private BigDecimal lonInterval;

    /** 緯度の格子点間隔. */
    private BigDecimal latInterval;

    /** 経度の格子点数. */
    private Integer lonCount;

    /** 緯度の格子点数. */
    private Integer latCount;

    /** 値. */
    private List<BigDecimal> values;

    /**
     * コンストラクタ.
     * 
     * @param type        種別
     * @param timeStart   予報開始日時
     * @param timeEnd     予報終了日時
     * @param lonStart    最初の格子点の経度
     * @param latStart    最初の格子点の緯度
     * @param lonInterval 経度の格子点間隔
     * @param latInterval 緯度の格子点間隔
     * @param lonCount    経度の格子点数
     * @param latCount    緯度の格子点数
     * @param values      値
     */
    public JmbscWeatherRegisterRequestWeather(String type, String timeStart,
            String timeEnd, BigDecimal lonStart, BigDecimal latStart,
            BigDecimal lonInterval, BigDecimal latInterval, Integer lonCount,
            Integer latCount, List<BigDecimal> values) {
        super();
        this.type = type;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.lonStart = lonStart;
        this.latStart = latStart;
        this.lonInterval = lonInterval;
        this.latInterval = latInterval;
        this.lonCount = lonCount;
        this.latCount = latCount;
        this.values = values;
    }
}
