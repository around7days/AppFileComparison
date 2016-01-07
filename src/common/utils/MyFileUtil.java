package common.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

/**
 * ファイル操作の共通クラス<br>
 * @author 7days
 */
public class MyFileUtil {

    /** Logger */
    private static final Logger logger = Logger.getLogger(MyFileUtil.class);

    /**
     * 文字コードタイプ
     */
    //@formatter:off
    public enum EncodeType {
        DEFAULT(System.getProperty("file.encoding")),
        MS932("MS932"),
        SJIS("Shift_JIS"),
        UTF8("UTF-8"),
        EUC("EUC-JP"),
        ;

        private Charset cs;
        EncodeType(String name) { this.cs = Charset.forName(name);}
        public Charset getCharset() { return cs; }
    }
    //@formatter:on

    /**
     * 改行コードタイプ
     */
    //@formatter:off
    public enum LineFeedType {
        /** デフォルト */
        DEFAULT(System.lineSeparator()),
        /** \n */
        LF("\n"),
        /** \r\n */
        CRLF("\r\n"),
        ;

        private String name;
        LineFeedType(String name) { this.name = name; }
        public String getValue() { return name; }
    }
    //@formatter:on

    /**
     * ファイルの出力処理
     * @param list 出力内容
     * @param outputPath 出力先パス
     * @param appendFlg 出力設定[true:追記 false:上書]
     * @param encode 文字コード
     * @param lineFeed 改行コード
     * @throws IOException
     */
    public static void fileOutput(List<?> list,
                                  Path outputPath,
                                  boolean appendFlg,
                                  Charset encode,
                                  String lineFeed) throws IOException {
        // 出力先フォルダの生成
        outputPath.toFile().getParentFile().mkdirs();

        // 出力オプションの設定
        List<OpenOption> options = new ArrayList<OpenOption>();
        options.add(StandardOpenOption.CREATE);
        options.add(StandardOpenOption.WRITE);
        if (appendFlg) {
            // 追記
            options.add(StandardOpenOption.APPEND);
        } else {
            // 上書き
            options.add(StandardOpenOption.TRUNCATE_EXISTING);
        }

        // 出力
        try (BufferedWriter bw = Files.newBufferedWriter(outputPath,
                                                         encode,
                                                         options.toArray(new OpenOption[options.size()]))) {
            for (Object line : list) {
                bw.write(line.toString());
                bw.write(lineFeed);
            }
        } catch (IOException e) {
            logger.warn("file output error", e);
            throw e;
        }
    }

    /**
     * ファイルのコピー処理
     * @param copyFrom コピー元ファイルパス
     * @param copyTo コピー先ファイルパス
     * @throws IOException
     */
    public static void fileCopy(Path copyFrom,
                                Path copyTo) throws IOException {

        // 出力先フォルダの生成
        copyTo.toFile().getParentFile().mkdirs();

        try {
            // ファイルコピー（上書き、属性コピー）
            Files.copy(copyFrom, copyTo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            logger.warn("file copy error", e);
            throw e;
        }
    }

    /**
     * 引数で指定されたファイルの更新日付を比較
     * @param filePath1
     * @param filePath2
     * @return 結果[true:一致 false:不一致]
     */
    public static boolean equalsLastModifiedTime(Path filePath1,
                                                 Path filePath2) {

        try {
            FileTime fileTime1 = Files.getLastModifiedTime(filePath1);
            FileTime fileTime2 = Files.getLastModifiedTime(filePath2);

            return fileTime1.equals(fileTime2);
        } catch (IOException e) {
            logger.warn("warning", e);
            return false;
        }
    }

    /**
     * 引数で指定されたファイルのチェックサムを比較
     * @param filePath1
     * @param filePath2
     * @return 結果[true:一致 false:不一致]
     */
    public static boolean equalsChecksum(Path filePath1,
                                         Path filePath2) {

        try (InputStream is1 = Files.newInputStream(filePath1, StandardOpenOption.READ);
             InputStream is2 = Files.newInputStream(filePath2, StandardOpenOption.READ)) {

            String checksum1 = DigestUtils.sha256Hex(is1);
            String checksum2 = DigestUtils.sha256Hex(is2);

            return checksum1.equals(checksum2);
        } catch (IOException e) {
            logger.warn("warning", e);
            return false;
        }
    }

}
