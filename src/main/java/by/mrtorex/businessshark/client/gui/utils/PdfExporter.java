package by.mrtorex.businessshark.client.gui.utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class PdfExporter {
    public void export(List<Map<String, Object>> data, OutputStream out, String title) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        if (title != null && !title.isEmpty()) {
            Font boldFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph titleParagraph = new Paragraph(title, boldFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(10f);
            document.add(titleParagraph);
        }

        if (!data.isEmpty()) {
            Map<String, Object> firstRow = data.get(0);
            PdfPTable table = new PdfPTable(firstRow.size());

            // Заголовки
            for (String key : firstRow.keySet()) {
                table.addCell(new Phrase(key, new Font(Font.HELVETICA, 12, Font.BOLD)));
            }

            // Данные
            for (Map<String, Object> row : data) {
                for (Object value : row.values()) {
                    table.addCell(new Phrase(value != null ? value.toString() : ""));
                }
            }

            document.add(table);
        }

        document.close();
    }
}
