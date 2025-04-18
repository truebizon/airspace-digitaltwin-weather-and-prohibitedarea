package com.hitachi.droneroute.fpadl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 飛行禁止エリア情報検索APIのレスポンスパラメータの飛行禁止エリア情報.
 *
 * @author isao.kaneko
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightProhibitedAreaSearchResponseInfo {

    /** 飛行禁止エリアID.. */
    private String flightProhibitedAreaId;

    /** 飛行禁止エリア名. */
    private String name;

    /** 飛行禁止範囲. */
    private FlightProhibitedAreaSearchResposeRange range;

    /** 説明詳細. */
    private String detail;

    /** 説明URL. */
    private String url;

    /** 飛行禁止エリア種別. */
    private Integer flightProhibitedAreaTypeId;

    /**
     * 有効期限(FROM).<br/>
     * 形式：uuuu-MM-ddTHH:mm:ss
     */
    private String startTime;

    /**
     * 有効期限(TO).<br/>
     * 形式：uuuu-MM-ddTHH:mm:ss
     */
    private String finishTime;
}
