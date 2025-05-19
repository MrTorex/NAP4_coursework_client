package by.mrtorex.businessshark.client.gui.utils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkdownExporter {
    public void export(List<Map<String, Object>> data, OutputStream out, String title) throws Exception {
        StringBuilder sb = new StringBuilder();

        if (title != null && !title.isEmpty()) {
            sb.append("## ").append(title).append("\n\n");
        }

        if (!data.isEmpty()) {
            List<String> headers = new ArrayList<>(data.get(0).keySet());

            sb.append("| ");
            for (String header : headers) {
                sb.append(header).append(" | ");
            }
            sb.append("\n| ");
            for (int i = 0; i < headers.size(); i++) {
                sb.append("--- | ");
            }
            sb.append("\n");

            for (Map<String, Object> row : data) {
                sb.append("| ");
                for (String key : headers) {
                    sb.append(row.getOrDefault(key, "")).append(" | ");
                }
                sb.append("\n");
            }
        }

        out.write(sb.toString().getBytes());
    }
}

