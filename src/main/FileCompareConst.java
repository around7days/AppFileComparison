package main;

import java.nio.file.Paths;

/**
 * ファイル比較定義クラス
 * @author 7days
 */
public class FileCompareConst {

    /** 実行ディレクトリパス */
    private static final String RUN_DIR = System.getProperty("user.dir");
    /** confディレクトリパス */
    public static final String CONF_DIR = Paths.get(RUN_DIR, "conf").toString();
    /** confファイル名 */
    public static final String CONF_NM = "FileCompare.properties";
}
