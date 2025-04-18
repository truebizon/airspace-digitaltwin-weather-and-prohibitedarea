package com.hitachi.droneroute.fpadl.config.yml;

import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotEmpty;

/**
 * application.ymlのrest.dips.
 *
 * @author isao.kaneko
 */

@ConfigurationProperties(prefix = "rest.dips")
@Validated
@Getter
public class RestDipsYml {

    /** 座標リスト. */
    @NotEmpty
    private final List<String> coordinatesList;

    /**
     * コンストラクタ.
     *
     * @param coordinatesList 座標リスト
     */
    public RestDipsYml(List<String> coordinatesList) {
        super();
        this.coordinatesList = coordinatesList;
    }
}
