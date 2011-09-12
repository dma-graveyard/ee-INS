package dk.frv.enav.ins.common.text;

import org.apache.commons.lang.StringUtils;

public class TextUtils {

    public static String className(Class<?> cls) {
        String[] nameParts = StringUtils.split(cls.getName(), '.');
        return nameParts[nameParts.length - 1];
    }

    public static boolean exists(String str) {
        return (str != null && str.length() > 0);
    }
    
}
