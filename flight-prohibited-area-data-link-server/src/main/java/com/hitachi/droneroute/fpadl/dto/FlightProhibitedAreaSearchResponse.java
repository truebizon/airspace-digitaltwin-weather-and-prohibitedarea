package com.hitachi.droneroute.fpadl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 飛行禁止エリア情報検索APIのレスポンスパラメータ.
 *
 * @author isao.kaneko
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightProhibitedAreaSearchResponse {

    /** 飛行禁止エリア情報リスト. */
    private List<FlightProhibitedAreaSearchResponseInfo>
            flightProhibitedAreaInfo = new ArrayList<>();

    /** 総件数. */
    private int totalCount = 0;

//使用しない※HTTPレスポンスコードと同じ値が入るため
//    /** ステータス. */
//    private String status = null;

    /** エラーメッセージ. */
    private String errorMessage = null;
}
