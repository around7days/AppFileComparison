package main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import main.FileSyncConst.CompareKbn;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.manager.MyPropertyManager;
import common.utils.MyArrayUtil;
import common.utils.MyFileSearch;
import common.utils.MyFileUtil;

/**
 * ファイル同期化メインクラス
 * @author 7days
 */
public class FileSyncMain {

    /** Logger */
    private static final Logger logger = Logger.getLogger(FileSyncMain.class);

    /** PropertyManager */
    private static final MyPropertyManager prop = MyPropertyManager.INSTANCE;

    /** 比較結果格納Bean */
    private DiffListBean diffListBean = new DiffListBean();

    /**
     * 実行処理
     * @param args
     */
    public static void main(String[] args) {

        logger.info("★★★★★ 処理開始 ★★★★★");

        try {
            new FileSyncMain().execute();
        } catch (Exception e) {
            logger.error("system error", e);
        }

        logger.info("★★★★★ 処理終了 ★★★★★");
    }

    /**
     * メイン処理
     * @throws IOException
     */
    private void execute() throws IOException {

        // 初期設定
        procInit();

        // 比較処理
        procCompare();

        // 比較結果処理
        procExecute();
    }

    /**
     * 初期設定
     * @throws IOException
     */
    private void procInit() throws IOException {

        // 設定ファイルの読み込み
        prop.setProperty(FileSyncConst.CONF_PATH.toString());

        // ログレベルの設定
        String logLevel = prop.getValue("logLevel");
        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.setLevel(Level.toLevel(logLevel));

        logger.info("◆設定一覧");
        prop.debug(Level.INFO);
    }

    /**
     * 比較
     * @throws IOException
     */
    private void procCompare() throws IOException {

        /*
         * 設定ファイルから値を取得
         */
        Path compareDir1 = Paths.get(prop.getValue("compareDir1")); // 比較元フォルダ
        Path compareDir2 = Paths.get(prop.getValue("compareDir2")); // 比較先フォルダ

        String[] includePatterns = prop.getValue("includePatterns").split(";"); // 比較パターン
        String[] excludePatterns = prop.getValue("excludePatterns").split(";"); // 除外パターン
        String[] excludePatternsDefault = prop.getValue("excludePatternsDefault").split(";"); // 除外パターン（デフォルト）
        excludePatterns = MyArrayUtil.joinArray(excludePatterns, excludePatternsDefault); // 除外パターンの結合

        boolean lastModifiedTimeCheck = Boolean.valueOf(prop.getValue("lastModifiedTimeCheck")); // 最終更新日付チェック
        boolean hashCheck = Boolean.valueOf(prop.getValue("hashCheck")); // チェックサム

        // 保存
        diffListBean.setCompareDir1(compareDir1.toString());
        diffListBean.setCompareDir2(compareDir2.toString());

        /*
         * ファイル探索
         */
        // ファイル探索クラスの生成
        MyFileSearch fileSearch = new MyFileSearch();

        // ディレクトリ1、ディレクトリ2のファイルリストの取得（相対パス）
        logger.info("◆ファイル探索（比較元）");
        List<String> fileList1 = fileSearch.search(compareDir1,
                                                   FileSyncConst.SYNTAX_TYPE,
                                                   includePatterns,
                                                   excludePatterns);
        logger.info("◆ファイル探索（比較先）");
        List<String> fileList2 = fileSearch.search(compareDir2,
                                                   FileSyncConst.SYNTAX_TYPE,
                                                   includePatterns,
                                                   excludePatterns);

        /*
         * ファイル比較
         */
        logger.info("◆ファイル比較");
        // 新規・更新リストの取得
        for (String file1 : fileList1) {
            if (!fileList2.contains(file1)) {
                // 比較先に同名ファイルが存在しない場合
                // 新規リストに格納
                diffListBean.addList(CompareKbn.insert, file1);
                continue;
            } else {
                // 比較先に同名ファイルが存在する場合

                Path filePath1 = compareDir1.resolve(file1);// 比較元
                Path filePath2 = compareDir2.resolve(file1);// 比較先

                // 更新日付の比較
                if (lastModifiedTimeCheck && !MyFileUtil.equalsLastModifiedTime(filePath1, filePath2)) {
                    // 異なる場合は更新リストに格納
                    diffListBean.addList(CompareKbn.update, file1);
                    continue;
                }

                // チェックサムの比較
                if (hashCheck && !MyFileUtil.equalsChecksum(filePath1, filePath2)) {
                    // 異なる場合は更新リストに格納
                    diffListBean.addList(CompareKbn.update, file1);
                    continue;
                }
            }
        }

        // 削除リストの取得
        for (String file2 : fileList2) {
            if (!fileList1.contains(file2)) {
                // 比較元に同名ファイルが存在しない場合
                // 削除リストに格納
                diffListBean.addList(CompareKbn.delete, file2);
            }
        }

        /*
         * 結果ログ出力
         */
        logger.info("◆比較結果");
        if (diffListBean.getDiffList().isEmpty()) {
            logger.info("差分なし");
        } else {
            diffListBean.debug(Level.INFO);
        }
    }

    /**
     * 比較結果処理
     * @throws IOException
     */
    @SuppressWarnings("incomplete-switch")
    private void procExecute() throws IOException {

        /*
         * 差分有無判定
         */
        if (diffListBean.getDiffList().isEmpty()) {
            // 差分なしの場合は処理終了
            return;
        }

        /*
         * 設定ファイルから値を取得
         */
        boolean isOutputDiffList = Boolean.valueOf(prop.getValue("outputDiffList")); // 比較結果 差分リスト出力
        boolean isOutputDiffFile = Boolean.valueOf(prop.getValue("outputDiffFile")); // 比較結果 差分ファイル出力
        boolean syncDiffFile = Boolean.valueOf(prop.getValue("syncDiffFile")); // 比較結果 差分ファイル同期化

        /*
         * 差分リストの出力
         */
        if (isOutputDiffList) {
            logger.info("◆差分リストの出力先　：" + FileSyncConst.OUTPUT_LIST_PATH);

            // 出力用フォーマットの作成
            List<String> formatList = new ArrayList<>();
            for (DiffBean diffBean : diffListBean.getDiffList()) {
                StringJoiner sj = new StringJoiner(FileSyncConst.SEPARATOR); // 区切り文字の指定
                sj.add(diffBean.getCompareKbn().value()); // 比較結果区分
                sj.add(diffBean.getFilePath());// 相対パス
                sj.add(Paths.get(diffListBean.getCompareDir1(), diffBean.getFilePath()).toString()); // 比較元絶対パス
                sj.add(Paths.get(diffListBean.getCompareDir2(), diffBean.getFilePath()).toString()); // 比較先絶対パス
                formatList.add(sj.toString());
            }

            // 出力
            MyFileUtil.fileOutput(formatList,
                                  FileSyncConst.OUTPUT_LIST_PATH,
                                  MyFileUtil.EncodeType.UTF8.getCharset(),
                                  MyFileUtil.LineFeedType.LF.getValue());
        }

        /*
         * 差分ファイルの出力
         */
        if (isOutputDiffFile) {
            logger.info("◆差分ファイルの出力先：" + FileSyncConst.OUTPUT_FILE_DIR);
            for (DiffBean diffBean : diffListBean.getDiffList()) {
                switch (diffBean.getCompareKbn()) {
                case insert:
                case update:
                    // 比較結果が登録・更新時のみファイル出力を実施
                    Path copyFrom = Paths.get(diffListBean.getCompareDir1(), diffBean.getFilePath());
                    Path copyTo = FileSyncConst.OUTPUT_FILE_DIR.resolve(diffBean.getFilePath());
                    MyFileUtil.fileCopy(copyFrom, copyTo);
                    logger.debug("output:" + copyTo);
                    break;
                }
            }
        }

        /*
         * 差分ファイルの同期化
         */
        if (syncDiffFile) {
            logger.info("◆差分ファイルの同期先：" + diffListBean.getCompareDir2());
            for (DiffBean diffBean : diffListBean.getDiffList()) {
                switch (diffBean.getCompareKbn()) {
                case insert:
                case update:
                    // 比較結果が登録・更新時はファイルコピーを実施
                    Path copyFrom = Paths.get(diffListBean.getCompareDir1(), diffBean.getFilePath());
                    Path copyTo = Paths.get(diffListBean.getCompareDir2(), diffBean.getFilePath());
                    MyFileUtil.fileCopy(copyFrom, copyTo);
                    logger.debug("copy:" + copyFrom + " → " + copyTo);
                    break;
                case delete:
                    // 比較結果が削除時はファイル削除を実施
                    Path path = Paths.get(diffListBean.getCompareDir2(), diffBean.getFilePath());
                    MyFileUtil.fileDelete(path);
                    logger.debug("delete:" + path);
                    break;
                }
            }
        }

    }
}
