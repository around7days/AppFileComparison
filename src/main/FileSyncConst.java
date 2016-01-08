package main;

import java.nio.file.Path;
import java.nio.file.Paths;

import common.utils.MyDateUtil;

/**
 * ファイル同期化定義クラス
 * @author 7days
 */
public class FileSyncConst {

    /** 実行ディレクトリ */
    private static final Path RUN_DIR = Paths.get(System.getProperty("user.dir"));

    /** confディレクトリ */
    private static final Path CONF_DIR = RUN_DIR.resolve("conf");
    /** confファイル名 */
    private static final String CONF_NM = "FileSync.properties";
    /** confパス */
    public static final Path CONF_PATH = CONF_DIR.resolve(CONF_NM);

    /** 出力先ディレクトリ */
    private static final Path OUTPUT_DIR = RUN_DIR.resolve("output")
                                                  .resolve(MyDateUtil.getStringSysDate(MyDateUtil.YYYYMMDDHHMMSS_NODELIMITER));
    /** 差分リストファイル名 */
    private static final String OUTPUT_LIST_NM = "diff.txt";
    /** 差分リストファイルパス */
    public static final Path OUTPUT_LIST_PATH = OUTPUT_DIR.resolve(OUTPUT_LIST_NM);
    /** 差分ファイル出力先ディレクトリ */
    public static final Path OUTPUT_FILE_DIR = OUTPUT_DIR.resolve("diff");

    /** 差分リストフォーマット区切り文字 */
    public static final String SEPARATOR = "\t";

    /** 構文タイプ */
    public static final String SYNTAX_TYPE = "glob";

    /**
     * 比較結果タイプ
     */
    //@formatter:off
    public enum CompareKbn {
        none(" "), // 差なし
        insert("I"), // 登録
        update("U"), // 更新
        delete("D"); // 削除

        String value;
        private CompareKbn(String value) { this.value = value; }
        public String value() { return value; }
    }
    //@formatter:on

}
