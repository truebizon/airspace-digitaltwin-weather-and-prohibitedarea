package com.hitachi.droneroute.wdl.dto.jmbsc;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.config.yml.ProvideContent;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.ToString;

/** 降水短時間予報. */
@Getter
@ToString(callSuper = true)
public class Srf extends Grib2 {

    /**
     * コンストラクタ.
     *
     * @param buffer         GRIB2メッセージデータを含むByteBuffer
     * @param yml            GRIB2の設定を含むYAML
     * @param provideContent 提供コンテンツ
     */
    public Srf(@Nonnull ByteBuffer buffer, @Nonnull Grib2Yml yml,
            @Nonnull ProvideContent provideContent) {
        super(buffer, yml, provideContent);
    }

    @Override
    Section5 createSection5(ByteBuffer buffer, int dataSize,
            int sectionDataSize, Grib2Yml yml) {
        Section5 result =
                new Section5(buffer, yml.getSection5BitCountOffset() + dataSize,
                        yml.getSection5BitCountLength(),
                        yml.getSection5CurrentMaxLevelLength(),
                        yml.getSection5RepValueOffset() + dataSize,
                        sectionDataSize + dataSize);
        return result;
    }

    @Override
    public boolean isProvideSection(@Nonnull Section4 section4,
            @Nonnull ProvideContent provideContent) {
        // 予報時間が最低予報時間より小さければ、すべて不要データとする
        if (section4.getForecastTime() < provideContent.getMinForecastTime()) {
            return false;
        }
        return super.isProvideSection(section4, provideContent);
    }

    @Override
    Section7 createSection7(ByteBuffer buffer, int dataSize,
            int sectionDataSize, Grib2Yml yml, Section5 section5) {
        Section7 result =
                new Section7(buffer, yml.getSection7OctetSeqOffset() + dataSize,
                        sectionDataSize + dataSize,
                        section5.getRunLengthConfig().getBitCount(),
                        section5.getRunLengthConfig().getCurrentMaxLevel());
        return result;
    }
}
