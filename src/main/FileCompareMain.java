package main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import common.manager.MyPropertyManager;
import common.utils.MyFileUtil;
import common.utils.MyFileVisitor;

/**
 * ファイル比較メインクラス
 * @author 7days
 */
public class FileCompareMain {

    /** Logger */
    private static final Logger logger = Logger.getLogger(FileCompareMain.class);

    /** PropertyManager */
    private static final MyPropertyManager prop = MyPropertyManager.INSTANCE;

    /** 比較結果格納リスト */
    // 比較結果格納リスト(新規)
    private List<String> insertList = new ArrayList<String>();
    // 比較結果格納リスト(更新)
    private List<String> updateList = new ArrayList<String>();
    // 比較結果格納リスト(削除)
    private List<String> deleteList = new ArrayList<String>();

    /**
     * 実行処理
     * @param args
     */
    public static void main(String[] args) {

        logger.debug("--------FileCompare Start");

        try {
            new FileCompareMain().execute();
        } catch (Exception e) {
            logger.error("system error", e);
        }

        logger.debug("--------FileCompare End");
    }

    /**
     * メイン処理
     * @throws IOException
     */
    private void execute() throws IOException {

        // 設定ファイルの読み込み処理
        procReadProperty();

        // 比較処理
        procCompare();

        // 結果出力処理
        procOutput();
    }

    /**
     * 設定ファイルの読み込み
     */
    private void procReadProperty() {
        logger.debug("----procReadProperty");

        // 設定ファイルパスの生成
        Path confPath = Paths.get(FileCompareConst.CONF_DIR, FileCompareConst.CONF_NM);

        // 設定ファイルの読み込み
        prop.setProperty(confPath.toString());
    }

    /**
     * 比較
     * @throws IOException
     */
    private void procCompare() throws IOException {
        logger.debug("----procCompare");

        /*
         * 設定ファイルから値を取得
         */
        Path compareDir1 = Paths.get(prop.getValue("compareDir1")); // 比較元フォルダ
        Path compareDir2 = Paths.get(prop.getValue("compareDir2")); // 比較先フォルダ

        String syntaxType = prop.getValue("syntaxType"); // 構文タイプ
        String[] includePatterns = prop.getValue("includePatterns").split(";"); // 比較パターン
        String[] excludePatterns = prop.getValue("excludePatterns").split(";"); // 除外パターン

        boolean checkLastTime = Boolean.getBoolean(prop.getValue("checkLastModifiedTime")); // 最終更新日付チェック
        boolean checkSha256 = Boolean.valueOf(prop.getValue("checkSha256")); // チェックサム

        /*
         * ファイル探索
         */
        // ファイル探索クラスの生成
        MyFileVisitor fileVisitor = new MyFileVisitor();

        // ディレクトリ1、ディレクトリ2のファイルリストの取得（相対パス）
        List<String> fileList1 = fileVisitor.walk(compareDir1, syntaxType, includePatterns, excludePatterns);
        List<String> fileList2 = fileVisitor.walk(compareDir2, syntaxType, includePatterns, excludePatterns);

        // 新規・更新リストの取得
        for (String file1 : fileList1) {
            if (fileList2.contains(file1)) {
                // 比較先に同名ファイルが存在する

                Path filePath1 = compareDir1.resolve(file1);// 比較元
                Path filePath2 = compareDir2.resolve(file1);// 比較先

                // 更新日付の比較
                if (checkLastTime) {
                    if (!MyFileUtil.equalsLastModifiedTime(filePath1, filePath2)) {
                        // 異なる場合は更新リストに格納
                        updateList.add(file1);
                        continue;
                    }
                }

                // チェックサムの比較
                if (checkSha256) {
                    if (!MyFileUtil.equalsChecksum(filePath1, filePath2)) {
                        // 異なる場合は更新リストに格納
                        updateList.add(file1);
                        continue;
                    }
                }

            } else {
                // 比較先に同名ファイルが存在しない
                // 新規リストに格納
                insertList.add(file1);
            }
        }

        // 削除リストの取得
        for (String file2 : fileList2) {
            if (!fileList1.contains(file2)) {
                // 比較元に同名ファイルが存在しない
                // 削除リストに格納
                deleteList.add(file2);
            }
        }

    }

    /**
     * 結果出力
     */
    private void procOutput() {
        logger.debug("----procOutput");

        logger.debug("★新規");
        insertList.forEach(s -> logger.debug(s));
        logger.debug("★更新");
        updateList.forEach(s -> logger.debug(s));
        logger.debug("★削除");
        deleteList.forEach(s -> logger.debug(s));
    }
}
