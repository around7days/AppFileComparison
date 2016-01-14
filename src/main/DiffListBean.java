package main;

import java.util.ArrayList;
import java.util.List;

import main.FileSyncConst.CompareKbn;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * ファイル比較結果格納Bean
 * @author 7days
 */
public class DiffListBean {

    /** Logger */
    private static final Logger logger = Logger.getLogger(DiffListBean.class);

    /** 比較元ディレクトリ */
    private String compareDir1;
    /** 比較先ディレクトリ */
    private String compareDir2;

    /** 比較結果格納リスト */
    private List<DiffBean> diffList = new ArrayList<>();

    /**
     * 比較元ディレクトリを取得します。
     * @return 比較元ディレクトリ
     */
    public String getCompareDir1() {
        return compareDir1;
    }

    /**
     * 比較元ディレクトリを設定します。
     * @param compareDir1 比較元ディレクトリ
     */
    public void setCompareDir1(String compareDir1) {
        this.compareDir1 = compareDir1;
    }

    /**
     * 比較先ディレクトリを取得します。
     * @return 比較先ディレクトリ
     */
    public String getCompareDir2() {
        return compareDir2;
    }

    /**
     * 比較先ディレクトリを設定します。
     * @param compareDir2 比較先ディレクトリ
     */
    public void setCompareDir2(String compareDir2) {
        this.compareDir2 = compareDir2;
    }

    /**
     * 比較結果の格納
     * @param diffType 比較結果タイプ
     * @param filePath ファイルパス
     */
    public void addList(CompareKbn diffType,
                        String filePath) {
        DiffBean diffBean = new DiffBean();
        diffBean.setCompareKbn(diffType);
        diffBean.setFilePath(filePath);
        diffList.add(diffBean);
    }

    /**
     * 比較結果格納リストを取得します。
     * @return 比較結果格納リスト
     */
    public List<DiffBean> getDiffList() {
        return diffList;
    }

    /**
     * デバッグログ出力
     * @param level
     */
    public void debug(Level level) {
        diffList.forEach(diffBean -> {
            String val = diffBean.getCompareKbn().value() + ":" + diffBean.getFilePath();
            logger.log(level, val);
        });
    }
}

class DiffBean {

    /** 比較結果タイプ */
    private CompareKbn compareKbn;

    /** ファイルパス（相対パス） */
    private String filePath;

    /**
     * 比較結果タイプを取得します。
     * @return 比較結果タイプ
     */
    public CompareKbn getCompareKbn() {
        return compareKbn;
    }

    /**
     * 比較結果タイプを設定します。
     * @param compareKbn 比較結果タイプ
     */
    public void setCompareKbn(CompareKbn compareKbn) {
        this.compareKbn = compareKbn;
    }

    /**
     * ファイルパス（相対パス）を取得します。
     * @return ファイルパス（相対パス）
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * ファイルパス（相対パス）を設定します。
     * @param filePath ファイルパス（相対パス）
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
