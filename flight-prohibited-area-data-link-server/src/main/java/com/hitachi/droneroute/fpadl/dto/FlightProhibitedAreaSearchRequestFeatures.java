package com.hitachi.droneroute.fpadl.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 飛行禁止エリア情報検索APIのリクエストパラメータの検索範囲.
 *
 * @author isao.kaneko
 */
@Getter
@Setter
@ToString
@Builder
public class FlightProhibitedAreaSearchRequestFeatures {

    /** ジオメトリ構成点. */
    private Double[][] coordinates;
}
