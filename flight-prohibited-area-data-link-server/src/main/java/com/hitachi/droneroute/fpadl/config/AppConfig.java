package com.hitachi.droneroute.fpadl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * アプリケーション設定.
 *
 * @author isao.kaneko
 *
 */
@Configuration
public class AppConfig {

    /**
     * 接続タイムアウト時間（ミリ秒）.
     */
    @Value("${rest.timeout.connect}")
    private int connectTimeout;

    /**
     * 読み取りタイムアウト時間（ミリ秒）.
     */
    @Value("${rest.timeout.read}")
    private int readTimeout;

    /**
     * 設定を追加したHTTPクライアントの作成.
     *
     * @return 設定を追加したHTTPクライアント
     * @throws Exception 例外
     */
    @Bean
    RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }
}
