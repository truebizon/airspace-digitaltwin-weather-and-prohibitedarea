package com.hitachi.droneroute.wdl.service.jmbsc.impl;

import com.hitachi.droneroute.wdl.config.yml.Grib2Yml;
import com.hitachi.droneroute.wdl.config.yml.ProvideContent;
import com.hitachi.droneroute.wdl.dto.jmbsc.Grib2;
import com.hitachi.droneroute.wdl.dto.nec.JmbscWeatherRegisterRequest;
import com.hitachi.droneroute.wdl.service.jmbsc.Grib2ConvertService;
import com.hitachi.droneroute.wdl.service.nec.JmbscWeatherRegisterService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

/** 気象情報取得サービスの抽象クラス. */
public abstract class WeatherInfoDataLinkServiceImpl {

    /** ロガー. */
    private static final Logger LOG =
            LoggerFactory.getLogger(WeatherInfoDataLinkServiceImpl.class);

    /** SFTPホスト. */
    @Value("${sftp.host}")
    private String host;

    /** SFTPポート. */
    @Value("${sftp.port}")
    private int port;

    /** SFTPユーザー名. */
    @Value("${sftp.username}")
    private String username;

    /** SFTPパスワード. */
    @Value("${sftp.password}")
    private String password;

    /** known_hostsファイル確認. */
    @Value("${sftp.known-hosts-check}")
    private boolean knownHostsCheck;

    /** known_hostsファイル. */
    @Value("${sftp.known-hosts}")
    private String knownHosts;

    /** 作成ステータスのオフセット. */
    @Value("${grib2.common.create-status.offset}")
    private int createStatusOffset;

    /** 作成ステータスの長さ. */
    @Value("${grib2.common.create-status.length}")
    private int createStatusLength;

    /** 本番用作成ステータス. */
    @Value("${grib2.common.create-status.operational-product}")
    private int productStatus;

    /**
     * 実行.
     *
     * @param rootDirectory   ルートディレクトリ
     * @param fileNameFormat  ファイル名の形式
     * @param yml             GRIB2の設定を含むYAML
     * @param provideContent  ProvideContent
     * @param convertService  Grib2ConvertService
     * @param registerService JmbscWeatherRegisterService
     * @throws Exception
     */
    protected void execute(String rootDirectory, String fileNameFormat,
            Grib2Yml yml, ProvideContent provideContent,
            Grib2ConvertService convertService,
            JmbscWeatherRegisterService registerService) throws Exception {

        // SFTPサーバに接続
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftp = null;

        try {
            // セッションの作成と接続
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            // known_hostsファイル確認
            if (knownHostsCheck) {
                jsch.setKnownHosts(knownHosts);
            } else {
                session.setConfig("StrictHostKeyChecking", "no");
            }
            session.connect();

            // SFTPチャネルの作成と接続
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            sftp.cd(rootDirectory);

            // SFTPサーバからルートディレクトリの全ファイル名リストを取得
            List<String> fileNames = sftp.ls(".").stream()
                    .map(LsEntry.class::cast).map(LsEntry::getFilename)
                    // ファイル名が「.」で始まるなら除外
                    .filter(fileName -> !fileName.startsWith("."))
                    .collect(Collectors.toList());
            LOG.info("ルートディレクトリの全ファイル名: " + fileNames);

            // 全ファイル名リストを使って不要ファイルを除外し、
            // 最新の気象ファイル名リストを取得
            List<String> latestFileNames =
                    trimFiles(fileNames, fileNameFormat, yml, sftp);

            // 最新の気象ファイルあり
            if (!CollectionUtils.isEmpty(latestFileNames)) {
                latestWeatheFilesrProcess(latestFileNames, sftp, convertService,
                        yml, provideContent, registerService);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    /**
     * 不要なファイルを除外し、最新の気象ファイル名リストを返す.
     *
     * @param fileNames      全ファイル名リスト
     * @param fileNameFormat ファイル名の形式
     * @param yml            GRIB2の設定を含むYAML
     * @param sftp           SFTPチャネル
     * @return 最新の気象ファイル名リスト
     * @throws SftpException
     * @throws IOException
     */
    List<String> trimFiles(List<String> fileNames, String fileNameFormat,
            Grib2Yml yml, ChannelSftp sftp) throws SftpException, IOException {

        // ファイル名の末尾が「.tmp」ならファイル名リストから除外
        List<String> notTmpFileNames = fileNames.stream()
                .filter(fileName -> !fileName.endsWith(".tmp"))
                .collect(Collectors.toList());

        // 形式通りのファイル名リスト
        List<String> formatedFileNames = new ArrayList<>();
        // ファイル名が形式通りなら形式通りのリストに追加し、
        for (String fileName : notTmpFileNames) {
            if (fileName.matches(fileNameFormat) && isValidDateTime(fileName,
                    yml.getFileNameDateTimeOffset(),
                    yml.getFileNameDateTimeLength(),
                    yml.getFileNameDateTimeFormat())) {
                formatedFileNames.add(fileName);
            } else {
                // そうでないならSFTPサーバから削除
                LOG.info("ファイル名が形式通りでないため削除: " + fileName);
                sftp.rm(fileName);
            }
        }

        // 本番用のファイル名リスト
        List<String> productFileNames = new ArrayList<>();
        // ファイル名リストから1件ずつファイル名を取り出し、
        for (String fileName : formatedFileNames) {
            try (InputStream inputStream = sftp.get(fileName)) {
                // SFTPサーバからファイル名のファイルをStreamで取得し、
                // StreamをByteBufferに変換し、
                ByteBuffer byteBuffer =
                        convertInputStreamToByteBuffer(inputStream);

                // 特定区間のバイトを取得し、
                ByteBuffer createStatusBuffer = Grib2.createBuffer(byteBuffer,
                        createStatusOffset, createStatusLength);

                // そのバイトをintに変換した値が本番用のものなら本番用のファイル名リストに追加し、
                int createStatus = createStatusBuffer.get() & 0xFF;
                if (createStatus == productStatus) {
                    productFileNames.add(fileName);
                } else {
                    // そうでないならそのファイルをSFTPサーバから削除する
                    LOG.info("本番用のファイルでないため削除: " + fileName);
                    sftp.rm(fileName);
                }
            }
        }

        // 本番用のファイル名リストが0件なら、0件の最新のファイル名リストを返す
        if (CollectionUtils.isEmpty(productFileNames)) {
            return new ArrayList<>();
        } else {
            List<String> result = filterLatestFileName(productFileNames,
                    yml.getFileNameDateTimeOffset(),
                    yml.getFileNameDateTimeLength(), sftp);
            return result;
        }
    }

    /**
     * ファイル名リストでファイル名の日時部分が最も大きいファイル名リストを返し、 それ以外のファイル名のファイルをSFTPサーバから削除する.
     *
     * @param fileNames      全ファイル名リスト
     * @param dateTimeOffset ファイル名の日時部分のオフセット
     * @param dateTimeLength ファイル名の日時部分の長さ
     * @param sftp           SFTPチャネル
     * @return 最新のファイル名リスト
     * @throws SftpException
     */
    List<String> filterLatestFileName(List<String> fileNames,
            int dateTimeOffset, int dateTimeLength, ChannelSftp sftp)
            throws SftpException {
        List<String> latestFileNames = new ArrayList<>();
        if (fileNames.isEmpty()) {
            return latestFileNames;
        }
        // ファイル名リストをファイル名の日時部分をキー、ファイル名のリストを値とするMapに変換
        Map<String, List<String>> fileNameMap = fileNames.stream()
                .collect(Collectors.groupingBy(fileName -> fileName
                        .substring(dateTimeOffset, dateTimeOffset + dateTimeLength)));
        // fileNameMapのキーで最大の値を取得
        String latestDateTime = Collections.max(fileNameMap.keySet());
        // fileNameMapのキーが最大の値のファイル名リストをlatestFileNamesに追加
        latestFileNames.addAll(fileNameMap.get(latestDateTime));
        // fileNameMapのキーが最大の値以外のファイル名をSFTPサーバから削除
        for (String fileName : fileNames) {
            if (!fileNameMap.get(latestDateTime).contains(fileName)) {
                LOG.info("最新のファイル名でないため削除: " + fileName);
                sftp.rm(fileName);
            }
        }

        return latestFileNames;
    }

    /**
     * ファイル名の日時部分が正しい形式かどうかを判定する.
     *
     * @param fileName               ファイル名
     * @param dateTimeOffset         ファイル名の日時部分のオフセット
     * @param dateTimeLength         ファイル名の日時部分の長さ
     * @param fileNameDateTimeFormat ファイル名の日時部分の形式
     * @return 正しい形式ならtrue、そうでないならfalse
     */
    boolean isValidDateTime(String fileName, int dateTimeOffset,
            int dateTimeLength, String fileNameDateTimeFormat) {
        String dateTime = fileName.substring(dateTimeOffset,
                dateTimeOffset + dateTimeLength);
        SimpleDateFormat sdf = new SimpleDateFormat(fileNameDateTimeFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * InputStreamをByteBufferに変換する.
     *
     * @param inputStream InputStream
     * @return ByteBuffer
     * @throws IOException
     */
    ByteBuffer convertInputStreamToByteBuffer(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        ByteBuffer result = ByteBuffer.wrap(byteArray);
        result.flip();
        result.order(ByteOrder.BIG_ENDIAN);
        result.limit(byteArray.length);
        return result;
    }

    /**
     * 最新の気象ファイルの処理.
     *
     * @param fileNames       最新の気象ファイル名リスト
     * @param sftp            SFTPチャネル
     * @param convertService  Grib2からリクエストへの変換サービス
     * @param yml             GRIB2の設定を含むYAM
     * @param provideContent  提供コンテンツ
     * @param registerService 登録サービス
     * @throws SftpException
     * @throws IOException
     */
    void latestWeatheFilesrProcess(List<String> fileNames, ChannelSftp sftp,
            Grib2ConvertService convertService, Grib2Yml yml,
            ProvideContent provideContent,
            JmbscWeatherRegisterService registerService)
            throws IOException, SftpException {

        JmbscWeatherRegisterRequest request = null;

        for (String fileName : fileNames) {
            // SFTPサーバから最新の気象ファイルを読み込み
            Grib2 grib = readFile(sftp, fileName, yml, provideContent);

            // 気象データをリクエストデータに変換
            LOG.info("開始：気象データをリクエストデータに変換: " + fileName);
            JmbscWeatherRegisterRequest tmpRequest =
                    convertService.execute(grib, yml);
            if (request == null) {
                request = tmpRequest;
            } else {
                request.getWeather().addAll(tmpRequest.getWeather());
            }
            LOG.info("終了：気象データをリクエストデータに変換: " + fileName);
        }

        if (request == null || CollectionUtils.isEmpty(request.getWeather())) {
            LOG.info("登録APIに送信なし※リクエストデータなし");
        } else {
            // リクエストデータを登録APIに送信
            registerService.execute(request);
        }

        for (String fileName : fileNames) {
            // 最新の気象ファイルをSFTPサーバから削除
            LOG.info("最新の気象ファイルを削除: " + fileName);
            sftp.rm(fileName);
        }
    }

    /**
     * SFTPサーバからファイルを読み込み、Grib2を生成する.
     *
     * @param sftp           SFTPチャネル
     * @param fileName       ファイル名
     * @param yml            GRIB2の設定を含むYAML
     * @param provideContent 提供コンテンツ
     * @return Grib2 ファイルの内容
     * @throws IOException
     * @throws SftpException
     */
    abstract Grib2 readFile(ChannelSftp sftp, String fileName, Grib2Yml yml,
            ProvideContent provideContent) throws IOException, SftpException;
}
