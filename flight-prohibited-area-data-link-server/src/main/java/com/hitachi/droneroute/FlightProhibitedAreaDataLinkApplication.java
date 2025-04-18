package com.hitachi.droneroute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 飛行禁止エリア取得機能.
 *
 * @author isao.kaneko
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FlightProhibitedAreaDataLinkApplication {

    /**
     * 起動.
     *
     * @param args コマンドライン引数※使用しない
     */
    public static void main(String[] args) {
        SpringApplication.run(FlightProhibitedAreaDataLinkApplication.class,
                args);
    }
}
