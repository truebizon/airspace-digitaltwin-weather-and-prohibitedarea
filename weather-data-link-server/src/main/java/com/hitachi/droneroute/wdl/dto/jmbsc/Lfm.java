package com.hitachi.droneroute.wdl.dto.jmbsc;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.config.yml.ProvideContent;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.ToString;

/** 局地数値予報モデル. */
@Getter
@ToString(callSuper = true)
public class Lfm extends Grib2 {

    /**
     * コンストラクタ.
     *
     * @param buffer         GRIB2メッセージデータを含むByteBuffer
     * @param yml            GRIB2の設定を含むYAML
     * @param provideContent 提供コンテンツ
     */
    public Lfm(@Nonnull ByteBuffer buffer, @Nonnull Grib2Yml yml,
            @Nonnull ProvideContent provideContent) {
        super(buffer, yml, provideContent);
    }

    @Override
    Section5 createSection5(ByteBuffer buffer, int dataSize,
            int sectionDataSize, Grib2Yml yml) {
        Section5 result = new Section5(buffer,
                yml.getSection5SimpleCompOffset() + dataSize,
                yml.getSection5SimpleCompLength());
        return result;
    }

    @Override
    int setSection6(int dataSize, ByteBuffer buffer, Grib2Yml yml,
            boolean isFirst) {
        int result = dataSize + getSectionDataSize(buffer, dataSize,
                yml.getDataSizeOffset(), yml.getDataSizeLength());
        if (isFirst) {
            this.section6 = new Section6(buffer,
                    yml.getSection6BitmapOffset() + dataSize, result);
        }
        return result;
    }

    @Override
    Section7 createSection7(ByteBuffer buffer, int dataSize,
            int sectionDataSize, Grib2Yml yml, Section5 section5) {
        Section7 result =
                new Section7(buffer, yml.getSection7OctetSeqOffset() + dataSize,
                        sectionDataSize + dataSize, section5.getSimpleConfig());
        return result;
    }
}
