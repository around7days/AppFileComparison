package common.utils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

/**
 * ファイル探索クラス<br>
 * @author 7days
 */
public class MyFileSearch {

    /** Logger */
    private static final Logger logger = Logger.getLogger(MyFileSearch.class);

    /** FileSystem */
    private static final FileSystem fs = FileSystems.getDefault();

    /**
     * ファイル探索
     * @param searchPath 探索ディレクトリパス
     * @param syntax 構文タイプ(regex or glob)
     * @param includePatterns 比較対象パターン
     * @param excludePatterns 除外対象パターン
     * @return 結果リスト
     * @throws IOException
     */
    public List<String> search(Path searchPath,
                               String syntax,
                               String[] includePatterns,
                               String[] excludePatterns) throws IOException {
        // 検索結果の格納List
        List<String> resultList = new ArrayList<String>();

        // 比較対象PathMatcherの生成
        logger.debug("▲inculdePattern");
        List<PathMatcher> includePathMatcherList = createPathMatcher(searchPath, syntax, includePatterns);

        // 除外対象PathMatcherの生成
        logger.debug("▲excludePattern");
        List<PathMatcher> excludePathMatcherList = createPathMatcher(searchPath, syntax, excludePatterns);

        // 検索処理
        try (Stream<Path> stream = Files.walk(searchPath)) {
            //@formatter:off
            stream.filter(p -> p.toFile().isFile())        // ファイルのみ対象
                  .filter(p -> includeMatchPath(p, includePathMatcherList))  // 比較対象に含まれるか
                  .filter(p -> !excludeMatchPath(p, excludePathMatcherList)) // 除外対象に含まれないか
                  .forEach(p -> {
                      // 相対パスを格納
                      resultList.add(searchPath.relativize(p).toString());
                  });
            //@formatter:on
        } catch (IOException e) {
            logger.error("file search error");
            throw e;
        }

        // 結果出力（デバッグ）
        logger.debug("▲matchList");
        resultList.forEach(s -> logger.debug(s));

        return resultList;
    }

    /**
     * PathMatcherリストの生成
     * @param searchPath
     * @param syntax
     * @param patterns
     * @return PathMatcherリスト
     */
    private List<PathMatcher> createPathMatcher(Path searchPath,
                                                String syntax,
                                                String[] patterns) {
        List<PathMatcher> includePathMatcherList = new ArrayList<>();

        for (String pattern : patterns) {
            // 構文とパターンの生成(パスを/に変換)
            String syntaxAndPattern = syntax + ":" + (searchPath + "/" + pattern.trim()).replaceAll("\\\\", "/");
            // PathMatcherの生成
            PathMatcher matcher = fs.getPathMatcher(syntaxAndPattern);
            // リストに格納
            includePathMatcherList.add(matcher);
            logger.debug(syntaxAndPattern);
        }

        return includePathMatcherList;
    }

    /**
     * 指定されたパスがパターンリストに一致するかチェック
     * @param p 比較対象パス
     * @param pathMatcherList
     * @return 結果[true:一致 false:不一致]
     */
    private boolean includeMatchPath(Path p,
                                     List<PathMatcher> pathMatcherList) {
        boolean isMatch = matchPath(p, pathMatcherList);
        if (isMatch) logger.debug("includeMatch : " + p.toString());

        return isMatch;
    }

    /**
     * 指定されたパスがパターンリストに一致するかチェック
     * @param p 比較対象パス
     * @param pathMatcherList
     * @return 結果[true:一致 false:不一致]
     */
    private boolean excludeMatchPath(Path p,
                                     List<PathMatcher> pathMatcherList) {
        boolean isMatch = matchPath(p, pathMatcherList);
        if (isMatch) logger.debug("excludeMatch : " + p.toString());

        return isMatch;
    }

    /**
     * 指定されたパスがパターンリストに一致するかチェック
     * @param p 比較対象パス
     * @param pathMatcherList
     * @return 結果[true:一致 false:不一致]
     */
    private boolean matchPath(Path p,
                              List<PathMatcher> pathMatcherList) {
        boolean b = false;
        for (PathMatcher matcher : pathMatcherList) {
            if (matcher.matches(p)) {
                b = true;// 一致
                break;
            }
        }
        return b;
    }

}
