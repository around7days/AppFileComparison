package main;

/**
 * 設定ファイル情報格納クラス
 * @author 7days
 */
public class FileCompareBean {

    /** 比較対象ディレクトリ1 */
    private String compDir1;

    /** 比較対象ディレクトリ2 */
    private String compDir2;

    /**
     * 比較対象ディレクトリ1を取得します。
     * @return 比較対象ディレクトリ1
     */
    public String getCompDir1() {
        return compDir1;
    }

    /**
     * 比較対象ディレクトリ1を設定します。
     * @param compDir1 比較対象ディレクトリ1
     */
    public void setCompDir1(String compDir1) {
        this.compDir1 = compDir1;
    }

    /**
     * 比較対象ディレクトリ2を取得します。
     * @return 比較対象ディレクトリ2
     */
    public String getCompDir2() {
        return compDir2;
    }

    /**
     * 比較対象ディレクトリ2を設定します。
     * @param compDir2 比較対象ディレクトリ2
     */
    public void setCompDir2(String compDir2) {
        this.compDir2 = compDir2;
    }

}
