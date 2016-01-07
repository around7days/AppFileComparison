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
     * @throws IOException
     */
    synchronized public void setProperty(String propertyFilePath) throws IOException {
        if (prop != null) return;

        try (FileInputStream fis = new FileInputStream(propertyFilePath)) {
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
            logger.error("property load error");
            throw e;
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        prop.stringPropertyNames().forEach(key -> {
            sb.append(String.format("%-25s", key))
              .append(" : ")
              .append(prop.getProperty(key))
              .append(System.lineSeparator());
        });
        return sb.toString();
    }

}
