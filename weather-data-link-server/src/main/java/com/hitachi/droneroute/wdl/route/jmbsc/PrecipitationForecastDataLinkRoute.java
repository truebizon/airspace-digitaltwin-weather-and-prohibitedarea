package com.hitachi.droneroute.wdl.route.jmbsc;

import com.hitachi.droneroute.wdl.service.jmbsc.WeatherInfoDataLinkService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 気象業務支援センターの降水量予報情報取得ルート.
 *
 * @author isao.kaneko
 */
@Component
@RequiredArgsConstructor
public class PrecipitationForecastDataLinkRoute extends RouteBuilder {

    /** 降水量予報取得サービス. */
    @Qualifier("precipitationForecastDataLinkServiceImpl")
    private final WeatherInfoDataLinkService importService;

    /** {@inheritDoc} */
    @Override
    public void configure() throws Exception {

        from("timer://jmbscPrecipitationForecastDataLink?period="
                + "{{route.jmbsc.precipitation-forecast.interval}}")
                        .routeId("jmbscPrecipitationForecastDataLink")
                        .autoStartup("{{route.jmbsc.precipitation-forecast"
                                + ".auto-startup}}")
                        .bean(importService, "execute()").end();
    }
}
