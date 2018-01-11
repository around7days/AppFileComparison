package main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import main.FileSyncConst.CompareKbn;

/**
 * ファイル比較結果格納Bean
 * @author 7days
 */
public class DiffListBean {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DiffListBean.class);

    /** 比較元ディレクトリ */
    private Path compareDir1;
    /** 比較先ディレクトリ */
    private Path compareDir2;
    /** 比較結果格納リスト */
    private List<DiffBean> diffList = new ArrayList<>();

    public Path getCompareDir1() {
        return compareDir1;
    }

    public void setCompareDir1(Path compareDir1) {
        this.compareDir1 = compareDir1;
    }

    public Path getCompareDir2() {
        return compareDir2;
    }

    public void setCompareDir2(Path compareDir2) {
        this.compareDir2 = compareDir2;
    }

    public List<DiffBean> getDiffList() {
        return diffList;
    }

    /**
     * 比較結果の格納
     * @param diffType 比較結果タイプ
     * @param filePath ファイルパス
     */
    public void addList(CompareKbn diffType,
                        Path filePath) {
        DiffBean diffBean = new DiffBean();
        diffBean.setCompareKbn(diffType);
        diffBean.setFilePath(filePath);
        diffList.add(diffBean);
    }
}

/**
 * ファイル比較格納Bean
 * @author 7days
 */
class DiffBean {

    /** 比較結果タイプ */
    private CompareKbn compareKbn;

    /** ファイルパス（相対パス） */
    private Path filePath;

    public CompareKbn getCompareKbn() {
        return compareKbn;
    }

    public void setCompareKbn(CompareKbn compareKbn) {
        this.compareKbn = compareKbn;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        String val = this.compareKbn + " : " + this.filePath.toString();
        return val;
    }

}
