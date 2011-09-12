package dk.frv.enav.ins.common.util;

public class SysUtils {

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return (os != null && os.startsWith("Windows"));
    }
    
}
