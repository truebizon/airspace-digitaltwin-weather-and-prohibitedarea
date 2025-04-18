package com.hitachi.droneroute.wdl.service.nec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitachi.droneroute.wdl.dto.nec.JmbscWeatherRegisterRequest;

/**
 * NECコンソへの気象業務支援センターの気象情報登録サービスインターフェース.
 *
 * @author kaneko
 */
public interface JmbscWeatherRegisterService {

    /**
     * 実行.
     *
     * @param request NECコンソへの気象業務支援センターの気象情報登録APIリクエストパラメータ
     * @throws JsonProcessingException json処理例外
     */
    void execute(JmbscWeatherRegisterRequest request)
            throws JsonProcessingException;

}
