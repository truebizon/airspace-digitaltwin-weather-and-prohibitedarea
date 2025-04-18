package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.ToString;

/** GRIB2の第7節. */
@Getter
@ToString
public class Section7 {

    /** ランレングス圧縮の値. */
    private RunLengthValue runLengthValue;

    /** 単純圧縮の値. */
    private SimpleValue simpleValue;

    /**
     * ランレングス圧縮の値を持つコンストラクタ.
     *
     * @param allBuffer       全バイトデータ
     * @param octetSeqOffset  ランレングス圧縮オクテット列のオフセット
     * @param sectionLength   セクションの長さ
     * @param nbit            圧縮後の1格子点値当りのビット数
     * @param currentMaxLevel 今回の圧縮に用いたレベルの最大値
     */
    public Section7(ByteBuffer allBuffer, int octetSeqOffset, int sectionLength,
            int nbit, int currentMaxLevel) {

        this.runLengthValue = new RunLengthValue(allBuffer, octetSeqOffset,
                sectionLength, nbit, currentMaxLevel);
    }

    /**
     * 単純圧縮の値を持つコンストラクタ.
     *
     * @param allBuffer         全バイトデータ
     * @param simpleValueOffset 単純圧縮オクテット列のオフセット
     * @param sectionLength     セクションの長さ
     * @param config            単純圧縮の設定
     */
    public Section7(ByteBuffer allBuffer, int simpleValueOffset,
            int sectionLength, SimpleConfig config) {

        this.simpleValue = new SimpleValue(allBuffer, simpleValueOffset,
                sectionLength, config);
    }
}
