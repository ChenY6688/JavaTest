package com.example.xlsxprocessor.service;

import com.example.xlsxprocessor.exception.FileProcessingException;
import com.example.xlsxprocessor.model.RowData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ExcelService {

    public List<RowData> processExcelFile(MultipartFile file) throws FileProcessingException {
        List<RowData> data = new ArrayList<>();
        List<String> planArray = new ArrayList<>();
        Set<String> planSet = new HashSet<>();
        List<String> duplicatePlans = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            throw new FileProcessingException("文件為空或未上傳");
        }
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new FileProcessingException("只支持處理 .xlsx 格式的文件");
        }

        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Sheet sheet = workbook.getSheetAt(0);

            int lastRowNum = getLastNonEmptyRow(sheet);

            Row firstRow = sheet.getRow(0);
            for (int col = 5;; col++) {
                Cell cell = firstRow.getCell(col);
                if (cell == null || getCellValue(cell, formulaEvaluator).trim().isEmpty())
                    break;
                String planValue = getCellValue(cell, formulaEvaluator);
                if (planSet.contains(planValue)) {
                    duplicatePlans.add(planValue);
                } else {
                    planSet.add(planValue);
                    planArray.add(planValue);
                }
            }

            if (!duplicatePlans.isEmpty()) {
                throw new Exception("plan重複命名: " + String.join(", ", duplicatePlans));
            }

            String currentAValue = "";
            String currentBValue = "";

            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;

                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.FORMULA) {
                        formulaEvaluator.evaluateFormulaCell(cell);
                    }
                }

                String aValue = getCellValue(row.getCell(0), formulaEvaluator).trim();
                String bValue = getCellValue(row.getCell(1), formulaEvaluator).trim();

                if (!aValue.isEmpty() && bValue.isEmpty()) {
                    currentAValue = aValue;
                }

                if (!aValue.isEmpty() && !bValue.isEmpty()) {
                    currentBValue = aValue;
                }

                if (!bValue.isEmpty()) {
                    for (int i = 0; i < planArray.size(); i++) {
                        String planName = planArray.get(i);
                        String coverageValue = getCellValue(row.getCell(5 + i), formulaEvaluator);

                        RowData rowData = new RowData();
                        rowData.setBenefit(currentAValue);
                        rowData.setCoverage(currentBValue);
                        rowData.setCategory(bValue);
                        rowData.setPlanName(planName);
                        rowData.setCoverageValue(coverageValue);

                        data.add(rowData);
                    }
                }
            }
        } catch (Exception e) {
            throw new FileProcessingException(e.getMessage());
        }
        return data;
    }

    private int getLastNonEmptyRow(Sheet sheet) {
        for (int i = sheet.getLastRowNum(); i >= 0; i--) {
            Row row = sheet.getRow(i);
            if (row != null && !isRowEmpty(row)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell, null).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        if (cell == null)
            return "";

        boolean isPercentageFormat = false;
        if (cell.getCellType() == CellType.NUMERIC) {
            String formatString = cell.getCellStyle().getDataFormatString();
            isPercentageFormat = formatString.contains("%");
        }

        if (cell.getCellType() == CellType.FORMULA) {
            if (formulaEvaluator == null)
                return cell.getCellFormula();

            CellValue cellValue = formulaEvaluator.evaluate(cell);

            switch (cellValue.getCellType()) {
                case NUMERIC:
                    if (isPercentageFormat) {
                        return (int) (cellValue.getNumberValue() * 100) + "%";
                    }

                    return NumberToTextConverter.toText(cellValue.getNumberValue());
                case STRING:
                    return cellValue.getStringValue();
                case BOOLEAN:
                    return String.valueOf(cellValue.getBooleanValue());
                case ERROR:
                    return "ERROR";
                default:
                    return "";
            }
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (isPercentageFormat) {
                    return (int) (cell.getNumericCellValue() * 100) + "%";
                }

                return NumberToTextConverter.toText(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}