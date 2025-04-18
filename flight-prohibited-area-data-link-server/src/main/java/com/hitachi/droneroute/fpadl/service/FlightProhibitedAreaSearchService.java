package com.hitachi.droneroute.fpadl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitachi.droneroute.fpadl.dto.FlightProhibitedAreaSearchResponse;

/**
 * 飛行禁止エリア情報検索サービスインターフェース.
 *
 * @author kaneko
 */
public interface FlightProhibitedAreaSearchService {

    /**
     * 実行.
     *
     * @return 飛行禁止エリア情報検索APIのレスポンス
     * @throws JsonProcessingException json処理例外
     */
    FlightProhibitedAreaSearchResponse execute() throws JsonProcessingException;

}
