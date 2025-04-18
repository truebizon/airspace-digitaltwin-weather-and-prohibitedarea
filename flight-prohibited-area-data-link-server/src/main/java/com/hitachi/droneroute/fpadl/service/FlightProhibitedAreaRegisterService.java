package com.hitachi.droneroute.fpadl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitachi.droneroute.fpadl.dto.FlightProhibitedAreaSearchResponse;

/**
 * 飛行禁止エリア情報登録サービスインターフェース.
 *
 * @author kaneko
 */
public interface FlightProhibitedAreaRegisterService {

    /**
     * 実行.
     *
     * @param request 飛行禁止エリア情報検索APIのレスポンスパラメータ
     * @throws JsonProcessingException json処理例外
     */
    void execute(FlightProhibitedAreaSearchResponse request)
            throws JsonProcessingException;

}
