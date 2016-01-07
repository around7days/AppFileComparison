package common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * 日付操作の共通クラス<br>
 * @author 7days
 */
public class MyDateUtil {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MyDateUtil.class);

    /** 最大日付 */
    public static final String MAX_DATE = "2999/12/31 23:59:59";

    /** 日付フォーマット [HH:mm:ss] */
    public static final String HHMMSS = "HH:mm:ss";
    /** 日付フォーマット [yyyy/MM] */
    public static final String YYYYMM = "yyyy/MM";
    /** 日付フォーマット [yyyy/MM/dd] */
    public static final String YYYYMMDD = "yyyy/MM/dd";
    /** 日付フォーマット [yyyy/MM/dd HH:mm:ss] */
    public static final String YYYYMMDDHHMMSS = "yyyy/MM/dd HH:mm:ss";
    /** 日付フォーマット [yyyy/MM/dd HH:mm:ss] */
    public static final String YYYYMMDDHHMMSS_S = "yyyy/MM/dd HH:mm:ss.SSS";
    /** 日付フォーマット [HH:mm:ss] */
    public static final String HHMMSS_NOSLASH = "HH:mm:ss";
    /** 日付フォーマット [yyyyMM] */
    public static final String YYYYMM_NOSLASH = "yyyyMM";
    /** 日付フォーマット [yyyyMMdd] */
    public static final String YYYYMMDD_NOSLASH = "yyyyMMdd";
    /** 日付フォーマット [yyyyMMddHHmmss] */
    public static final String YYYYMMDDHHMMSS_NODELIMITER = "yyyyMMddHHmmss";
    /** 日付フォーマット [yyyyMMddHHmmssS] */
    public static final String YYYYMMDDHHMMSS_S_NODELIMITER = "yyyyMMddHHmmssSSS";

    /**
     * 現在日付を取得する。(固定)<br>
     * @return str 現在日付（yyyy/MM/dd HH:mm:ss）
     */
    public static String getStringSysDate() {
        String format = YYYYMMDDHHMMSS;

        return getStringSysDate(format);
    }

    /**
     * 現在日付を指定されたフォーマットで取得する。<br>
     * @param format 日付フォーマット
     * @return str 現在日付
     */
    public static String getStringSysDate(String format) {
        if (format == null) return "";

        // 日付を指定されたフォーマットで取得
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        String sysDate = simpleDateFormat.format(new Date());

        return sysDate;
    }

}
