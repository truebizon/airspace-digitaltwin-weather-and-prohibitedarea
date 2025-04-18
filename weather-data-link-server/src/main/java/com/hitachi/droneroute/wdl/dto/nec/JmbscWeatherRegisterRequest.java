package com.hitachi.droneroute.wdl.dto.nec;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * NECコンソへの気象業務支援センターの気象情報登録APIリクエストパラメータ.
 *
 * @author isao.kaneko
 */
@Getter
@AllArgsConstructor
@ToString
public class JmbscWeatherRegisterRequest {
    /** 気象. */
    private List<JmbscWeatherRegisterRequestWeather> weather;
}
