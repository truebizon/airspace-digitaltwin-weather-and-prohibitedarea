package com.hitachi.droneroute.fpadl.config.yml;

import java.text.MessageFormat;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.ymlのrest.dips.add-minutes-to-now.
 *
 * @author isao.kaneko
 */

@ConfigurationProperties(prefix = "rest.dips.add-minutes-to-now")
//@Validated
@Getter
public class RestDipsAddMinutesToNowYml {

    /** 検索期間(FROM)を算出するために現在時刻に加算する分. */
    private final Integer searchPeriodFrom;

    /** 検索期間(TO)を算出するために現在時刻に加算する分. */
    private final Integer searchPeriodTo;

    /**
     * コンストラクタ.
     *
     * @param searchPeriodFrom 検索期間(FROM)を算出するために現在時刻に加算する分
     * @param searchPeriodTo   検索期間(TO)を算出するために現在時刻に加算する分
     */
    public RestDipsAddMinutesToNowYml(Integer searchPeriodFrom,
            Integer searchPeriodTo) {
        super();
        this.searchPeriodFrom = searchPeriodFrom;
        this.searchPeriodTo = searchPeriodTo;

        // 検索開始、終了日間の検証
        if (searchPeriodFrom != null && searchPeriodTo != null
                && searchPeriodFrom > searchPeriodTo) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "検索期間TOを算出するために現在時刻に加算する分が検索期間FROMのものより大きい："
                            + "検索期間TOを算出するために現在時刻に加算する分={0}, "
                            + "検検索期間FROMを算出するために現在時刻に加算する分={1}",
                    searchPeriodTo, searchPeriodFrom));
        }
    }
}
