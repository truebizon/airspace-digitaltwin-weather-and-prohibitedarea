package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.ToString;

/** GRIB2の第7節の単純圧縮の値. */
@Getter
@ToString
public class SimpleValue {

    /** 圧縮値リスト. */
    private List<Short> compressedValues = new ArrayList<>();
    /** 展開された値リスト. */
    private List<BigDecimal> decompressedValues = new ArrayList<>();

    /**
     * コンストラクタ.
     *
     * @param allBuffer      全バイトデータ
     * @param octetSeqOffset 先頭から単純圧縮オクテット列までのオフセット
     * @param sectionLength  先頭からセクション末までの長さ
     * @param config         単純圧縮の設定
     */
    public SimpleValue(ByteBuffer allBuffer, int octetSeqOffset,
            int sectionLength, SimpleConfig config) {

        ByteBuffer buffer = Grib2.createBuffer(allBuffer, octetSeqOffset,
                sectionLength - octetSeqOffset);

        // 単純圧縮値リスト作成
        this.compressedValues =
                extractCompressedValue(buffer, config.getBitCount());

        // 単純圧縮を展開
        this.decompressedValues = decompress(this.compressedValues,
                config.getRefValue(), config.getBinaryScaleFactor(),
                config.getDecimalScaleFactor());
    }

    /**
     * 圧縮値を抽出.
     * 
     * @param buffer   単純圧縮オクテット列のByteBuffer
     * @param bitCount 圧縮値のビット数
     * @return 抽出後の圧縮値
     */
    List<Short> extractCompressedValue(@Nonnull ByteBuffer buffer,
            int bitCount) {
        List<Short> result = new ArrayList<>();

        //bufferから3バイトずつ取得
        byte[] bytes = new byte[3];
        for(int i = 0; i < buffer.limit() -2; i += 3) {
            buffer.get(i, bytes, 0, 3);
            // 3バイトをバイナリ文字列に変換
            StringBuilder binary = new StringBuilder();
            for (byte b : bytes) {
                int val = b;
                for (int j = 0; j < Byte.SIZE; j++) {
                    binary.append((val & 128) == 0 ? 0 : 1);
                    val <<= 1;
                }
            }
            String firstBitValues = binary.toString().substring(0,12);
            result.add(convertDecimal(firstBitValues));
            String secondBitValues = binary.toString().substring(12,24);
            result.add(convertDecimal(secondBitValues));
        }
        return result;
    }
    
    /**
     * 2進数を10進数に変換.
     * 
     * @param bitValues 2進数文字列
     * @return 10進数
     */
    Short convertDecimal(String bitValues) {
        // 1文字目が1の場合は負の値、それ以外は正の値
        int sign = 1;
        if (bitValues.charAt(0) == '1') {
            sign = -1;
            // 1文字目を0に変換
            bitValues = "0" + bitValues.substring(1);
        }
        // 2進数を10進数に変換
        int compressedValue = Integer.parseInt(bitValues, 2);
        return (short) (sign * compressedValue);
    }

    /**
     * 単純圧縮を展開.
     * 
     * @param simpleValues       単純圧縮値リスト
     * @param refValue           参照値
     * @param binaryScaleFactor  二進尺度因子
     * @param decimalScaleFactor 十進尺度因子
     * @return 展開された値リスト
     */
    List<BigDecimal> decompress(@Nonnull List<Short> simpleValues, float refValue,
            int binaryScaleFactor, int decimalScaleFactor) {
        List<BigDecimal> result = new ArrayList<>();
        for (short simpleValue : simpleValues) {
            BigDecimal value = (new BigDecimal(String.valueOf(refValue))
                    .add((new BigDecimal(simpleValue).multiply(new BigDecimal(
                            String.valueOf(Math.pow(2, binaryScaleFactor)))))))
                                    .scaleByPowerOfTen(-1 * decimalScaleFactor);
            result.add(value);
        }
        return result;
    }
}
