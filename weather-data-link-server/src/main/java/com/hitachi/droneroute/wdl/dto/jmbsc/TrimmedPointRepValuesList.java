package com.hitachi.droneroute.wdl.dto.jmbsc;

import com.hitachi.droneroute.wdl.config.yml.ProvideContent;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/** 不要データ削除済みの座標点の代表値リスト. */
@Getter
@ToString
public class TrimmedPointRepValuesList {

    /**
     * 分割した座標点の代表値リスト. ※分割があるのは提供地域が複数ある場合など
     */
    private List<List<CoordinatePointRepValue>> separatedRepValuesList =
            new ArrayList<>();

    /**
     * コンストラクタ.
     *
     * @param pointRepValuesList 座標点の代表値リスト
     * @param provideContent     提供コンテンツ
     */
    public TrimmedPointRepValuesList(PointRepValuesList pointRepValuesList,
            ProvideContent provideContent) {
        // 座標点の代表値リストの内で、
        for (Area area : provideContent.getAreas()) {
            List<CoordinatePointRepValue> trimmedRepValues = new ArrayList<>();
            for (CoordinatePointRepValue value : pointRepValuesList
                    .getRepValues()) {
                // 座標点の代表値の緯度が提供コンテンツの地域リストの最小緯度から最大緯度までの間にあり、
                if (value.getLat().compareTo(area.getMinLat()) >= 0
                        && value.getLat().compareTo(area.getMaxLat()) <= 0
                        // 座標点の代表値の経度も提供コンテンツの地域リストの最小経度から最大経度までの間にあるものを抽出
                        && value.getLon().compareTo(area.getMinLon()) >= 0
                        && value.getLon().compareTo(area.getMaxLon()) <= 0) {
                    trimmedRepValues.add(value);
                }
            }
            if (trimmedRepValues.size() > 0) {
                separatedRepValuesList.add(trimmedRepValues);
            }
        }
    }
}
