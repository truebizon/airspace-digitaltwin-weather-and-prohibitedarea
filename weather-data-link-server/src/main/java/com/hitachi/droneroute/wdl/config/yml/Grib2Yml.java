package com.hitachi.droneroute.wdl.config.yml;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** GRIB2の設定を含むYAML. */
@Component
@Getter
public class Grib2Yml {

    /** ファイル名日時のフォーマット. */
    @Value("${grib2.file-name.date-time.format}")
    private String fileNameDateTimeFormat;

    /** ファイル名日時のオフセット. */
    @Value("${grib2.file-name.date-time.offset}")
    private int fileNameDateTimeOffset;

    /** ファイル名日時の長さ. */
    @Value("${grib2.file-name.date-time.length}")
    private int fileNameDateTimeLength;

    /** 第0節のデータサイズ. */
    @Value("${grib2.section0.data-size}")
    private int section0DataSize;

    /** 全体のデータサイズのオフセット. */
    @Value("${grib2.section0.all-data-size.offset}")
    private int allDataSizeOffset;

    /** 全体のデータサイズの長さ. */
    @Value("${grib2.section0.all-data-size.length}")
    private int allDataSizeLength;

    /** 共通(第0,8節除く)のデータサイズのオフセット. */
    @Value("${grib2.common.data-size.offset}")
    private int dataSizeOffset;

    /** 共通(第0,8節除く)のデータサイズの長さ. */
    @Value("${grib2.common.data-size.length}")
    private int dataSizeLength;

    /** 共通(第0,8節除く)の節番号のオフセット. */
    @Value("${grib2.common.section-number.offset}")
    private int sectionNumberOffset;

    /** 共通(第0,8節除く)の節番号の長さ. */
    @Value("${grib2.common.section-number.length}")
    private int sectionNumberLength;

    /** 第1節の参照日時のオフセット. */
    @Value("${grib2.section1.ref-date-time.offset}")
    private int section1Offset;

    /** 第1節の参照日時の長さ. */
    @Value("${grib2.section1.ref-date-time.length}")
    private int section1Length;

    /** 第3節の格子点数のオフセット. */
    @Value("${grib2.section3.grid-point.count.offset}")
    private int section3CountOffset;

    /** 第3節の最初の格子点のオフセット. */
    @Value("${grib2.section3.grid-point.first.offset}")
    private int section3FirstOffset;

    /** 第3節の格子点間隔のオフセット. */
    @Value("${grib2.section3.grid-point.interval.offset}")
    private int section3IntervalOffset;

    /** 第3節の格子点数の長さ. */
    @Value("${grib2.section3.grid-point.common.length}")
    private int section3Length;

    /** 第3節の座標の尺度因子. */
    @Value("${grib2.section3.grid-point.common.coordinate-scale-factor}")
    private int section3CoordinateScaleFactor;

    /** 第4節のパラメータのオフセット. */
    @Value("${grib2.section4.parameter.offset}")
    private int section4ParameterOffset;

    /** 第4節のパラメータの長さ. */
    @Value("${grib2.section4.parameter.length}")
    private int section4ParameterLength;

    /** 第4節の予報時間のオフセット. */
    @Value("${grib2.section4.forecast-time.offset}")
    private int section4ForecastTimeOffset;

    /** 第4節の予報時間の長さ. */
    @Value("${grib2.section4.forecast-time.length}")
    private int section4ForecastTimeLength;

    /** 第4節の終了日時のオフセット. */
    @Value("${grib2.section4.end-date-time.offset}")
    private int section4EndDateTimeOffset;

    /** 第4節の終了日時の長さ. */
    @Value("${grib2.section4.end-date-time.length}")
    private int section4EndDateTimeLength;

    /** 第5節の降水ナウキャスト(5分)、降水短時間予報の1データのビット数のオフセット. */
    @Value("${grib2.section5.nowc-srf.bit-count.offset}")
    private int section5BitCountOffset;

    /** 第5節の降水ナウキャスト(5分)、降水短時間予報の1データのビット数の長さ. */
    @Value("${grib2.section5.nowc-srf.bit-count.length}")
    private int section5BitCountLength;

    /** 第5節の降水ナウキャスト(5分)、降水短時間予報の今回の圧縮に用いたレベルの最大値のオフセット. */
    @Value("${grib2.section5.nowc-srf.current-max-level.offset}")
    private int section5CurrentMaxLevelOffset;

    /** 第5節の降水ナウキャスト(5分)、降水短時間予報の今回の圧縮に用いたレベルの最大値の長さ. */
    @Value("${grib2.section5.nowc-srf.current-max-level.length}")
    private int section5CurrentMaxLevelLength;

    /** 第5節の降水ナウキャスト(5分)、降水短時間予報の代表値のオフセット. */
    @Value("${grib2.section5.nowc-srf.rep-value.offset}")
    private int section5RepValueOffset;

    /** 第5節の局地数値予報モデルの単純圧縮のオフセット. */
    @Value("${grib2.section5.lfm.simple-compression.offset}")
    private int section5SimpleCompOffset;

    /** 第5節の局地数値予報モデルの単純圧縮の長さ. */
    @Value("${grib2.section5.lfm.simple-compression.length}")
    private int section5SimpleCompLength;

    /** 第6節のビットマップ指示符のオフセット. */
    @Value("${grib2.section6.bitmap.offset}")
    private int section6BitmapOffset;

    /** 第7節のオクテット列のオフセット. */
    @Value("${grib2.section7.octet-sequence.offset}")
    private int section7OctetSeqOffset;

    /** 第8節のデータサイズ. */
    @Value("${grib2.section8.data-size}")
    private int section8DataSize;
}
