package com.hitachi.droneroute.wdl.dto.jmbsc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 複数節(第4、5、7節、座標点の代表値、不要データ削除済みの座標点の代表値)リスト. */
@Getter
@Setter
@ToString
public class MultiSection {
    /** 第4節. */
    Section4 section4;
    /** 第5節. */
    Section5 section5;
    /** 第7節. */
    Section7 section7;
    /** 座標点の代表値リスト. */
    PointRepValuesList pointRepValuesList;
    /** 不要データ削除済みの座標点の代表値リスト. */
    TrimmedPointRepValuesList trimmedPointRepValuesList;
}
