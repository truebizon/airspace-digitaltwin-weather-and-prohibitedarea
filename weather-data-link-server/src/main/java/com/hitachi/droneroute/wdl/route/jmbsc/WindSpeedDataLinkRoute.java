package com.hitachi.droneroute.wdl.route.jmbsc;

import com.hitachi.droneroute.wdl.service.jmbsc.WeatherInfoDataLinkService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 気象業務支援センターの風速情報取得ルート.
 *
 * @author isao.kaneko
 */
@Component
@RequiredArgsConstructor
public class WindSpeedDataLinkRoute extends RouteBuilder {

    /** 風速取得サービス. */
    @Qualifier("windSpeedDataLinkServiceImpl")
    private final WeatherInfoDataLinkService importService;

    /** {@inheritDoc} */
    @Override
    public void configure() throws Exception {

        from("timer://jmbscWindSpeedDataLink?period="
                + "{{route.jmbsc.wind-speed.interval}}")
                        .routeId("jmbscWindSpeedDataLink")
                        .autoStartup("{{route.jmbsc.wind-speed.auto-startup}}")
                        .bean(importService, "execute()").end();
    }
}
