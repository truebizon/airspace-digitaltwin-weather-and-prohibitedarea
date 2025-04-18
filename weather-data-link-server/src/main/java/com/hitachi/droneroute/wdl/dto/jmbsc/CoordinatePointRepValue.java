package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/** 座標点の代表値. */
@AllArgsConstructor
@Getter
@ToString
public class CoordinatePointRepValue {

    /** 緯度. */
    private BigDecimal lat;
    /** 経度. */
    private BigDecimal lon;
    /** 値. */
    private BigDecimal value;
}
