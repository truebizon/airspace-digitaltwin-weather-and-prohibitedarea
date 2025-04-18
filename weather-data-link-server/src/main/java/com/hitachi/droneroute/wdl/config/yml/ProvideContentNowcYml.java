package com.hitachi.droneroute.wdl.config.yml;

import com.hitachi.droneroute.wdl.dto.jmbsc.Area;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotEmpty;

/**
 * 降水ナウキャストで提供する情報のYAML(provide-content.nowc).
 *
 * @author isao.kaneko
 */
@ConfigurationProperties(prefix = "provide-content.nowc")
@Validated
@Getter
public class ProvideContentNowcYml implements ProvideContent {

    /** 地域CSV. */
    @NotEmpty
    private final List<String> areaCsvs;

    /** 地域リスト. */
    private List<Area> areas = new ArrayList<>();

    /** 最低予報時間(分). */
    private int minForecastTime;

    /** パラメータカテゴリ. */
    private int parameterCategory;

    /** パラメータ番号リスト. */
    @NotEmpty
    private List<Integer> parameterNumbers;

    /**
     * コンストラクタ.
     *
     * @param areaCsvs          地域CSV
     * @param minForecastTime   最低予報時間(分)
     * @param parameterCategory パラメータカテゴリ
     * @param parameterNumbers  パラメータ番号リスト
     */
    public ProvideContentNowcYml(List<String> areaCsvs, int minForecastTime,
            int parameterCategory, List<Integer> parameterNumbers) {
        super();
        this.areaCsvs = areaCsvs;
        for (String areaCsv : areaCsvs) {
            String[] areaParts = areaCsv.split(",");
            areas.add(new Area(new BigDecimal(areaParts[0]),
                    new BigDecimal(areaParts[1]), new BigDecimal(areaParts[2]),
                    new BigDecimal(areaParts[3])));
        }
        this.minForecastTime = minForecastTime;
        this.parameterCategory = parameterCategory;
        this.parameterNumbers = parameterNumbers;
    }
}
