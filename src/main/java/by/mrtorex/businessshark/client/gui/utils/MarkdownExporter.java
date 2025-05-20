package by.mrtorex.businessshark.client.gui.utils;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Утилита для экспорта данных в формате Markdown.
 * Позволяет создавать и сохранять Markdown-файлы с данными.
 */
public class MarkdownExporter {

    /**
     * Экспортирует данные в Markdown-файл.
     *
     * @param data  список данных для экспорта
     * @param out   выходной поток для записи Markdown-файла
     * @param title заголовок для Markdown-документа
     * @throws Exception если произошла ошибка при экспорте
     */
    public void export(List<Map<String, Object>> data, OutputStream out, String title) throws Exception {
        StringBuilder sb = new StringBuilder();

        if (title != null && !title.isEmpty()) {
            sb.append("## ").append(title).append("\n\n");
        }

        if (!data.isEmpty()) {
            List<String> headers = new ArrayList<>(data.getFirst().keySet());

            sb.append("| ");
            for (String header : headers) {
                sb.append(header).append(" | ");
            }
            sb.append("\n| ");
            sb.append("--- | ".repeat(headers.size()));
            sb.append("\n");

            for (Map<String, Object> row : data) {
                sb.append("| ");
                for (String key : headers) {
                    sb.append(row.getOrDefault(key, "")).append(" | ");
                }
                sb.append("\n");
            }
        }

        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
