package com.hitachi.droneroute.wdl.dto.jmbsc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.config.yml.ProvideContent;

import javax.annotation.Nonnull;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import lombok.Getter;
import lombok.ToString;

/** GRIB2. */
@Getter
@ToString
public abstract class Grib2 {

    /** ロガー. */
    // private static final Logger LOG = LoggerFactory.getLogger(Grib2.class);

    /** 第1節. */
    Section1 section1;
    /** 第3節. */
    Section3 section3;
    /** 第6節. */
    Section6 section6;
    /** 複数節(第4、5、7節、座標点の代表値、不要データ削除済みの座標点の代表値)リスト. */
    List<MultiSection> multiSections = new ArrayList<>();

    /**
     * コンストラクタ.
     *
     * @param buffer         GRIB2メッセージデータを含むByteBuffer
     * @param yml            GRIB2の設定を含むYAML
     * @param provideContent 提供コンテンツ
     */
    public Grib2(@Nonnull ByteBuffer buffer, @Nonnull Grib2Yml yml,
            @Nonnull ProvideContent provideContent) {
        int dataSize = yml.getSection0DataSize();
        // LOG.info("dataSize0: " + dataSize);
        int allDataSize = (int) (Grib2
                .createBuffer(buffer, yml.getAllDataSizeOffset(),
                        yml.getAllDataSizeLength())
                .getLong() & 0xFFFFFFFFFFFFFFFFL);
        // LOG.info("allDataSize: " + allDataSize);
        dataSize = setSection1(dataSize, buffer, yml);
        // LOG.info("dataSize1: " + dataSize);
        dataSize = setSection3(dataSize, buffer, yml);
        // LOG.info("dataSize3: " + dataSize);
        MultiSection multiSection = new MultiSection();

        boolean isFirst = true;
        while (dataSize < allDataSize - yml.getSection8DataSize()) {
            int sectionNumber = Grib2.createBuffer(buffer,
                    dataSize + yml.getSectionNumberOffset(),
                    yml.getSectionNumberLength()).get() & 0xFF;

            switch (sectionNumber) {
                case 4:
                    // LOG.info("dataSize4_直前: " + dataSize);
                    multiSection = new MultiSection();
                    dataSize = addSection4(dataSize, buffer, yml, multiSection);
                    // if (isProvideSection(multiSection.getSection4(),provideContent)) {
                    // LOG.info("dataSize4: " + dataSize);
                    // }
                    break;
                case 5:
                    dataSize = addSection5(dataSize, buffer, yml, multiSection,
                            provideContent);
                    // if (isProvideSection(multiSection.getSection4(),provideContent)) {
                    // LOG.info("dataSize5: " + dataSize);
                    // }
                    break;
                case 6:
                    // if (isFirst || isProvideSection(multiSection.getSection4(),
                    // provideContent)) {
                    // LOG.info("dataSize6_直前: " + dataSize);
                    // }
                    dataSize = setSection6(dataSize, buffer, yml, isFirst);

                    // if (isFirst || isProvideSection(multiSection.getSection4(),provideContent)) {
                    // LOG.info("dataSize6: " + dataSize);
                    // }
                    break;
                case 7:
                    dataSize = addSection7(dataSize, buffer, yml, multiSection,
                            provideContent);
                    isFirst = false;
                    // if (isProvideSection(multiSection.getSection4(),provideContent)) {
                    // LOG.info("dataSize7: " + dataSize);
                    // }
                    if (!isProvideSection(multiSection.getSection4(),
                            provideContent)) {
                        break;
                    }
                    addPointRepValueLists(section3, section6, multiSection);
                    multiSection.setSection7(null);
                    trim(provideContent, multiSection);
                    multiSection.setPointRepValuesList(null);
                    if (CollectionUtils
                            .isEmpty(multiSection.getTrimmedPointRepValuesList()
                                    .getSeparatedRepValuesList())) {
                        break;
                    }
                    multiSections.add(multiSection);

                    break;
                default:
                    throw new IllegalArgumentException("想定外の節番号: " + sectionNumber);
            }
        }
    }

    /**
     * 指定されたオフセットと長さに基づいて、元のByteBufferから新しいByteBufferを作成.
     *
     * @param srcByteBuffer 元のByteBuffer
     * @param offset        コピーを開始する位置
     * @param length        新しいByteBufferの長さ
     * @return 指定された範囲のデータを含む新しいByteBuffer
     */
    public static ByteBuffer createBuffer(ByteBuffer srcByteBuffer, int offset,
            int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (int i = offset; i < offset + length; i++) {
            buffer.put(srcByteBuffer.get(i));
        }
        buffer.flip();
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer;
    }

    /**
     * 第1節を設定し、第1節を含めたデータサイズを返す.
     *
     * @param dataSize 第1節を含まないデータサイズ
     * @param buffer   GRIB2メッセージデータを含むByteBuffer
     * @param yml      第1節の設定を含むYAML
     * @return dataSizeに第1節のデータサイズを加算したデータサイズ
     */
    int setSection1(int dataSize, @Nonnull ByteBuffer buffer,
                @Nonnull Grib2Yml yml) {
        this.section1 = new Section1(buffer, yml.getSection1Offset() + dataSize,
                yml.getSection1Length());

        int result = dataSize + getSectionDataSize(buffer, dataSize,
                yml.getDataSizeOffset(), yml.getDataSizeLength());
        return result;
    }

    /**
     * 指定されたByteBufferから節のサイズを取得.
     *
     * @param buffer   データを含む元のByteBuffer
     * @param dataSize 読み取るデータのサイズ
     * @param offset   ByteBuffer内でデータが開始するオフセット
     * @param length   読み取るデータの長さ
     * @return 節のサイズ
     */
    int getSectionDataSize(@Nonnull ByteBuffer buffer, int dataSize,
                int offset, int length) {
        int result = Grib2.createBuffer(buffer, dataSize + offset, length).getInt()
                & 0xFFFFFFFF;
        return result;
    }

    /**
     * 第3節を設定し、第3節を含めたデータサイズを返す.
     *
     * @param dataSize 第3節を含まないデータサイズ
     * @param buffer   GRIB2メッセージデータを含むByteBuffer
     * @param yml      第3節の設定を含むYAML
     * @return dataSizeに第3節のデータサイズを加算したデータサイズ
     */
    int setSection3(int dataSize, @Nonnull ByteBuffer buffer,
            @Nonnull Grib2Yml yml) {
        this.section3 = new Section3(buffer, yml.getSection3CountOffset() + dataSize,
                yml.getSection3FirstOffset() + dataSize,
                yml.getSection3IntervalOffset() + dataSize,
                yml.getSection3Length(),
                yml.getSection3CoordinateScaleFactor());

        int sectionDataSize = getSectionDataSize(buffer, dataSize,
                yml.getDataSizeOffset(), yml.getDataSizeLength());
        int result = dataSize + sectionDataSize;
        return result;
    }

    /**
     * 第4節を追加し、第4節を含めたデータサイズを返す.
     *
     * @param dataSize     第4節を含まないデータサイズ
     * @param buffer       GRIB2メッセージデータを含むByteBuffer
     * @param yml          第4節の設定を含むYAML
     * @param multiSection 複数節
     * @return dataSizeに第4節のデータサイズを加算したデータサイズ
     */
    int addSection4(int dataSize, @Nonnull ByteBuffer buffer,
            @Nonnull Grib2Yml yml, @Nonnull MultiSection multiSection) {
        Section4 section4 = new Section4(buffer,
                yml.getSection4ParameterOffset() + dataSize,
                yml.getSection4ParameterLength(),
                yml.getSection4ForecastTimeOffset() + dataSize,
                yml.getSection4ForecastTimeLength(),
                yml.getSection4EndDateTimeOffset() + dataSize,
                yml.getSection4EndDateTimeLength());
        multiSection.setSection4(section4);

        int result = dataSize + getSectionDataSize(buffer, dataSize,
                yml.getDataSizeOffset(), yml.getDataSizeLength());
        return result;
    }

    /**
     * 提供する節かどうか.
     *
     * @param section4       第4節
     * @param provideContent 提供コンテンツ
     * @return true:提供する、false:提供しない
     */
    boolean isProvideSection(@Nonnull Section4 section4,
            @Nonnull ProvideContent provideContent) {
        return (section4.getParameterCategory() == provideContent
                .getParameterCategory())
                && (provideContent.getParameterNumbers()
                        .contains(section4.getParameterNumber()));
    }

    /**
     * 第5節を追加し、第5節を含めたデータサイズを返す.
     *
     * @param dataSize       第5節を含まないデータサイズ
     * @param buffer         GRIB2メッセージデータを含むByteBuffer
     * @param yml            第5節の設定を含むYAML
     * @param multiSection   複数節
     * @param provideContent 提供コンテンツ
     * @return dataSizeに第5節のデータサイズを加算したデータサイズ
     */
    int addSection5(int dataSize, @Nonnull ByteBuffer buffer,
            @Nonnull Grib2Yml yml, @Nonnull MultiSection multiSection,
            @Nonnull ProvideContent provideContent) {
        int sectionDataSize = getSectionDataSize(buffer, dataSize,
                yml.getDataSizeOffset(), yml.getDataSizeLength());
        int result = dataSize + sectionDataSize;

        if (!isProvideSection(multiSection.getSection4(), provideContent)) {
            return result;
        }

        Section5 section5 = createSection5(buffer, dataSize, sectionDataSize, yml);
        multiSection.setSection5(section5);
        return result;
    }

    /**
     * 第5節を作成.
     *
     * @param buffer          GRIB2メッセージデータを含むByteBuffer
     * @param dataSize        第5節を含まないデータサイズ
     * @param sectionDataSize 第5節のデータサイズ
     * @param yml             第5節の設定を含むYAML
     * @return 第5節
     */
    abstract Section5 createSection5(ByteBuffer buffer, int dataSize,
            int sectionDataSize, Grib2Yml yml);

    /**
     * 第6節を設定し、第6節を含めたデータサイズを返す.
     *
     * @param dataSize 第6節を含まないデータサイズ
     * @param buffer   GRIB2メッセージデータを含むByteBuffer
     * @param yml      第6節の設定を含むYAML
     * @param isFirst  最初の第6節かどうか
     * @return dataSizeに第6節のデータサイズを加算したデータサイズ
     */
    int setSection6(int dataSize, ByteBuffer buffer, Grib2Yml yml,
            boolean isFirst) {
        int result = dataSize + getSectionDataSize(buffer, dataSize,
                yml.getDataSizeOffset(), yml.getDataSizeLength());
        return result;
    }

    /**
     * 第7節を追加し、第7節を含めたデータサイズを返す.
     *
     * @param dataSize       第7節を含まないデータサイズ
     * @param buffer         GRIB2メッセージデータを含むByteBuffer
     * @param yml            第7節の設定を含むYAML
     * @param multiSection   複数節
     * @param provideContent 提供コンテンツ
     * @return dataSizeに第7節のデータサイズを加算したデータサイズ
     */
    int addSection7(int dataSize, @Nonnull ByteBuffer buffer,
                @Nonnull Grib2Yml yml, @Nonnull MultiSection multiSection,
                @Nonnull ProvideContent provideContent) {
        int sectionDataSize = getSectionDataSize(buffer, dataSize,
                yml.getDataSizeOffset(), yml.getDataSizeLength());
        int result = dataSize + sectionDataSize;
        if (!isProvideSection(multiSection.getSection4(), provideContent)) {
            return result;
        }
        Section7 section7 = createSection7(buffer, dataSize, sectionDataSize,
                yml, multiSection.getSection5());
        multiSection.setSection7(section7);
        return result;
    }

    /**
     * 第7節を作成.
     *
     * @param buffer          GRIB2メッセージデータを含むByteBuffer
     * @param dataSize        第7節を含まないデータサイズ
     * @param sectionDataSize 第7節のデータサイズ
     * @param yml             第7節の設定を含むYAML
     * @param section5        第5節
     * @return 第7節
     */
    abstract Section7 createSection7(ByteBuffer buffer, int dataSize,
            int sectionDataSize, Grib2Yml yml, Section5 section5);

    /**
     * 座標点の代表値リストを追加.
     *
     * @param section3     第3節
     * @param section6     第6節
     * @param multiSection 複数節
     */
    void addPointRepValueLists(@Nonnull Section3 section3,
            @Nonnull Section6 section6, @Nonnull MultiSection multiSection) {
        PointRepValuesList pointRepValueList = new PointRepValuesList(section3, multiSection.getSection5(),
                section6, multiSection.getSection7());
        multiSection.setPointRepValuesList(pointRepValueList);
    }

    /**
     * 不要データ削除.
     *
     * @param provideContent 提供コンテンツ
     * @param multiSection   複数節
     */
    public void trim(@Nonnull ProvideContent provideContent,
            @Nonnull  MultiSection multiSection) {
        PointRepValuesList valuesList = multiSection.getPointRepValuesList();
        TrimmedPointRepValuesList trimmedList = new TrimmedPointRepValuesList(valuesList, provideContent);
        multiSection.setTrimmedPointRepValuesList(trimmedList);
    }
}
