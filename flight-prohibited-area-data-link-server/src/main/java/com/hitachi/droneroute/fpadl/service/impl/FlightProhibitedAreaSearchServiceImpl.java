package com.hitachi.droneroute.fpadl.service.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.fpadl.config.yml.RestDipsAddMinutesToNowYml;
import com.hitachi.droneroute.fpadl.config.yml.RestDipsYml;
import com.hitachi.droneroute.fpadl.dto.FlightProhibitedAreaSearchRequest;
import com.hitachi.droneroute.fpadl.dto.FlightProhibitedAreaSearchRequestFeatures;
import com.hitachi.droneroute.fpadl.dto.FlightProhibitedAreaSearchResponse;
import com.hitachi.droneroute.fpadl.dto.mqtt.FlightProhibitedAreaError;
// エラーをMQTTのpublishで通知する場合に下記コメントアウトを外す
//import com.hitachi.droneroute.fpadl.mqtt.MqttBrokerPublisher;
import com.hitachi.droneroute.fpadl.service.FlightProhibitedAreaSearchService;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/**
 * 飛行禁止エリア情報検索サービス.
 *
 * @author isao.kaneko
 */
@Service
@RequiredArgsConstructor
public class FlightProhibitedAreaSearchServiceImpl
        implements FlightProhibitedAreaSearchService {

    /** ロガー. */
    private static final Logger LOG = LoggerFactory
            .getLogger(FlightProhibitedAreaSearchServiceImpl.class);

    /** 最大リトライ回数. */
    @Value("${rest.max-retry-count}")
    private int maxRetryCount;

    /** エンドポイント. */
    @Value("${rest.dips.endpoint}")
    private String endpoint;

    /** 検索期間の日付形式. */
    @Value("${rest.dips.search-period.date-formmat}")
    private String dateFormmat;

    /** 検索期間のタイムゾーン表示名. */
    @Value("${rest.dips.search-period.time-zone}")
    private String timeZoneDisplayName;

    /** application.ymlのrest.dips. */
    private final RestDipsYml restDipsYml;

    /** application.ymlのrest.dips.add-minutes-to-now. */
    private final RestDipsAddMinutesToNowYml addMinutesToNowYml;

    /** jacksonのObjectMapper. */
    private final ObjectMapper mapper;

    /** HTTPクライアント. */
    private final RestTemplate httpClient;

    // エラーをMQTTのpublishで通知する場合に下記コメントアウトを外す
    // /** MQTTブローカへのPublisher. */
    // private final MqttBrokerPublisher mqttBrokerPublisher;

    /** {@inheritDoc} */
    @Override
    public FlightProhibitedAreaSearchResponse execute()
            throws JsonProcessingException {
        LOG.info("飛行禁止エリア情報検索 開始");

        FlightProhibitedAreaSearchResponse result =
                new FlightProhibitedAreaSearchResponse();

        for (String coordinates : restDipsYml.getCoordinatesList()) {
            // リクエスト作成
            FlightProhibitedAreaSearchRequest request =
                    createRequest(coordinates);
            // 飛行禁止エリア情報検索API呼び出し
            FlightProhibitedAreaSearchResponse response = callApi(request);

            if (response.getErrorMessage() != null) {
                result = new FlightProhibitedAreaSearchResponse();
                result.setErrorMessage(response.getErrorMessage());
                break;
            }
            result.getFlightProhibitedAreaInfo()
                    .addAll(response.getFlightProhibitedAreaInfo());
            result.setTotalCount(
                    result.getTotalCount() + response.getTotalCount());
        }
        if(StringUtils.hasLength(result.getErrorMessage())){
            LOG.error("飛行禁止エリア情報検索 異常終了");
        }else {
            LOG.info("飛行禁止エリア情報検索 終了");
        }

        return result;
    }

    /**
     * リクエストパラメータ作成.
     *
     * @param coordinates 座標
     * @return リクエストパラメータ
     * @throws JsonProcessingException json処理例外
     */
    FlightProhibitedAreaSearchRequest createRequest(String coordinates)
            throws JsonProcessingException {

        ZoneId zoneId = TimeZone.getTimeZone(timeZoneDisplayName).toZoneId();
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        FlightProhibitedAreaSearchRequest result =
                FlightProhibitedAreaSearchRequest.builder()
                        // 検索範囲
                        .features(FlightProhibitedAreaSearchRequestFeatures
                                .builder()
                                .coordinates(mapper.readValue(coordinates,
                                        Double[][].class)).build())
                        // 検索期間(FROM)
                        .startTime(createTime(now,
                                addMinutesToNowYml.getSearchPeriodFrom()))
                        // 検索期間(TO)
                        .finishTime(createTime(now,
                                addMinutesToNowYml.getSearchPeriodTo()))
                        .build();
        return result;
    }

    /**
     * 検索期間(FROMまたはTO)を算出.
     *
     * @param now        現在日時
     * @param addMinutes 検索期間(FROMまたはTO)を算出するために現在時刻に加算する分
     * @return 検索期間(FROMまたはTO)※addMinutes=nullならnull
     */
    String createTime(ZonedDateTime now, Integer addMinutes) {
        String result = null;
        if (addMinutes != null) {
            ZonedDateTime addedTime = now.plusMinutes(addMinutes);
            result = addedTime
                    .format(DateTimeFormatter.ofPattern(dateFormmat + "HHmm"));
        }
        return result;
    }

    /**
     * API呼び出し.
     *
     * @param request 飛行禁止エリア情報検索APIのリクエストパラメータ
     * @return 飛行禁止エリア情報検索APIのレスポンスパラメータ
     * @throws JsonProcessingException json処理例外
     * @throws RestClientException     RESTクライアント例外
     */
    FlightProhibitedAreaSearchResponse callApi(
            FlightProhibitedAreaSearchRequest request)
            throws JsonProcessingException, RestClientException {
        LOG.info("飛行禁止エリア情報検索API呼び出し 開始：request={}", request);
        int retryCount = 0;
        RestClientException clientException = null;
        while (true) {
            FlightProhibitedAreaSearchResponse result = null;
            try {
                HttpEntity<FlightProhibitedAreaSearchRequest> requestEntity =
                        new HttpEntity<>(request);
                ResponseEntity<FlightProhibitedAreaSearchResponse>
                        responseEntity = httpClient.exchange(
                                endpoint, HttpMethod.POST, requestEntity,
                                FlightProhibitedAreaSearchResponse.class);
                result = responseEntity.getBody();
                LOG.info("飛行禁止エリア情報検索API呼び出し 終了：response={}",
                        responseEntity.getBody());
                return result;
                // レスポンスコード:400～599
            } catch (RestClientResponseException e) {
                // レスポンスコード:400(リクエストパラメータエラー)
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    result = mapper.readValue(e.getResponseBodyAsString(),
                            FlightProhibitedAreaSearchResponse.class);
                    LOG.error(MessageFormat.format(
                            "飛行禁止エリア情報検索API呼び出し 異常終了(リクエスト失敗:値域の制約違反)："
                                    + "request={0}, response={1}",
                            request, result), e);
                    return result;
                }
                // レスポンスコード：401 または 403(認証失敗)
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                    result = mapper.readValue(e.getResponseBodyAsString(),
                            FlightProhibitedAreaSearchResponse.class);
                    LOG.warn(MessageFormat.format(
                            "飛行禁止エリア情報検索API呼び出し 異常終了(リクエスト失敗:認証失敗)："
                                    + "request={0}, response={1}",
                            request, result), e);
                    // エラーをMQTTのpublishで通知する場合に下記コメントアウトを外す
                    // mqttBrokerPublisher.publishError(createFlightProhibitedAreaError(result.getErrorMessage()));
                    return result;
                }
                // レスポンスコード:402,404～599
                if (!e.getResponseBodyAsString().isEmpty()) {
                    try {
                        result = mapper.readValue(e.getResponseBodyAsString(),
                                FlightProhibitedAreaSearchResponse.class);
                    } catch (JacksonException je) {
                        result =new FlightProhibitedAreaSearchResponse();
                        result.setErrorMessage(e.getResponseBodyAsString());
                    }
                }
                LOG.error(MessageFormat.format(
                        "飛行禁止エリア情報検索API呼び出し リクエスト失敗(値域の制約違反・認証失敗以外のエラーレスポンスコード)："
                                + "リトライ回数/最大リトライ回数={0}/{1}, request={2}, "
                                + "response={3}",
                        retryCount, maxRetryCount, request, result), e);
                clientException = e;
                // 接続失敗やタイムアウト
            } catch (ResourceAccessException e) {
                LOG.error(MessageFormat.format(
                        "飛行禁止エリア情報検索API呼び出し リクエスト失敗(接続失敗やタイムアウト)："
                                + "リトライ回数/最大リトライ回数={0}/{1}, request={2}",
                        retryCount, maxRetryCount, request), e);
                clientException = e;
            } catch (Exception e) {
                LOG.error(MessageFormat.format(
                        "飛行禁止エリア情報検索API呼び出し 異常終了(想定外)：" + "request={0}",
                        request), e);
                result = new FlightProhibitedAreaSearchResponse();
                result.setErrorMessage(e.getMessage());
                return result;
            }
            retryCount++;
            if (retryCount > maxRetryCount) {
                LOG.error(MessageFormat.format(
                        "飛行禁止エリア情報検索API呼び出し 異常終了(リクエストの最大リトライ回数({0})回失敗)",
                        maxRetryCount), clientException);
                if (result == null) {
                    result = new FlightProhibitedAreaSearchResponse();
                    result.setErrorMessage(clientException.getMessage());
                }
                return result;
            }
        }
    }
    /**
     * エラー通知作成.
     *
     * @param errorMessage エラーメッセージ
     * @return エラー通知
     */
    FlightProhibitedAreaError createFlightProhibitedAreaError(String errorMessage) {
        return FlightProhibitedAreaError.builder()
                .occurrenceTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)))
                .errorMessage(errorMessage).build();
    }
}
