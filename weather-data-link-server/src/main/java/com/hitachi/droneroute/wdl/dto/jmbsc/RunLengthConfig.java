package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/** GRIB2の第5節のランレングス圧縮の設定. */
@Getter
@ToString
public class RunLengthConfig {

    /** 1データのビット数. */
    private int bitCount;
    /** 今回の圧縮に用いたレベルの最大値. */
    private int currentMaxLevel;
    /** 代表値の尺度因子. */
    private int scaleFactor;
    /** 代表(representative)値リスト. */
    private List<Integer> repValues = new ArrayList<>();

    /**
     * コンストラクタ.
     *
     * @param allBuffer             全バイトデータ
     * @param bitCountOffset        1データのビット数のオフセット
     * @param bitCountLength        1データのビット数の長さ
     * @param currentMaxLevelLength 今回の圧縮に用いたレベルの最大値の長さ
     * @param repValueOffset        代表値のオフセット
     * @param sectionLength         セクションの長さ
     */
    public RunLengthConfig(ByteBuffer allBuffer, int bitCountOffset,
            int bitCountLength, int currentMaxLevelLength, int repValueOffset,
            int sectionLength) {

        ByteBuffer buffer =
                Grib2.createBuffer(allBuffer, bitCountOffset, bitCountLength);
        this.bitCount = buffer.get() & 0xFF;

        buffer = Grib2.createBuffer(allBuffer, bitCountOffset + bitCountLength,
                currentMaxLevelLength);
        this.currentMaxLevel = buffer.getShort() & 0xFFFF;

        buffer = Grib2.createBuffer(allBuffer, repValueOffset,
                sectionLength - repValueOffset);
        this.scaleFactor = buffer.get() & 0xFF;
        // 序数＝レベルで、レベル０は欠測値
        this.repValues.add(null);
        while (buffer.hasRemaining()) {
            this.repValues.add(buffer.getShort() & 0xFFFF);
        }
    }
}
