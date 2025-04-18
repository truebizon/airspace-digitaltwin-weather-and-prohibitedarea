package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.ToString;

/** GRIB2の第5節. */
@Getter
@ToString
public class Section5 {

    /** ランレングス圧縮の設定. */
    private RunLengthConfig runLengthConfig;

    /** 単純圧縮の設定. */
    private SimpleConfig simpleConfig;

    /**
     * ランレングス圧縮の設定を持つコンストラクタ.
     *
     * @param allBuffer             全バイトデータ
     * @param bitCountOffset        1データのビット数のオフセット
     * @param bitCountLength        1データのビット数の長さ
     * @param currentMaxLevelLength 今回の圧縮に用いたレベルの最大値の長さ
     * @param repValueOffset        代表値のオフセット
     * @param sectionLength         セクションの長さ
     */
    public Section5(ByteBuffer allBuffer, int bitCountOffset,
            int bitCountLength, int currentMaxLevelLength, int repValueOffset,
            int sectionLength) {
        this.runLengthConfig =
                new RunLengthConfig(allBuffer, bitCountOffset, bitCountLength,
                        currentMaxLevelLength, repValueOffset, sectionLength);
    }

    /**
     * 単純圧縮の設定を持つコンストラクタ.
     *
     * @param allBuffer        全バイトデータ
     * @param simpleCompOffset 単純圧縮のオフセット
     * @param simpleCompLength 単純圧縮の長さ
     */
    public Section5(ByteBuffer allBuffer, int simpleCompOffset,
            int simpleCompLength) {
        this.simpleConfig =
                new SimpleConfig(allBuffer, simpleCompOffset, simpleCompLength);
    }
}
