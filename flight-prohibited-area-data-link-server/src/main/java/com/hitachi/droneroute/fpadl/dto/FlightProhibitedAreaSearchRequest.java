package com.hitachi.droneroute.fpadl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 飛行禁止エリア情報検索APIのリクエストパラメータ.
 *
 * @author isao.kaneko
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightProhibitedAreaSearchRequest {

    /** 検索範囲. */
    private FlightProhibitedAreaSearchRequestFeatures features;

    /** 検索開始時刻. */
    private String startTime;

    /** 検索終了時刻. */
    private String finishTime;
}
