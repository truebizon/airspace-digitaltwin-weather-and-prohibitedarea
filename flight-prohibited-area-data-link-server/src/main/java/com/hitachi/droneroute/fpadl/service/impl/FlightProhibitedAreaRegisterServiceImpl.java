package com.hitachi.droneroute.fpadl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitachi.droneroute.fpadl.dto.FlightProhibitedAreaSearchResponse;
import com.hitachi.droneroute.fpadl.service.FlightProhibitedAreaRegisterService;
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
 * 飛行禁止エリア情報登録サービス.
 *
 * @author isao.kaneko
 */
@Service
@RequiredArgsConstructor
public class FlightProhibitedAreaRegisterServiceImpl
        implements FlightProhibitedAreaRegisterService {

    /** ロガー. */
    private static final Logger LOG = LoggerFactory
            .getLogger(FlightProhibitedAreaRegisterServiceImpl.class);

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
    public void execute(FlightProhibitedAreaSearchResponse request)
            throws JsonProcessingException {
        LOG.info("飛行禁止エリア情報登録 開始");
        try {
            // 飛行禁止エリア情報登録API呼び出し
            callApi(request);

            LOG.info("飛行禁止エリア情報登録 終了");
        } catch (Exception e) {
            LOG.error("飛行禁止エリア情報登録 異常終了", e);
        }
    }

    /**
     * API呼び出し.
     *
     * @param request 飛行禁止エリア情報検索APIのレスポンスパラメータ
     * @throws JsonProcessingException json処理例外
     * @throws RestClientException     RESTクライアント例外
     */
    void callApi(FlightProhibitedAreaSearchResponse request)
            throws JsonProcessingException, RestClientException {
        LOG.info("飛行禁止エリア情報登録API呼び出し 開始：request={}", request);
        int retryCount = 0;
        RestClientException clientException = null;
        while (true) {
            try {
                HttpEntity<FlightProhibitedAreaSearchResponse> requestEntity =
                        new HttpEntity<>(request);
                httpClient.exchange(endpoint, HttpMethod.POST, requestEntity,
                        Void.class);
                LOG.info("飛行禁止エリア情報登録API呼び出し 終了");
                return;
                // レスポンスコード:400～599
            } catch (RestClientResponseException e) {
                // レスポンスコード:400(リクエストパラメータエラー)
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    LOG.error(MessageFormat.format("飛行禁止エリア情報登録API呼び出し "
                            + "異常終了(リクエスト失敗:値域の制約違反)：" + "request={0}",
                            request), e);
                    throw e;
                } else {
                    // レスポンスコード:401～599
                    LOG.error(MessageFormat.format(
                            "飛行禁止エリア情報登録API呼び出し リクエスト失敗(エラーレスポンスコード400以外)："
                                    + "リトライ回数/最大リトライ回数={0}/{1}, request={2}",
                            retryCount, maxRetryCount, request), e);
                    clientException = e;
                }
                // 接続失敗やタイムアウト
            } catch (ResourceAccessException e) {
                LOG.error(MessageFormat.format(
                        "飛行禁止エリア情報登録API呼び出し リクエスト失敗(接続失敗やタイムアウト)："
                                + "リトライ回数/最大リトライ回数={0}/{1}, request={2}",
                        retryCount, maxRetryCount, request), e);
                clientException = e;
            } catch (Exception e) {
                LOG.error(MessageFormat.format(
                        "飛行禁止エリア情報登録API呼び出し 異常終了(想定外)：" + "request={0}",
                        request), e);
                throw e;
            }
            retryCount++;
            if (retryCount > maxRetryCount) {
                LOG.error(MessageFormat.format(
                        "飛行禁止エリア情報登録API呼び出し 異常終了(リクエストの最大リトライ回数({0})回失敗)",
                        maxRetryCount), clientException);
                throw clientException;
            }
        }
    }
}
