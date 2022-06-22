package com.example.demo.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Test03ExcelDemo {

    @SuppressWarnings("unchecked")
    public static HSSFWorkbook warpSingleWorkbook2(String title, List<Map<String, Object>> mapsList, List<String> head) throws Exception {

        String[] str = {"id", "name", "num"};
        String testUsers = "testUsers";
        String[] str2 = {"userId", "userName", "email"};

        if (mapsList == null || mapsList.isEmpty()) {
            throw new NullPointerException("the row list is null");
        }
        // 如果要设置背景色 最好用 XSSFWorkbook
        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet(title);
        sheet.setDefaultColumnWidth(20);
        HSSFCellStyle style = book.createCellStyle();

        // 生成表头
        HSSFRow headRow = sheet.createRow(0);
        for (int i = 0; i < head.size(); i++) {
            HSSFCellStyle headStyle = book.createCellStyle();
            setExcelValue(headRow.createCell(i), head.get(i), headStyle);
        }

        int rowIndex = 1;
        int commonTotalSize = mapsList.get(0).size() - 1;
        List<List<Integer>> mergeParams = new ArrayList<>();

        for (Map<String, Object> map : mapsList) {
            // 记录合并的开始行
            int startRowIndex = rowIndex;
            HSSFRow bodyRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < str.length; i++) {
                setExcelValue(bodyRow.createCell(i), map.get(str[i]), style);
            }
            //组装数据的时候至少又一个，没有数据空串填充一个数据
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(testUsers);
            for (int i = 0; i < str2.length; i++) {
                setExcelValue(bodyRow.createCell(str.length + i), null, style);
            }
            for (int i = 1; i < list.size(); i++) {
                HSSFRow bodyRow2 = sheet.createRow(rowIndex++);
                for (int j = 0; j < str2.length; j++) {
                    setExcelValue(bodyRow2.createCell(str.length + j), list.get(i).get(str2[j]), style);
                }
            }
            if (list.size() > 1) {
                // 依次放入  起始行 结束行 起始列 结束列
                for (int i = 0; i < commonTotalSize; i++) {
                    List<Integer> mergeParam = new ArrayList<>(4);
                    mergeParam.add(startRowIndex);
                    mergeParam.add(rowIndex - 1);
                    mergeParam.add(i);
                    mergeParam.add(i);
                    mergeParams.add(mergeParam);
                }
            }
        }

        for (List<Integer> list : mergeParams) {
            sheet.addMergedRegion(new CellRangeAddress(list.get(0), list.get(1), list.get(2), list.get(3)));
        }
        return book;
    }

    /**
     * 设置Excel浮点数可做金额等数据统计
     *
     * @param cell  单元格类
     * @param value 传入的值
     */
    public static void setExcelValue(HSSFCell cell, Object value, HSSFCellStyle style) {
        // 写数据
        if (value == null) {
            cell.setCellValue("");
        } else {
            if (value instanceof Integer || value instanceof Long) {
                cell.setCellValue(Long.parseLong(value.toString()));
            } else if (value instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) value).setScale(1, RoundingMode.HALF_UP).doubleValue());
            } else {
                cell.setCellValue(value.toString());
            }
            cell.setCellStyle(style);
        }
    }

    public static void main(String[] args) {
        FileOutputStream fileOut = null;
        try {
            List<String> head = Arrays.asList("部门ID", "部门", "renshu", "人员ID", "姓名", "邮箱");
            List<Map<String, Object>> depts = getData();
            HSSFWorkbook wb = warpSingleWorkbook2("测试", depts, head);
            File file = new File("/Users/tangshanyuan/test/new2.xls");
            fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            System.out.println("----Excle文件已生成------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 模拟查询获取数据
     *
     * @return
     */
    private static List<Map<String, Object>> getData() {
        List<Map<String, Object>> depts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> deptMap = new HashMap<>();
            deptMap.put("id", i + "主键");
            if (i > 0) {
                deptMap.put("name", i + "部门");
            } else {
                deptMap.put("name", null);
            }
            deptMap.put("num", i + "");

            List<Map<String, String>> testUserList = new ArrayList<>();
            for (int j = 0; j < new Random().nextInt(10) + 1; j++) {
                Map<String, String> testUser = new HashMap<>();
                testUser.put("userId", j + "");
                testUser.put("userName", j + "姓名");
                testUser.put("email", j + "544416131");
                testUserList.add(testUser);
            }
            deptMap.put("testUsers", testUserList);
            depts.add(deptMap);
        }
        return depts;
    }

}
