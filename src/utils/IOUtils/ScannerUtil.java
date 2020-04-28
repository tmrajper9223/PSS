package utils.IOUtils;

import java.util.*;

/**
 * Singleton Instance of the Scanner class
 */
public class ScannerUtil {

    private static Scanner scan = null;

    public static Scanner getInstance() {
        if (scan == null)
            scan = new Scanner(System.in);
        return scan;
    }

}
