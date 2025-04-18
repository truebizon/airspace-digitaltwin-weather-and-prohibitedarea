package com.hitachi.droneroute.wdl.route.jmbsc;

import com.hitachi.droneroute.wdl.service.jmbsc.WeatherInfoDataLinkService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 気象業務支援センターの降水量実況情報取得ルート.
 *
 * @author isao.kaneko
 */
@Component
@RequiredArgsConstructor
public class PrecipitationLiveDataLinkRoute extends RouteBuilder {

    /** 降水量実況取得サービス. */
    @Qualifier("precipitationLiveDataLinkServiceImpl")
    private final WeatherInfoDataLinkService importService;

    /** {@inheritDoc} */
    @Override
    public void configure() throws Exception {

        from("timer://jmbscPrecipitationLiveDataLink?period="
                + "{{route.jmbsc.precipitation-live.interval}}")
                        .routeId("jmbscPrecipitationLiveDataLink")
                        .autoStartup("{{route.jmbsc.precipitation-live"
                                + ".auto-startup}}")
                        .bean(importService, "execute()").end();
    }
}
