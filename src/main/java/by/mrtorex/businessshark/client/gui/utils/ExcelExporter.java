package by.mrtorex.businessshark.client.gui.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Утилита для экспорта данных в формате Excel.
 * Позволяет создавать и сохранять Excel-файлы с данными.
 */
public class ExcelExporter {

    /**
     * Экспортирует данные в Excel-файл.
     *
     * @param data  список данных для экспорта
     * @param out   выходной поток для записи Excel-файла
     * @param title заголовок для листа
     * @throws Exception если произошла ошибка при экспорте
     */
    public void export(List<Map<String, Object>> data, OutputStream out, String title) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Export");

        int rowIdx = 0;

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
                    0, 0, 0, data.isEmpty() ? 0 : data.getFirst().size() - 1
            ));
        }

        if (!data.isEmpty()) {
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

            Row headerRow = sheet.createRow(rowIdx++);
            int colIdx = 0;
            for (String key : data.getFirst().keySet()) {
                Cell cell = headerRow.createCell(colIdx++);
                cell.setCellValue(key);
                cell.setCellStyle(headerStyle);
            }

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
