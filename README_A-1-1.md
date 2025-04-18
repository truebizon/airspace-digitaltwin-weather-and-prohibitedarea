# README

## 気象データ取扱いの注意点
気象業務支援センターから配信されるデータについて、気象業務支援センターの配信事業が、利用者の負担金のみによって成り立っていることからデータを加工せずに第三者に公開することはできる限りご遠慮いただきたくお願いいたします。
２次利用で運航事業者等に気象情報を広く表示させる場合の負担金の取り扱いついては、気象業務支援センターにご相談ください。


## インストール

このプロジェクトをローカル環境にインストールする手順を記載します。

1. git clone後

```bash
# 気象データ取得用
cd weather-data-link-server

# プロジェクトのビルド
./gradlew build  

# プロジェクトをビルドして実行可能JARファイルを作成します。
./gradlew bootJar
```

```bash
# 飛行禁止エリア取得用
cd flight-prohibited-area-data-link-server

# プロジェクトのビルド
./gradlew build  

# プロジェクトをビルドして実行可能JARファイルを作成します。
./gradlew bootJar
```

## 環境変数

環境変数の説明と設定値例を記載します。
なお、気象情報を取得するためには気象業務支援センターから情報を配信していただく必要があります。
接続方式の詳細は、気象業務支援センターのホームページをご確認ください。

| 環境変数名                                                          | 物理名(docker)                                           | 環境変数名                                                                | 説明                                                                                         | 設定値例                                                                                                                                                                                                                      |
|---------------------------------------------------------------------|----------------------------------------------------------|---------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| A-1-6_外部連携機能のDIPS飛行禁止エリア情報取得機能のエンドポイント  | REST_DIPS_ENDPOINT_TO_CONTEXTPATH                        | --rest.dips.endpoint.to.contextpath                                       | A-1-6_外部連携機能のDIPS飛行禁止エリア情報取得機能のエンドポイントです                       | https://{A-1-6_外部連携機能サーバのFQDN}:{ポート番号}/external                                                                                                                                                                |
| A-1-1_空域デジタルツインサーバのURL                                 | REST_NEC_ENDPOINT_TO_CONTEXTPATH                         | --rest.nec.endpoint.to.contextpath                                        | A-1-1_空域デジタルツインサーバのURLです                                                      | https://{A-1-1_空域デジタルツインサーバのFQDN}:{ポート番号}/airDtw                                                                                                                                                            |
| 飛行禁止エリア取得の自動起動                                        | ROUTE_FLIGHT_PROHIBITED_AREA_AUTO_STARTUP                | --route.auto-startup                                                      | 飛行禁止エリア取得の自動起動の設定です                                                       | true                                                                                                                                                                                                                          |
| 飛行禁止エリアの取得間隔                                            | ROUTE_FLIGHT_PROHIBITED_AREA_INTERVAL                    | --route.interval                                                          | 飛行禁止エリアの取得間隔(ミリ秒)の設定です                                                   | 300000                                                                                                                                                                                                                        |
| 飛行禁止エリアの検索期間(FROM)                                      | REST_DIPS_ADD_MINUTES_TO_NOW_SEARCH_PERIOD_FROM          | --rest.dips.add-minutes-to-now.search-period-from                         | 検索期間(FROM)※現在時刻に加算する時間(単位：分)の設定です                                   | -10                                                                                                                                                                                                                           |
| 飛行禁止エリアの検索期間(TO)                                        | REST_DIPS_ADD_MINUTES_TO_NOW_SEARCH_PERIOD_TO            | --rest.dips.add-minutes-to-now.search-period-to                           | 検索期間(TO)※現在時刻に加算する時間(単位：分)の設定です                                     | 20                                                                                                                                                                                                                            |
| 飛行禁止エリアの座標リスト                                          | REST_DIPS_COORDINATES_LIST                               | --rest.dips.coordinates-list[n]※nは0から始まる正数の連番                 | 取得地域を多角形の頂点座標で指定 頂点は[経度,緯度] 複数地域を指定可能です。地域の区切り文字は「;」です| 埼玉県秩父地域(四角形)と静岡県浜松市(四角形)を設定する場合 [[138.565063,36.206052],[139.270248,36.206052],[139.270248,35.822267],[138.565063,35.822267]];[[137.695531,35.305039],[138.050692,35.305039],[138.050692,34.640377],[137.695531,34.640377]] |
| SFTPサーバホスト                                                    | SFTP_SERVER_HOST                                         | --sftp.host                                                               | SFTPサーバのホストです                                                                       | ー                                                                                                                                                                                                                            |
| SFTPサーバポート                                                    | SFTP_SERVER_PORT                                         | --sftp.port                                                               | SFTPサーバのポートです                                                                       | 22                                                                                                                                                                                                                            |
| SFTPサーバユーザ名                                                  | SFTP_SERVER_USERNAME                                     | --sftp.username                                                           | SFTPサーバのユーザ名です                                                                     | ー                                                                                                                                                                                                                            |
| SFTPサーバパスワード                                                | SFTP_SERVER_PASSWORD                                     | --sftp.password                                                           | SFTPサーバのパスワードです                                                                   | ー                                                                                                                                                                                                                            |
| SFTPサーバディレクトリ(降水ナウキャスト)                            | SFTP_SERVER_DIRECTORY_NOWC                               | --sftp.root-directory.nowc                                                | SFTPサーバディレクトリ(降水ナウキャスト)です                                                 | /sftp/weather/nowc                                                                                                                                                                                                            |
| SFTPサーバディレクトリ(降水短時間予報)                              | SFTP_SERVER_DIRECTORY_SRF                                | --sftp.root-directory.srf                                                 | SFTPサーバディレクトリ(降水短時間予報)です                                                   | /sftp/weather/srf                                                                                                                                                                                                             |
| SFTPサーバディレクトリ(極地数値予報モデル)                          | SFTP_SERVER_DIRECTORY_LFM                                | --sftp.root-directory.lfm                                                 | SFTPサーバディレクトリ(極地数値予報モデル)です                                               | /sftp/weather/rjtd                                                                                                                                                                                                            |
| 気象業務支援センターの降水量実況取得の自動起動                      | ROUTE_JMBSC_PRECIPITATION_LIVE_AUTO_STARTUP              | --route.jmbsc.precipitation-live.auto-startup                             | 気象業務支援センターの降水量実況取得の自動起動です                                           | true                                                                                                                                                                                                                          |
| 気象業務支援センターの降水量実況の取得間隔                          | ROUTE_JMBSC_PRECIPITATION_LIVE_INTERVAL                  | --route.jmbsc.precipitation-live.interval                                 | 気象業務支援センターの降水量実況の取得間隔(ミリ秒)です                                       | 10000                                                                                                                                                                                                                         |
| 気象業務支援センターの降水量予報取得の自動起動                      | ROUTE_JMBSC_PRECIPITATION_FORECAST_AUTO_STARTUP          | --route.jmbsc.precipitation-forecast.auto-startup                         | 気象業務支援センターの降水量予報取得の自動起動です                                           | true                                                                                                                                                                                                                          |
| 気象業務支援センターの降水量予報の取得間隔                          | ROUTE_JMBSC_PRECIPITATION_FORECAST_INTERVAL              | --route.jmbsc.precipitation-forecast.interval                             | 気象業務支援センターの降水量予報の実行間隔(ミリ秒)です                                       | 10000                                                                                                                                                                                                                         |
| 気象業務支援センターの風速取得の自動起動                            | ROUTE_JMBSC_WIND_SPEED_AUTO_STARTUP                      | --route.jmbsc.wind-speed.auto-startup                                     | 気象業務支援センターの風速取得の自動起動です                                                 | true                                                                                                                                                                                                                          |
| 気象業務支援センターの風速の取得間隔                                | ROUTE_JMBSC_WIND_SPEED_INTERVAL                          | --route.jmbsc.wind-speed.interval                                         | 気象業務支援センターの風速の取得間隔(ミリ秒)です                                             | 10000                                                                                                                                                                                                                         |
| 気象情報の提供地域CSV                                               | PROVIDE_CONTENT_AREA_CSV                                 | --provide-content.nowc.area-csvs[n] --provide-content.srf.area-csvs[n] --provide-content.lfm.area-csvs[n] ※nは0から始まる正数の連番 | 取得地域をCSV形式で指定 CSV形式：最小緯度,最小経度,最大緯度,最大経度 複数地域を指定可能です。地域の区切り文字は「;」です | 埼玉県秩父地域、静岡県浜松市を設定する場合 35.822267,138.565063,36.206052,139.270248;34.640377,137.695531,35.305039,138.050692         |
