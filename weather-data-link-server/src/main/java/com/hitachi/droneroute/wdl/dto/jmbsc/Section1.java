package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.ToString;

/** GRIB2の第1節. */
@Getter
@ToString
public class Section1 {

    /** 年. */
    private int year;
    /** 月. */
    private int month;
    /** 日. */
    private int day;
    /** 時. */
    private int hour;
    /** 分. */
    private int minute;
    /** 秒. */
    private int second;

    /**
     * コンストラクタ.
     * 
     * @param allBuffer 全バイトデータ
     * @param offset    オフセット(前節までの長さも含む)
     * @param length    長さ
     */
    public Section1(ByteBuffer allBuffer, int offset, int length) {

        ByteBuffer buffer = Grib2.createBuffer(allBuffer, offset, length);

        this.year = buffer.getShort() & 0xFFFF;
        this.month = buffer.get() & 0xFF;
        this.day = buffer.get() & 0xFF;
        this.hour = buffer.get() & 0xFF;
        this.minute = buffer.get() & 0xFF;
        this.second = buffer.get() & 0xFF;
    }

    /**
     * 参照日時(UTC)の取得.
     * 
     * @return 参照日時(UTC)
     */
    public ZonedDateTime getRefDateTime() {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0,
                ZoneOffset.UTC);
    }
}
