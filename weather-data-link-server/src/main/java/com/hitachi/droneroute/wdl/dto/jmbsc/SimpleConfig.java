package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.ToString;

/** GRIB2の第5節の単純圧縮の設定. */
@Getter
@ToString
public class SimpleConfig {

    /** 参照値(R). */
    private float refValue;
    /** 二進尺度因子(E). */
    private int binaryScaleFactor;
    /** 十進尺度因子(D). */
    private int decimalScaleFactor;
    /** 圧縮値のビット数. */
    private int bitCount;

    /**
     * コンストラクタ.
     *
     * @param allBuffer        全バイトデータ
     * @param simpleCompOffset 単純圧縮のオフセット
     * @param simpleCompLength 単純圧縮の長さ
     */
    public SimpleConfig(ByteBuffer allBuffer, int simpleCompOffset,
            int simpleCompLength) {

        ByteBuffer buffer = Grib2.createBuffer(allBuffer, simpleCompOffset,
                simpleCompLength);
        // IEEE 754 binary32で取得
        this.refValue = buffer.getFloat();
        this.binaryScaleFactor = getMinusValueIfMostSignificantBitToOne(buffer);
        this.decimalScaleFactor =
                getMinusValueIfMostSignificantBitToOne(buffer);
        // 仕様上、12の固定値になる
        this.bitCount = buffer.get() & 0xFF;
    }

    /**
     * 最上位BITが1なら負の値を取得する.
     *
     * @param buffer 単純圧縮のByteBuffer
     * @return 最上位BITが1なら負の値、その他は正の値
     */
    short getMinusValueIfMostSignificantBitToOne(ByteBuffer buffer) {
        short result = 0;
        byte[] bytes = new byte[Short.BYTES];
        buffer.get(buffer.position(), bytes, 0, Short.BYTES);
        buffer.position(buffer.position() + Short.BYTES);
        // バイナリ文字列に変換
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < Byte.SIZE; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        // 最上位ビットが1なら負の値
        if (binary.charAt(0) == '1') {
            // 負数
            binary.replace(0, 1, "0");
            result = (short) (-1 * Integer.parseInt(binary.toString(), 2));
        } else {
            // 正数
            result = (short) Integer.parseInt(binary.toString(), 2);
        }
        return result;
    }

}
