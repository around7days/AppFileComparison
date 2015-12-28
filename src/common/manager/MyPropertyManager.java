package common.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * PropertyManagerクラス<br>
 * （シングルトン）
 */
public enum MyPropertyManager {
    INSTANCE;

    /** Logger */
    private static final Logger logger = Logger.getLogger(MyPropertyManager.class);

    /** プロパティ */
    private static Properties prop = null;

    /**
     * プロパティを設定します。
     * @param propertyFilePath プロパティファイル
     */
    synchronized public void setProperty(String propertyFilePath) {
        if (prop != null) return;

        try (FileInputStream fis = new FileInputStream(propertyFilePath)) {
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
            // エラー自体は握り潰す
            logger.error("property load error", e);
        }
    }

    /**
     * プロパティから値を取得
     * @param key
     * @return keyに対応する値
     */
    public String getValue(String key) {
        return prop.getProperty(key);
    }
}
// package my.manager;
//
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.Properties;
//
// import org.apache.log4j.Logger;
//
// /**
// * PropertyManagerクラス
// */
// public class MyPropertyManager {
//
// /** Logger */
// private static final Logger logger = Logger.getLogger(MyPropertyManager.class);
//
// /** 唯一のインスタンス */
// private static final MyPropertyManager instance = new MyPropertyManager();
// /** プロパティ */
// private static Properties prop = null;
//
// /**
// * コンストラクタ
// */
// private MyPropertyManager() {
// }
//
// /**
// * 唯一のインスタンスを取得します。
// * @return 唯一のインスタンス
// */
// public static MyPropertyManager getInstance() {
// return instance;
// }
//
// /**
// * プロパティを設定します。
// * @param propertyFilePath プロパティファイル
// */
// synchronized public void setProperty(String propertyFilePath) {
// if (prop != null) return;
//
// try (FileInputStream fis = new FileInputStream(propertyFilePath)) {
// prop = new Properties();
// prop.load(fis);
// } catch (IOException e) {
// // エラー自体は握り潰す
// logger.error("property load error", e);
// }
// }
//
// /**
// * プロパティから値を取得
// * @param key
// * @return keyに対応する値
// */
// public String getValue(String key) {
// return prop.getProperty(key);
// }
// }
