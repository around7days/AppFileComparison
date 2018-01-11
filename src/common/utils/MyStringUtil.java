package common.utils;

import org.apache.log4j.Logger;

/**
 * 文字列操作の共通クラス<br>
 * @author 7days
 */
public class MyStringUtil {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MyStringUtil.class);

    /**
     * 空白チェック（NULL含む）
     * @param str 文字列
     * @return 結果 [true:空白 false:空白以外]
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 空白チェック（NULL含む）
     * @param str 文字列
     * @return 結果 [true:空白以外 false:空白]
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
