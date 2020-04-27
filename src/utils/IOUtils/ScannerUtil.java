package utils.IOUtils;

import java.util.*;

public class ScannerUtil {

    private static Scanner scan = null;

    public static Scanner getInstance() {
        if (scan == null)
            scan = new Scanner(System.in);
        return scan;
    }

}
