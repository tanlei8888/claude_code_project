package com.hedgehog.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-z0-9-]");
    private static final Pattern DASHES = Pattern.compile("-+");

    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : input.trim().toCharArray()) {
            if (c >= 0x4e00 && c <= 0x9fff) {
                try {
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyin != null && pinyin.length > 0) {
                        sb.append(pinyin[0]);
                    }
                } catch (Exception ignored) {
                }
            } else if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (c == ' ' || c == '-' || c == '_') {
                sb.append('-');
            }
        }
        String result = sb.toString().toLowerCase().replaceAll("\\s+", "-");
        result = NON_ALPHANUM.matcher(result).replaceAll("");
        result = DASHES.matcher(result).replaceAll("-");
        if (result.startsWith("-")) result = result.substring(1);
        if (result.endsWith("-")) result = result.substring(0, result.length() - 1);
        return result;
    }
}
