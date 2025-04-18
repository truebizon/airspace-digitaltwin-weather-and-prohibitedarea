package com.hitachi.droneroute.wdl.service.jmbsc.impl;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.config.yml.ProvideContent;
import com.hitachi.droneroute.wdl.config.yml.ProvideContentNowcYml;
import com.hitachi.droneroute.wdl.dto.jmbsc.Grib2;
import com.hitachi.droneroute.wdl.dto.jmbsc.Nowc;
import com.hitachi.droneroute.wdl.service.jmbsc.Grib2ConvertService;
import com.hitachi.droneroute.wdl.service.jmbsc.WeatherInfoDataLinkService;
import com.hitachi.droneroute.wdl.service.nec.JmbscWeatherRegisterService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrecipitationLiveDataLinkServiceImpl extends
        WeatherInfoDataLinkServiceImpl implements WeatherInfoDataLinkService {

    /** ロガー. */
    private static final Logger LOG =
            LoggerFactory.getLogger(PrecipitationLiveDataLinkServiceImpl.class);

    /** GRIB2の設定を含むYAML. */
    private final Grib2Yml yml;

    /** application.ymlの提供する情報の降水ナウキャスト. */
    private final ProvideContentNowcYml provideContent;

    /** 気象情報変換サービス. */
    @Qualifier("nowcConvertServiceImpl")
    private final Grib2ConvertService convertService;

    /** 気象情報登録サービス. */
    private final JmbscWeatherRegisterService registerService;

    /** ルートディレクトリ. */
    @Value("${sftp.root-directory.nowc}")
    private String rootDirectory;

    /** ファイル名フォーマット. */
    @Value("${grib2.file-name.format.nowc}")
    private String fileNameFormat;

    @Override
    public void execute() {
        LOG.info("開始：降水量実況情報取得");
        try {
            super.execute(rootDirectory, fileNameFormat, yml, provideContent,
                    convertService, registerService);
            LOG.info("終了：降水量実況情報取得");
        } catch (Exception e) {
            LOG.error("異常終了：降水量実況情報取得", e);
        }
    }

    @Override
    Grib2 readFile(ChannelSftp sftp, String fileName, Grib2Yml yml,
            ProvideContent provideContent) throws IOException, SftpException {
        LOG.info("開始：降水ナウキャスト(5分)ファイル読み込み：{}", fileName);
        try (InputStream inputStream = sftp.get(fileName)) {
            ByteBuffer buffer = convertInputStreamToByteBuffer(inputStream);
            Grib2 grib = new Nowc(buffer, yml, provideContent);
            LOG.info("終了：降水ナウキャスト(5分)ファイル読み込み：{}", fileName);
            return grib;
        }
    }
}
