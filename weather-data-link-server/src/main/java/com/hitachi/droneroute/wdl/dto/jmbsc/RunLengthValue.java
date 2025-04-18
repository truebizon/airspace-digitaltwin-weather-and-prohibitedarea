package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.ToString;

/** GRIB2の第7節のランレングス圧縮の値. */
@Getter
@ToString
public class RunLengthValue {

    /** 圧縮値リスト. */
    private List<Integer> compressedValues = new ArrayList<>();
    /** 展開された値リスト. */
    private List<Integer> decompressedValues = new ArrayList<>();

    /**
     * コンストラクタ.
     *
     * @param allBuffer       全バイトデータ
     * @param octetSeqOffset  先頭からランレングス圧縮オクテット列までのオフセット
     * @param sectionLength   先頭からセクション末までの長さ
     * @param nbit            圧縮後の1格子点値当りのビット数
     * @param currentMaxLevel 今回の圧縮に用いたレベルの最大値
     */
    public RunLengthValue(ByteBuffer allBuffer, int octetSeqOffset,
            int sectionLength, int nbit, int currentMaxLevel) {

        ByteBuffer buffer = Grib2.createBuffer(allBuffer, octetSeqOffset,
                sectionLength - octetSeqOffset);

        while (buffer.hasRemaining()) {
            this.compressedValues.add(buffer.get() & 0xFF);
        }

        // ランレングス圧縮を展開
        this.decompressedValues =
                decompress(this.compressedValues, nbit, currentMaxLevel);
    }

    /**
     * ランレングス圧縮を展開.
     *
     * @param runLengthValues ランレングス圧縮値リスト
     * @param nbit            圧縮後の1格子点値当りのビット数
     * @param currentMaxLevel 今回の圧縮に用いたレベルの最大値
     * @return 展開された値リスト
     */
    List<Integer> decompress(@Nonnull List<Integer> runLengthValues, int nbit,
            int currentMaxLevel) {
        Integer prev = null;
        List<Integer> rle = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        int lngu = (int) Math.pow(2, nbit) - 1 - currentMaxLevel;

        for (Integer runLengthValue : runLengthValues) {
            if (runLengthValue <= currentMaxLevel) {
                if (prev != null) {
                    int repeatCount = rleRepeat(rle, lngu, currentMaxLevel);
                    for (int i = 0; i < repeatCount; i++) {
                        result.add(prev);
                    }
                    rle.clear();
                }
                prev = runLengthValue;
            } else {
                rle.add(runLengthValue);
            }
        }

        int repeatCount = rleRepeat(rle, lngu, currentMaxLevel);
        for (int i = 0; i < repeatCount; i++) {
            result.add(prev);
        }
        return result;
    }

    /**
     * ランレングス圧縮の繰り返し回数を計算するメソッド.
     *
     * @param rle             ランレングス圧縮のリスト
     * @param lngu            lnguの値
     * @param currentMaxLevel 今回の圧縮に用いたレベルの最大値
     * @return 繰り返し回数
     */
    int rleRepeat(List<Integer> rle, int lngu, int currentMaxLevel) {
        if (rle.isEmpty()) {
            return 1;
        }
        int sum = 0;
        for (int m = 0; m < rle.size(); m++) {
            int n = rle.get(m);
            sum = sum + (((int)(Math.pow(lngu, m))) * (n - (currentMaxLevel + 1)));
        }
        return sum + 1;
    }
}
