package com.hitachi.droneroute.wdl.config.yml;

import com.hitachi.droneroute.wdl.dto.jmbsc.Area;
import java.util.List;

/** YAMLの提供する情報のインターフェース. */
public interface ProvideContent {

    /**
     * 地域リストを取得する.
     * 
     * @return 地域リスト
     */
    public List<Area> getAreas();

    /**
     * 最低予報時間(分)を取得する.
     * 
     * @return 最低予報時間(分)
     */
    public int getMinForecastTime();

    /**
     * パラメータカテゴリを取得する.
     * 
     * @return パラメータカテゴリ
     */
    public int getParameterCategory();

    /**
     * パラメータ番号リストを取得する.
     * 
     * @return パラメータ番号リスト
     */
    public List<Integer> getParameterNumbers();
}
