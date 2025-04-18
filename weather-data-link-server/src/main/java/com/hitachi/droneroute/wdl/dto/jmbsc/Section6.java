package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import java.util.BitSet;

import lombok.Getter;
import lombok.ToString;

/** GRIB2の第6節. */
@Getter
@ToString
public class Section6 {

    /** ビットマップ指示符. */
    private BitSet bitmap;

    /**
     * コンストラクタ.
     * 
     * @param buffer        GRIB2メッセージデータを含むByteBuffer
     * @param bitmapOffset  ビットマップのオフセット(前節までの長さも含む)
     * @param sectionLength セクションの末尾までの長さ(前節までの長さも含む)
     */
    public Section6(ByteBuffer buffer, int bitmapOffset, int sectionLength) {
        ByteBuffer bitmapBuffer = Grib2.createBuffer(buffer, bitmapOffset,
                sectionLength - bitmapOffset);
        this.bitmap = BitSet.valueOf(bitmapBuffer);
    }
}
