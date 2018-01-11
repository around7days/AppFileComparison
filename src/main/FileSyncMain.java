package main;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.manager.MyPropertyManager;
import common.utils.MyArrayUtil;
import common.utils.MyFileUtil;
import common.utils.MyStringUtil;
import main.FileSyncConst.CompareKbn;

/**
 * ファイル同期化メインクラス
 * @author 7days
 */
public class FileSyncMain {

    /** Logger */
    private static final Logger logger = Logger.getLogger(FileSyncMain.class);

    /** PropertyManager */
    private static final MyPropertyManager prop = MyPropertyManager.INSTANCE;

    /** FileSystem */
    private static final FileSystem fs = FileSystems.getDefault();

    /**
     * 実行処理
     * @param args
     */
    public static void main(String[] args) {

        logger.info("★★★★★ 処理開始 ★★★★★");

        try {
            new FileSyncMain().execute();
        } catch (Exception e) {
            logger.error("system error", e);
        }

        logger.info("★★★★★ 処理終了 ★★★★★");
    }

    /**
     * メイン処理
     * @throws IOException
     */
    private void execute() throws IOException {

        // 初期設定
        procInit();

        // 比較処理
        DiffListBean diffListBean = procCompare();

        // 比較結果処理
        procExecute(diffListBean);
    }

    /**
     * 初期設定
     * @throws IOException
     */
    private void procInit() throws IOException {

        // 設定ファイルの読み込み
        prop.setProperty(FileSyncConst.CONF_PATH.toString());

        // ログレベルの設定
        logger.info("◆ログレベル設定");
        String logLevel = prop.getValue("logLevel");
        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.setLevel(Level.toLevel(logLevel));
        logger.info(logLevel);

        logger.info("◆設定一覧");
        prop.debug(Level.INFO);
    }

    /**
     * 比較
     * @throws IOException
     */
    private DiffListBean procCompare() throws IOException {

        /*
         * ファイル比較結果格納Beanの生成
         */
        DiffListBean diffListBean = new DiffListBean();

        /*
         * 設定ファイルから値を取得
         */
        Path compareDir1 = Paths.get(prop.getValue("compareDir1")); // 比較元フォルダ
        Path compareDir2 = Paths.get(prop.getValue("compareDir2")); // 比較先フォルダ

        String[] includePatterns = prop.getValue("includePatterns").split(";"); // 比較パターン
        String[] excludePatterns = prop.getValue("excludePatterns").split(";"); // 除外パターン
        String[] excludePatternsDefault = prop.getValue("excludePatternsDefault").split(";"); // 除外パターン（デフォルト）
        excludePatterns = MyArrayUtil.joinArray(excludePatterns, excludePatternsDefault); // 除外パターンの結合

        boolean lastModifiedTimeCheck = Boolean.valueOf(prop.getValue("lastModifiedTimeCheck")); // 最終更新日付チェック
        boolean hashCheck = Boolean.valueOf(prop.getValue("hashCheck")); // チェックサム

        // 保存
        diffListBean.setCompareDir1(compareDir1);
        diffListBean.setCompareDir2(compareDir2);

        /*
         * ファイル探索
         */
        // 比較対象PathMatcher・除外対象PathMatcherの生成
        logger.debug("▲inculdePattern");
        List<PathMatcher> includeMatcherList = createPathMatcher(FileSyncConst.SYNTAX_TYPE, includePatterns);
        logger.debug("▲excludePattern");
        List<PathMatcher> excludeMatcherList = createPathMatcher(FileSyncConst.SYNTAX_TYPE, excludePatterns);

        // ディレクトリ1、ディレクトリ2のファイルリストの取得（相対パス）
        logger.info("◆ファイル探索（比較元）");
        List<Path> fileList1 = fileSearch(compareDir1, includeMatcherList, excludeMatcherList);
        logger.info("◆ファイル探索（比較先）");
        List<Path> fileList2 = fileSearch(compareDir2, includeMatcherList, excludeMatcherList);

        /*
         * ファイル比較
         */
        logger.info("◆ファイル比較");

        // 新規・更新リストの取得
        fileList1.forEach(file1 -> {
            if (!fileList2.contains(file1)) {
                // 比較先に同名ファイルが存在しない場合
                // 新規リストに格納
                diffListBean.addList(CompareKbn.insert, file1);
                return;
            } else {
                // 比較先に同名ファイルが存在する場合

                Path filePath1 = compareDir1.resolve(file1);// 比較元
                Path filePath2 = compareDir2.resolve(file1);// 比較先

                // 更新日付の比較
                if (lastModifiedTimeCheck && !MyFileUtil.equalsLastModifiedTime(filePath1, filePath2)) {
                    // 異なる場合は更新リストに格納
                    diffListBean.addList(CompareKbn.update, file1);
                    return;
                }

                // チェックサムの比較
                if (hashCheck && !MyFileUtil.equalsChecksum(filePath1, filePath2)) {
                    // 異なる場合は更新リストに格納
                    diffListBean.addList(CompareKbn.update, file1);
                    return;
                }
            }
        });

        // 削除リストの取得
        fileList2.stream()
                 .filter(file2 -> !fileList1.contains(file2)) // 比較元に同名ファイルが存在しない
                 .forEach(file2 -> {
                     // 削除リストに格納
                     diffListBean.addList(CompareKbn.delete, file2);
                 });

        /*
         * 結果ログ出力
         */
        logger.info("◆比較結果");
        if (!diffListBean.getDiffList().isEmpty()) {
            logger.info("差分あり");
            diffListBean.getDiffList().forEach(logger::debug);
        } else {
            logger.info("差分なし");
        }

        return diffListBean;
    }

    /**
     * 比較結果処理
     * @param diffListBean ファイル比較結果格納Bean
     * @throws IOException
     */
    @SuppressWarnings("incomplete-switch")
    private void procExecute(DiffListBean diffListBean) throws IOException {

        /*
         * 差分有無判定
         */
        if (diffListBean.getDiffList().isEmpty()) {
            // 差分なしの場合は処理終了
            return;
        }

        /*
         * 設定ファイルから値を取得
         */
        boolean isOutputDiffList = Boolean.valueOf(prop.getValue("outputDiffList")); // 比較結果 差分リスト出力
        boolean isOutputDiffFile = Boolean.valueOf(prop.getValue("outputDiffFile")); // 比較結果 差分ファイル出力
        boolean syncDiffFile = Boolean.valueOf(prop.getValue("syncDiffFile")); // 比較結果 差分ファイル同期化

        /*
         * 差分リストの出力
         */
        if (isOutputDiffList) {
            logger.info("◆差分リストの出力先　：" + FileSyncConst.OUTPUT_LIST_PATH);

            // 出力用フォーマットの作成
            List<String> formatList = diffListBean.getDiffList().stream().map(diffBean -> {
                StringJoiner sj = new StringJoiner(FileSyncConst.SEPARATOR); // 区切り文字の指定
                sj.add(diffBean.getCompareKbn().value()); // 比較結果区分
                sj.add(diffBean.getFilePath().toString());// 相対パス
                sj.add(diffListBean.getCompareDir1().resolve(diffBean.getFilePath()).toString()); // 比較元絶対パス
                sj.add(diffListBean.getCompareDir2().resolve(diffBean.getFilePath()).toString()); // 比較先絶対パス
                return sj.toString();
            }).collect(Collectors.toList());

            // 出力
            MyFileUtil.fileOutput(formatList,
                                  FileSyncConst.OUTPUT_LIST_PATH,
                                  MyFileUtil.EncodeType.UTF8.getCharset(),
                                  MyFileUtil.LineFeedType.LF.getValue());
        }

        /*
         * 差分ファイルの出力
         */
        if (isOutputDiffFile) {
            logger.info("◆差分ファイルの出力先：" + FileSyncConst.OUTPUT_FILE_DIR);
            for (DiffBean diffBean : diffListBean.getDiffList()) {

                switch (diffBean.getCompareKbn()) {
                case insert:
                case update:
                    // 比較結果が登録・更新時のみファイル出力を実施
                    Path copyFrom = diffListBean.getCompareDir1().resolve(diffBean.getFilePath());
                    Path copyTo = FileSyncConst.OUTPUT_FILE_DIR.resolve(diffBean.getFilePath());
                    MyFileUtil.fileCopy(copyFrom, copyTo);
                    logger.debug("output : " + copyTo);
                    break;
                }
            }
        }

        /*
         * 差分ファイルの同期化
         */
        if (syncDiffFile) {
            logger.info("◆差分ファイルの同期先：" + diffListBean.getCompareDir2());
            for (DiffBean diffBean : diffListBean.getDiffList()) {
                switch (diffBean.getCompareKbn()) {
                case insert:
                case update:
                    // 比較結果が登録・更新時はファイルコピーを実施
                    Path copyFrom = diffListBean.getCompareDir1().resolve(diffBean.getFilePath());
                    Path copyTo = diffListBean.getCompareDir2().resolve(diffBean.getFilePath());
                    MyFileUtil.fileCopy(copyFrom, copyTo);
                    logger.debug("copy : " + copyFrom + " → " + copyTo);
                    break;
                case delete:
                    // 比較結果が削除時はファイル削除を実施
                    Path path = diffListBean.getCompareDir2().resolve(diffBean.getFilePath());
                    MyFileUtil.fileDelete(path);
                    logger.debug("delete : " + path);
                    break;
                }
            }
        }
    }

    /**
     * ファイル探索
     * @param searchPath 探索ディレクトリパス
     * @param includeMatcherList 比較対象パターンリスト
     * @param excludeMatcherList 除外対象パターンリスト
     * @return 結果リスト
     * @throws IOException
     */
    private List<Path> fileSearch(Path searchPath,
                                  List<PathMatcher> includeMatcherList,
                                  List<PathMatcher> excludeMatcherList) throws IOException {
        // 検索結果の格納List
        List<Path> resultList;

        // 検索処理
        logger.debug("▲fileWalk");
        try (Stream<Path> fileTreeStream = Files.walk(searchPath)) {
            resultList = fileTreeStream.filter(p -> p.toFile().isFile()) // ファイルのみ対象
                                       .filter(p -> matchPath(p, includeMatcherList, "includeMatch")) // 比較対象に含まれるか
                                       .filter(p -> !matchPath(p, excludeMatcherList, "excludeMatch")) // 除外対象に含まれないか
                                       .map(searchPath::relativize) // 相対パスに変換
                                       .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("file search error");
            throw e;
        }

        // 結果出力（デバッグ）
        logger.debug("▲matchList");
        resultList.forEach(logger::debug);

        return resultList;
    }

    /**
     * PathMatcherリストの生成
     * @param syntax
     * @param patterns
     * @return PathMatcherリスト
     */
    private List<PathMatcher> createPathMatcher(String syntax,
                                                String[] patterns) {
        return Stream.of(patterns)
                     .filter(MyStringUtil::isNotEmpty) // 空文字を除外
                     .map(p -> syntax + ":" + p.trim().replaceAll("\\\\", "/")) // 構文とパターンの生成(パスを/に変換)
                     .peek(logger::debug) // 構文とパターンのログ出力 // TODO ログが出ない・・・
                     .map(fs::getPathMatcher) // PathMatcherの生成
                     .collect(Collectors.toList()); // Listに格納
    }

    /**
     * 指定されたパスがパターンリストに一致するかチェック
     * @param p 比較対象パス
     * @param pathMatcherStream
     * @return 結果[true:一致 false:不一致]
     */
    private boolean matchPath(Path p,
                              List<PathMatcher> pathMatcherStream,
                              String debugMsg) {
        boolean b = pathMatcherStream.stream().anyMatch(m -> m.matches(p));
        if (b) logger.debug(debugMsg + " : " + p.toString());

        return b;
    }
}
