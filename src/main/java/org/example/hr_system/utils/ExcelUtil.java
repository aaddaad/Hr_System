package org.example.hr_system.utils;

import org.apache.poi.ss.usermodel.*;
import org.example.hr_system.entity.Position;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static List<Position> readExcelToPosition(MultipartFile file) {
        List<Position> list = new ArrayList<>();
        try {
            InputStream in = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(in);
            Sheet sheet = workbook.getSheetAt(0);

            // 从第二行读取数据（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Position position = new Position();
                position.setTitle(getCellString(row.getCell(0)));
                position.setDescription(getCellString(row.getCell(1)));
                list.add(position);
            }
            workbook.close();
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Excel导入失败");
        }
        return list;
    }

    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }
}