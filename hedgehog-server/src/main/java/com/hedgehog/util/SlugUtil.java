package com.hedgehog.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.regex.Pattern;

/**
 * Slug 生成工具类，将中文标题转换为 URL 友好的拼音标识。
 *
 * <p>转换规则：
 * <ul>
 *   <li>中文字符 → 无声调拼音</li>
 *   <li>英文字母/数字 → 转小写保留</li>
 *   <li>空格、横线、下划线 → 替换为单个 "-"</li>
 *   <li>其他字符 → 移除</li>
 *   <li>连续横线 → 合并为单个</li>
 *   <li>首尾横线 → 去除</li>
 * </ul>
 */
public class SlugUtil {

    /** 匹配非小写字母、数字、横线的字符 */
    private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-z0-9-]");
    /** 匹配连续横线 */
    private static final Pattern DASHES = Pattern.compile("-+");

    /**
     * 将输入文本转换为 slug。
     *
     * @param input 原始文本（如中文标题）
     * @return 拼音 slug，输入为空时返回空字符串
     */
    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : input.trim().toCharArray()) {
            // 中文字符范围
            if (c >= 0x4e00 && c <= 0x9fff) {
                try {
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyin != null && pinyin.length > 0) {
                        sb.append(pinyin[0]);
                    }
                } catch (Exception e) {
                    // 极少数生僻字无拼音映射，忽略异常保留原字符
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
