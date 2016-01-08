package common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 配列操作の共通クラス<br>
 * @author 7days
 */
public class MyArrayUtil {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MyArrayUtil.class);

    /**
     * 配列の結合
     * @param array1
     * @param array2
     * @return 結合配列
     */
    public static String[] joinArray(String[] array1,
                                     String[] array2) {
        List<String> newList = new ArrayList<>();

        newList.addAll(Arrays.asList(array1));
        newList.addAll(Arrays.asList(array2));

        return newList.toArray(new String[0]);
    }
}
