package org.example.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportToExcel {

    public static void export(JTable table, String fileName) {
        TableModel model = table.getModel();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file Excel");
        chooser.setSelectedFile(new java.io.File(fileName != null ? fileName : "data.xlsx"));
        int result = chooser.showSaveDialog(table);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())) {

            Sheet sheet = workbook.createSheet("Sheet1");

            // Header
            Row header = sheet.createRow(0);
            for (int c = 0; c < model.getColumnCount(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(model.getColumnName(c));
            }

            // Data
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Cell cell = row.createCell(c);
                    Object value = model.getValueAt(r, c);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            // Auto size columns
            for (int c = 0; c < model.getColumnCount(); c++) {
                sheet.autoSizeColumn(c);
            }

            workbook.write(fos);

            // Mở file Excel luôn
            File excelFile = chooser.getSelectedFile();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(excelFile);
            }

            JOptionPane.showMessageDialog(table, "Xuất Excel thành công và mở file!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(table, "Lỗi khi xuất Excel: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

}
