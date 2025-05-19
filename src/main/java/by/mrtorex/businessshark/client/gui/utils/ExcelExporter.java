package by.mrtorex.businessshark.client.gui.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    public void export(List<Map<String, Object>> data, OutputStream out, String title) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Export");

        int rowIdx = 0;

        // Заголовок
        if (title != null && !title.isEmpty()) {
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);

            CellStyle titleStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            titleStyle.setFont(font);
            titleCell.setCellStyle(titleStyle);

            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                    0, 0, 0, data.isEmpty() ? 0 : data.get(0).size() - 1
            ));
        }

        if (!data.isEmpty()) {
            // Стили для границ
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            // Заголовки
            Row headerRow = sheet.createRow(rowIdx++);
            int colIdx = 0;
            for (String key : data.get(0).keySet()) {
                Cell cell = headerRow.createCell(colIdx++);
                cell.setCellValue(key);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            for (Map<String, Object> rowMap : data) {
                Row row = sheet.createRow(rowIdx++);
                colIdx = 0;
                for (Object value : rowMap.values()) {
                    Cell cell = row.createCell(colIdx++);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        workbook.write(out);
        workbook.close();
    }
}
