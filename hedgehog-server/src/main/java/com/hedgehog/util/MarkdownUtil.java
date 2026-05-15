package com.hedgehog.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.Arrays;

/**
 * Markdown 转 HTML 工具类，基于 flexmark 实现。
 *
 * <p>在类加载时初始化 Parser 和 Renderer 单例，
 * 启用表格、目录生成、emoji 三个扩展。
 * 文章保存时调用 {@link #render(String)} 将 content_md 转换为 content_html。
 */
public class MarkdownUtil {

    /** flexmark Markdown 解析器，线程安全 */
    private static final Parser PARSER;
    /** flexmark HTML 渲染器，线程安全 */
    private static final HtmlRenderer RENDERER;

    static {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                TocExtension.create(),
                EmojiExtension.create()
        ));
        PARSER = Parser.builder(options).build();
        RENDERER = HtmlRenderer.builder(options).build();
    }

    /**
     * 将 Markdown 文本渲染为 HTML。
     *
     * @param markdown Markdown 原始文本
     * @return 渲染后的 HTML，输入为空时返回空字符串
     */
    public static String render(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        return RENDERER.render(PARSER.parse(markdown));
    }
}
