package com.hitachi.droneroute.wdl.service.jmbsc.impl;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.config.yml.ProvideContent;
import com.hitachi.droneroute.wdl.config.yml.ProvideContentLfmYml;
import com.hitachi.droneroute.wdl.dto.jmbsc.Grib2;
import com.hitachi.droneroute.wdl.dto.jmbsc.Lfm;
import com.hitachi.droneroute.wdl.service.jmbsc.Grib2ConvertService;
import com.hitachi.droneroute.wdl.service.jmbsc.WeatherInfoDataLinkService;
import com.hitachi.droneroute.wdl.service.nec.JmbscWeatherRegisterService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WindSpeedDataLinkServiceImpl extends WeatherInfoDataLinkServiceImpl
        implements WeatherInfoDataLinkService {

    /** ロガー. */
    private static final Logger LOG =
            LoggerFactory.getLogger(WindSpeedDataLinkServiceImpl.class);

    /** GRIB2の設定を含むYAML. */
    private final Grib2Yml yml;

    /** 局地数値予報モデルで提供する情報のYAML. */
    private final ProvideContentLfmYml provideContent;

    /** 気象情報変換サービス. */
    @Qualifier("lfmConvertServiceImpl")
    private final Grib2ConvertService convertService;

    /** 気象情報登録サービス. */
    private final JmbscWeatherRegisterService registerService;

    /** ルートディレクトリ. */
    @Value("${sftp.root-directory.lfm}")
    private String rootDirectory;

    /** ファイル名フォーマット. */
    @Value("${grib2.file-name.format.lfm}")
    private String fileNameFormat;

    /** ファイル名の予報時間のオフセット. */
    @Value("${grib2.file-name.forecast-time.lfm.offset}")
    private int forecastTimeOffset;

    /** ファイル名の予報時間の長さ. */
    @Value("${grib2.file-name.forecast-time.lfm.length}")
    private int forecastTimeLength;

    /** ファイル名の予報時間リスト. */
    @Value("#{${grib2.file-name.forecast-time.lfm.values}}")
    private List<String> fileNameForecastTimes;

    @Override
    public void execute() {
        LOG.info("開始：風速情報取得");
        try {
            super.execute(rootDirectory, fileNameFormat, yml, provideContent,
                    convertService, registerService);
        } catch (Exception e) {
            LOG.error("異常終了：風速情報取得", e);
        }
        LOG.info("終了：風速情報取得");
    }

    @Override
    Grib2 readFile(ChannelSftp sftp, String fileName, Grib2Yml yml,
            ProvideContent provideContent) throws IOException, SftpException {
        LOG.info("開始：局地数値予報モデルファイル読み込み：{}", fileName);
        try (InputStream inputStream = sftp.get(fileName)) {
            ByteBuffer buffer = convertInputStreamToByteBuffer(inputStream);
            Grib2 grib = new Lfm(buffer, yml, provideContent);
            LOG.info("終了：局地数値予報モデルファイル読み込み：{}", fileName);
            return grib;
        }
    }

    @Override
    boolean isValidDateTime(String fileName, int dateTimeOffset,
            int dateTimeLength, String fileNameDateTimeFormat) {
        // ファイル名の予報時間が予報時間リストに含まれていなければ、falseを返す
        String forecastTime = fileName.substring(forecastTimeOffset,
                forecastTimeOffset + forecastTimeLength);
        if (!fileNameForecastTimes.contains(forecastTime)) {
            return false;
        }
        return super.isValidDateTime(fileName, dateTimeOffset, dateTimeLength,
                fileNameDateTimeFormat);
    }

    @Override
    List<String> filterLatestFileName(List<String> fileNames,
    int dateTimeOffset, int dateTimeLength, ChannelSftp sftp)
    throws SftpException {
        List<String> latestFileNames = super.filterLatestFileName(fileNames, dateTimeOffset,
                dateTimeLength, sftp);

        if(latestFileNames.size() != fileNameForecastTimes.size()) {
            LOG.info("最新のファイル数が{}で{}に満たないためファイル読み込みをスキップ",
                    latestFileNames.size(), fileNameForecastTimes.size());
            return new ArrayList<>();
        }
        return latestFileNames;
    }
}
