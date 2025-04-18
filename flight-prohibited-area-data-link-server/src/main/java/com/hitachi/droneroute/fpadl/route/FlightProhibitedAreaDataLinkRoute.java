package com.hitachi.droneroute.fpadl.route;

import com.hitachi.droneroute.fpadl.service.FlightProhibitedAreaRegisterService;
import com.hitachi.droneroute.fpadl.service.FlightProhibitedAreaSearchService;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 飛行禁止エリア情報取得ルート.
 *
 * @author isao.kaneko
 */
@Component
public class FlightProhibitedAreaDataLinkRoute extends RouteBuilder {

    /** 実行間隔 ミリ秒. */
    @Value("${route.interval}")
    private int interval;

    /** 自動起動. */
    @Value("${route.auto-startup}")
    private boolean isAutoStartup;

    /** 飛行禁止エリア情報検索サービス. */
    @Autowired
    private FlightProhibitedAreaSearchService searchService;

    /** 飛行禁止エリア情報登録サービス. */
    @Autowired
    private FlightProhibitedAreaRegisterService registerService;

    /** {@inheritDoc} */
    @Override
    public void configure() throws Exception {
        from("timer://flightProhibitedAreaDataLink?period=" + interval)
            .routeId("flightProhibitedAreaDataLink")
            .autoStartup(isAutoStartup)
            .choice()
                .when().simple("${properties:route.interval} <= 0")
                    .log(LoggingLevel.ERROR, "実行間隔異常:route.interval="
                            + interval).stop()
            .end()
            .bean(searchService, "execute")
            .choice()
                .when().simple("${in.body.errorMessage} == null")
                    .bean(registerService, "execute(${in.body})")
                .otherwise()
                    .log(LoggingLevel.INFO, "飛行禁止エリア情報登録 なし:飛行禁止エリア情報検索で異常終了")
            .end()
        .end();
    }
}
