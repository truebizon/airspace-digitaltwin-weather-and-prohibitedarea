package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.ToString;

/** GRIB2の第3節. */
@Getter
@ToString
public class Section3 {

    /** 格子点の緯度方向の数. */
    private int latCount;
    /** 格子点の経度方向の数. */
    private int lonCount;
    /** 最初の格子点の緯度. */
    private int firstLat;
    /** 最初の格子点の経度. */
    private int firstLon;
    /** 格子点の経度方向の間隔. */
    private int lonInterval;
    /** 格子点の緯度方向の間隔. */
    private int latInterval;
    /** 座標の尺度因子. */
    private int coordinateScaleFactor;

    /**
     * コンストラクタ.
     *
     * @param allBuffer             全バイトデータ
     * @param countOffset           格子点数のオフセット(前節までの長さも含む)
     * @param firstOffset           最初の格子点のオフセット(前節までの長さも含む)
     * @param intervalOffset        格子点間隔のオフセット(前節までの長さも含む)
     * @param length                長さ(上記3つは同じ長さ)
     * @param coordinateScaleFactor 座標の尺度因子
     */
    public Section3(ByteBuffer allBuffer, int countOffset, int firstOffset,
            int intervalOffset, int length, int coordinateScaleFactor) {

        ByteBuffer buffer = Grib2.createBuffer(allBuffer, countOffset, length);
        this.latCount = buffer.getInt() & 0xFFFFFFFF;
        this.lonCount = buffer.getInt() & 0xFFFFFFFF;

        buffer = Grib2.createBuffer(allBuffer, firstOffset, length);
        this.firstLat = buffer.getInt() & 0xFFFFFFFF;
        this.firstLon = buffer.getInt() & 0xFFFFFFFF;

        buffer = Grib2.createBuffer(allBuffer, intervalOffset, length);
        this.lonInterval = buffer.getInt() & 0xFFFFFFFF;
        // 緯度方向の間隔は負の値
        this.latInterval = (buffer.getInt() & 0xFFFFFFFF) * -1;

        this.coordinateScaleFactor = coordinateScaleFactor;
    }
}
