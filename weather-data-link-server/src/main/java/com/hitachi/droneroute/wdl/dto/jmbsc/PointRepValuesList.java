package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/** 座標点の代表値リスト. */
@Getter
@ToString
public class PointRepValuesList {

    /** 座標点の代表値. */
    private List<CoordinatePointRepValue> repValues = new ArrayList<>();

    /**
     * コンストラクタ.
     * 
     * @param section3 第3節
     * @param section5 第5節
     * @param section6 第6節
     * @param section7 第7節
     */
    public PointRepValuesList(Section3 section3, Section5 section5,
            Section6 section6, Section7 section7) {
        int scaleFactor = section3.getCoordinateScaleFactor();
        for (int latIndex = 0; latIndex < section3.getLatCount(); latIndex++) {
            for (int lonIndex = 0; lonIndex < section3
                    .getLonCount(); lonIndex++) {
                BigDecimal lat = new BigDecimal(section3.getFirstLat()
                        + section3.getLatInterval() * latIndex)
                                .scaleByPowerOfTen(scaleFactor);
                BigDecimal lon = new BigDecimal(section3.getFirstLon()
                        + section3.getLonInterval() * lonIndex)
                                .scaleByPowerOfTen(scaleFactor);
                // 展開された(decompressed)値リストのインデックス
                int depIndex = latIndex * section3.getLatCount() + lonIndex;
                // 座標点の値
                BigDecimal value =
                        getValue(section5, section6, section7, depIndex);
                CoordinatePointRepValue pointValue =
                        new CoordinatePointRepValue(lat, lon, value);
                this.repValues.add(pointValue);
            }
        }
    }

    /**
     * 座標点の値を取得.
     * 
     * @param section5 第5節
     * @param section6 第6節
     * @param section7 第7節
     * @param depIndex 展開された(decompressed)値リストのインデックス
     * @return 座標点の値
     */
    BigDecimal getValue(Section5 section5, Section6 section6, Section7 section7,
            int depIndex) {

        if (section7.getSimpleValue() == null) {
            // ランレングス圧縮の場合
            return getRunLengthValue(section5, section7, depIndex);
        } else {
            // 単純圧縮の場合
            return getSimpleValue(section6, section7, depIndex);
        }
    }

    /**
     * ランレングス圧縮の展開後の座標点の値を取得.
     * 
     * @param section5 第5節
     * @param section7 第7節
     * @param depIndex 展開された(decompressed)値リストのインデックス
     * @return 座標点の値
     */
    BigDecimal getRunLengthValue(Section5 section5, Section7 section7,
            int depIndex) {
        // 展開された値
        Integer depValue = section7.getRunLengthValue().getDecompressedValues()
                .get(depIndex);

        // 展開された値が0(欠測値)なら値をnull、そうでなければ代表値
        BigDecimal value = null;
        if (depValue != 0) {
            // 代表(representative)値
            Integer repValue =
                    section5.getRunLengthConfig().getRepValues().get(depValue);
            value = new BigDecimal(repValue).scaleByPowerOfTen(
                    -1 * section5.getRunLengthConfig().getScaleFactor());
        }
        return value;
    }

    /**
     * 単純圧縮の展開後の座標点の値を取得.
     * 
     * @param section6 第6節
     * @param section7 第7節
     * @param depIndex 展開された(decompressed)値リストのインデックス
     * @return 座標点の値
     */
    BigDecimal getSimpleValue(Section6 section6, Section7 section7,
            int depIndex) {
        if (section6.getBitmap().get(depIndex)) {
            int trueCount =
                    section6.getBitmap().get(0, depIndex + 1).cardinality();
            int decValuesCount =
                    section7.getSimpleValue().getDecompressedValues().size();
            if (trueCount > decValuesCount) {
                // 欠測値
                return null;
            }
            // 代表(representative)値
            BigDecimal repValue = section7.getSimpleValue()
                    .getDecompressedValues().get(trueCount - 1);
            return repValue;
        } else {
            // 欠測値
            return null;
        }
    }
}
