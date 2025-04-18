package com.hitachi.droneroute.wdl.service.jmbsc;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.dto.jmbsc.Grib2;
import com.hitachi.droneroute.wdl.dto.nec.JmbscWeatherRegisterRequest;

/** Grib2からリクエストへの変換サービスインターフェース. */
public interface Grib2ConvertService {

    /**
     * 天気情報登録リクエスト作成.
     * 
     * @param grib GRIB2
     * @param yml  GRIB2の設定を含むYAML
     * @return 天気情報登録リクエスト
     */
    JmbscWeatherRegisterRequest execute(Grib2 grib, Grib2Yml yml);
}
