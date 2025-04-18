package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.ToString;

/** GRIB2の第4節. */
@Getter
@ToString
public class Section4 {

    /** パラメータカテゴリー. */
    private int parameterCategory;
    /** パラメータ番号. */
    private int parameterNumber;
    /** 予報時間(分). */
    private int forecastTime;
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
     * @param allBuffer          全バイトデータ
     * @param parameterOffset    パラメータのオフセット
     * @param parameterLength    パラメータの長さ
     * @param forecastTimeOffset 予報時間のオフセット(前節までの長さも含む)
     * @param forecastTimeLength 予報時間の長さ
     * @param endDateTimeOffset  終了日時のオフセット(前節までの長さも含む)
     * @param endDateTimeLength  終了日時の長さ
     */
    public Section4(ByteBuffer allBuffer, int parameterOffset,
            int parameterLength, int forecastTimeOffset, int forecastTimeLength,
            int endDateTimeOffset, int endDateTimeLength) {

        ByteBuffer buffer =
                Grib2.createBuffer(allBuffer, parameterOffset, parameterLength);
        this.parameterCategory = buffer.get() & 0xFF;
        this.parameterNumber = buffer.get() & 0xFF;

        buffer = Grib2.createBuffer(allBuffer, forecastTimeOffset,
                forecastTimeLength);
        this.forecastTime = buffer.getInt() & 0xFFFFFFFF;

        buffer = Grib2.createBuffer(allBuffer, endDateTimeOffset,
                endDateTimeLength);
        this.year = buffer.getShort() & 0xFFFF;
        this.month = buffer.get() & 0xFF;
        this.day = buffer.get() & 0xFF;
        this.hour = buffer.get() & 0xFF;
        this.minute = buffer.get() & 0xFF;
        this.second = buffer.get() & 0xFF;
    }

    /**
     * 終了日時(UTC)の取得.
     *
     * @return 終了日時(UTC)
     */
    public ZonedDateTime getEndDateTime() {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0,
                ZoneOffset.UTC);
    }
}
