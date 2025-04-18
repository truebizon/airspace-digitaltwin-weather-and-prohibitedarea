package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Area {

    /** 最小緯度. */
    private BigDecimal minLat;

    /** 最小経度. */
    private BigDecimal minLon;

    /** 最大緯度. */
    private BigDecimal maxLat;

    /** 最大経度. */
    private BigDecimal maxLon;

}
