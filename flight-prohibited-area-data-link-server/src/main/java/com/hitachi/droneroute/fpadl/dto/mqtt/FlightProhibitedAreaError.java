package com.hitachi.droneroute.fpadl.dto.mqtt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * エラー通知.
 *
 * @author soichi.kimura
 */
@AllArgsConstructor
@Getter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightProhibitedAreaError {
    /** 発生時刻. */
    private String occurrenceTime;

    /** エラーメッセージ. */
    private String errorMessage;
}
