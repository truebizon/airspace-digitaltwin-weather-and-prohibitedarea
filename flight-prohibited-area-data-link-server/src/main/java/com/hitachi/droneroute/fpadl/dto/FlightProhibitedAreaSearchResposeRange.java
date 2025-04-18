package com.hitachi.droneroute.fpadl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 飛行禁止エリア情報検索APIのレスポンスパラメータの範囲.
 *
 * @author isao.kaneko
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightProhibitedAreaSearchResposeRange {

    /** ジオメトリタイプ. */
    private String type;

    /** ジオメトリ(中心点). */
    private Double[] center;

    /** 半径. */
    private Integer radius;

    /** ジオメトリ(構成点). */
    private Double[][] coordinates;
}
