
package com.hitachi.droneroute.wdl.service.nec.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitachi.droneroute.wdl.dto.nec.JmbscWeatherRegisterRequest;
import com.hitachi.droneroute.wdl.service.nec.JmbscWeatherRegisterService;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/**
 * NECコンソへの気象業務支援センターの気象情報登録サービス.
 *
 * @author isao.kaneko
 */
@Service
@RequiredArgsConstructor
public class JmbscWeatherRegisterServiceImpl
        implements JmbscWeatherRegisterService {

    /** ロガー. */
    private static final Logger LOG =
            LoggerFactory.getLogger(JmbscWeatherRegisterServiceImpl.class);

    /** 降水量実況. */
    @Value("${rest.nec.type.apcp-live}")
    private String apcpLive;

    /** 降水量予報. */
    @Value("${rest.nec.type.apcp-forecast}")
    private String apcpForecast;

    /** 東西風速. */
    @Value("${rest.nec.type.ugrd}")
    private String ugrd;

    /** 南北風速. */
    @Value("${rest.nec.type.vgrd}")
    private String vgrd;

    /** 最大リトライ回数. */
    @Value("${rest.max-retry-count}")
    private int maxRetryCount;

    /** エンドポイント. */
    @Value("${rest.nec.endpoint}")
    private String endpoint;

    /** HTTPクライアント. */
    private final RestTemplate httpClient;

    /** {@inheritDoc} */
    @Override
    public void execute(JmbscWeatherRegisterRequest request)
            throws JsonProcessingException {
        String weatherTypeName = "";
        String firstWeatherType = request.getWeather().get(0).getType();

        if (firstWeatherType.equals(apcpLive)) {
            weatherTypeName = "降水量実況";
        } else if (firstWeatherType.equals(apcpForecast)) {
            weatherTypeName = "降水量予報";
        } else if (firstWeatherType.equals(ugrd)
                || firstWeatherType.equals(vgrd)) {
            weatherTypeName = "風速";
        } else {
            throw new IllegalArgumentException(
                    "想定外の気象情報種別:" + firstWeatherType);
        }

        LOG.info(weatherTypeName + "登録 開始：request={}", request);
        try {
            // 気象情報登録API呼び出し
            callApi(weatherTypeName, request);

            LOG.info(weatherTypeName + "登録 終了");
        } catch (Exception e) {
            LOG.error(weatherTypeName + "登録 異常終了", e);
        }
    }

    /**
     * API呼び出し.
     *
     * @param weatherTypeName 気象情報種別名
     * @param request         NECコンソへの気象業務支援センターの気象情報登録APIリクエストパラメータ
     * @throws JsonProcessingException json処理例外
     * @throws RestClientException     RESTクライアント例外
     */
    void callApi(String weatherTypeName, JmbscWeatherRegisterRequest request)
            throws JsonProcessingException, RestClientException {
        LOG.info(weatherTypeName + "登録API呼び出し 開始");
        int retryCount = 0;
        RestClientException clientException = null;
        while (true) {
            try {
                HttpEntity<JmbscWeatherRegisterRequest> requestEntity =
                        new HttpEntity<>(request);
                httpClient.exchange(endpoint, HttpMethod.POST, requestEntity,
                        Void.class);
                LOG.info(weatherTypeName + "登録API呼び出し 終了");
                return;
                // レスポンスコード:400～599
            } catch (RestClientResponseException e) {
                // レスポンスコード:400(リクエストパラメータエラー)
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    LOG.error(weatherTypeName + "登録API呼び出し 異常終了("
                            + "リクエスト失敗:値域の制約違反)", e);
                    throw e;
                } else {
                    // レスポンスコード:401～599
                    LOG.error(
                            MessageFormat.format(
                                    "{0}登録API呼び出し リクエスト失敗(エラーレスポンスコード400以外)："
                                            + "リトライ回数/最大リトライ回数={1}/{2}",
                                    weatherTypeName, retryCount, maxRetryCount),
                            e);
                    clientException = e;
                }
                // タイムアウト
            } catch (ResourceAccessException e) {
                LOG.error(MessageFormat.format(
                        "{0}登録API呼び出し リクエスト失敗(接続失敗やタイムアウト)："
                                + "リトライ回数/最大リトライ回数={1}/{2}",
                        weatherTypeName, retryCount, maxRetryCount), e);
                clientException = e;
            } catch (RestClientException e) {
                LOG.error(weatherTypeName + "登録API呼び出し 異常終了(想定外)", e);
                throw e;
            }
            retryCount++;
            if (retryCount > maxRetryCount) {
                LOG.error(
                        MessageFormat.format(
                                "{0}登録API呼び出し 異常終了(リクエストの最大リトライ回数({1})回失敗)",
                                weatherTypeName, maxRetryCount),
                        clientException);
                throw clientException;
            }
        }
    }
}
