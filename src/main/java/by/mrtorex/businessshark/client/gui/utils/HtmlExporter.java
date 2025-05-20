package by.mrtorex.businessshark.client.gui.utils;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Утилита для экспорта данных в формате HTML.
 * Позволяет создавать и сохранять HTML-файлы с данными.
 */
public class HtmlExporter {

    /**
     * Экспортирует данные в HTML-файл.
     *
     * @param data  список данных для экспорта
     * @param out   выходной поток для записи HTML-файла
     * @param title заголовок для HTML-документа
     * @throws Exception если произошла ошибка при экспорте
     */
    public void export(List<Map<String, Object>> data, OutputStream out, String title) throws Exception {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"ru\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <title>").append(title).append("</title>\n");
        html.append("    <style>\n");
        html.append("        table { border-collapse: collapse; width: 100%; }\n");
        html.append("        th, td { border: 1px solid #000; padding: 8px; text-align: left; }\n");
        html.append("        th { background-color: #f2f2f2; }\n");
        html.append("        h1 { font-size: 20px; text-align: center; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        html.append("    <h1>").append(title).append("</h1>\n");
        html.append("    <table>\n");

        if (!data.isEmpty()) {
            html.append("        <tr>\n");
            for (String key : data.getFirst().keySet()) {
                html.append("            <th>").append(key).append("</th>\n");
            }
            html.append("        </tr>\n");

            // Данные
            for (Map<String, Object> row : data) {
                html.append("        <tr>\n");
                for (Object value : row.values()) {
                    html.append("            <td>").append(value).append("</td>\n");
                }
                html.append("        </tr>\n");
            }
        }

        html.append("    </table>\n");
        html.append("</body>\n");
        html.append("</html>");

        out.write(html.toString().getBytes(StandardCharsets.UTF_8));
    }
}
