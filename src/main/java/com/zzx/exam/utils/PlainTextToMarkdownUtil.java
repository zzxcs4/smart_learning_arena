package com.zzx.exam.utils;

public class PlainTextToMarkdownUtil {

    /**
     * 核心方法：将普通文本转为Markdown文本
     * @param plainText 无Markdown格式的普通文本（如你提供的lessonPlanTemplate）
     * @return 原始Markdown字符串（前端可直接用marked渲染）
     */
    public static String convertToMarkdown(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return "";
        }

        // 步骤1：预处理文本（统一换行符、去除首尾空白、替换多余空格）
        String processedText = preprocessText(plainText);

        // 步骤2：按行分割文本（每行作为处理单位）
        String[] lines = processedText.split("\n");

        // 步骤3：逐行解析，添加Markdown语法
        StringBuilder markdownBuilder = new StringBuilder();
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                // 空行保留为Markdown的空行（用2个\n，避免连续空行）
                if (markdownBuilder.length() > 0 && !markdownBuilder.toString().endsWith("\n\n")) {
                    markdownBuilder.append("\n\n");
                }
                continue;
            }

            // 按语义规则匹配，添加对应的Markdown标记
            String markdownLine = mapToMarkdownLine(trimmedLine);
            markdownBuilder.append(markdownLine).append("\n");
        }

        // 步骤4：最终处理（去除末尾多余换行，优化格式）
        String finalMarkdown = markdownBuilder.toString().trim();
        // 确保标题后有正确的换行（避免标题与内容粘连）
        finalMarkdown = finalMarkdown.replaceAll("(#+ .+?)\n(-|###)", "$1\n\n$2");

        return finalMarkdown;
    }

    /**
     * 文本预处理：统一换行符、去除多余空格
     */
    private static String preprocessText(String plainText) {
        // 1. 统一换行符（Windows \r\n → Linux \n）
        String unifiedNewline = plainText.replaceAll("\r\n", "\n");
        // 2. 去除每行首尾的多余空格（保留文本内的正常空格）
        String[] lines = unifiedNewline.split("\n");
        StringBuilder processed = new StringBuilder();
        for (String line : lines) {
            processed.append(line.trim()).append("\n");
        }
        // 3. 去除文本首尾的空白字符（避免开头/结尾空行）
        return processed.toString().trim();
    }

    /**
     * 核心映射：将单行普通文本转为Markdown格式行
     */
    private static String mapToMarkdownLine(String line) {
        // 规则1：匹配顶级标题（如“教案模板（一）：”）→ 一级标题 #
        if (line.startsWith("教案模板") && line.endsWith("：")) {
            return "# " + line.replace("：", ""); // 去除末尾冒号，添加一级标题标记
        }

        // 规则2：匹配二级模块（如“（一）教学目标：”“（二）教学重难点：”）→ 二级标题 ##
        if (line.matches("^（[一二三四五六七八九十]）.+：$")) { // 正则匹配“（X）XXX：”格式
            return "## " + line.replace("：", ""); // 去除冒号，添加二级标题标记
        }

        // 规则3：匹配三级模块（如“1、导入新课”“2、讲授新课”）→ 三级标题 ###
        if (line.matches("^\\d+、.+$")) { // 正则匹配“数字、XXX”格式
            return "### " + line; // 添加三级标题标记
        }

        // 规则4：匹配子项（如“（1）温故而知新...”“（2）设计新颖活泼...”）→ 无序列表 -
        if (line.matches("^（\\d+）.+$")) { // 正则匹配“（数字）XXX”格式
            return "- " + line; // 添加列表标记，前端渲染会自动缩进
        }

        // 规则5：其他普通文本（如段落说明）→ 直接保留（Markdown默认段落格式）
        return line;
    }

    // 测试方法：验证转换效果
    public static void main(String[] args) {
        // 你的原始普通文本（无Markdown格式）
        String lessonPlanTemplate = "教案模板（一）：\n" +
                "\n" +
                "    （一）教学目标：\n" +
                "    说明本课所要完成的教学任务。\n" +
                "\n" +
                "    （二）教学重难点：\n" +
                "    说明本课所必须处理的关键性问题和学习时易产生困难和障碍的知识传授与本事培养点。\n" +
                "\n" +
                "    （三）教学过程设计\n" +
                "    1、导入新课\n" +
                "    （1）温故而知新，提问复习上节资料\n" +
                "    （2）设计新颖活泼，精当概括。\n" +
                "    （3）怎样进行，复习哪些资料？\n" +
                "    （4）提问哪些学生，需用多少时光等。\n" +
                "\n" +
                "    2、讲授新课\n" +
                "    （1）针对不一样教学资料，选择不一样的教学方法。\n" +
                "    （2）怎样提出问题，如何逐步启发、诱导？\n" +
                "    （3）教师怎样教？学生怎样学？详细步骤安排，需用时光。\n" +
                "\n" +
                "    3、巩固练习\n" +
                "    （1）练习设计精巧，有层次、有坡度、有密度。\n" +
                "    （2）怎样进行，谁上黑板板演？\n" +
                "    （3）需要多少时光？\n" +
                "\n" +
                "    4、归纳小结\n" +
                "    （1）怎样进行，是教师还是学生归纳？\n" +
                "    （2）需用多少时光？\n" +
                "\n" +
                "    5、作业布置\n" +
                "    （1）布置哪些作业资料，要研究到课本知识巩固积累和运用，兼顾知识的拓展性和学生运用语言本事的培养。\n" +
                "    （2）教师要注意：需不需要给学生以解题提示、点拨或必要的解释。";

        // 转换为Markdown
        String markdown = PlainTextToMarkdownUtil.convertToMarkdown(lessonPlanTemplate);

        // 输出结果（可直接返回给前端）
        System.out.println("转换后的Markdown文本：");
        System.out.println(markdown);
    }
}