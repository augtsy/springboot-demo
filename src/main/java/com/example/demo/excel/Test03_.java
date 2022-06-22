package com.example.demo.excel;

import com.example.demo.entity.Dept;
import com.example.demo.entity.TestUser;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test03_ {

    /**
     * 合并单元格 测试
     *
     * @param title    sheet页名称
     * @param onlyKeys 每条数据独一数据表示
     * @param head     表头
     * @param rowList  主题数据
     * @param subDatas 子数据 map 的key 就属于 onlyKeys中的值
     * @return
     * @throws Exception
     */
    public static HSSFWorkbook warpSingleWorkbook2(String title, List<String> onlyKeys, List<String> head,
                                                   List<List<Object>> rowList, Map<String, List<List<Object>>> subDatas) throws Exception {
        String filename = title;
//        if (!PlatformUtils.hasText(title)) {
//            filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
//        }
        if (rowList == null || rowList.isEmpty()) {
            throw new NullPointerException("the row list is null");
        }
        // 如果要设置背景色 最好用 XSSFWorkbook
        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet(filename);
        sheet.setDefaultColumnWidth(20);
        HSSFCellStyle style = book.createCellStyle();

        // 生成表头
        HSSFRow headRow = sheet.createRow(0);
        for (int i = 0; i < head.size(); i++) {
            HSSFCellStyle headStyle = book.createCellStyle();
            setExcelValue(headRow.createCell(i), head.get(i), headStyle);
        }
        Iterator<List<Object>> iterator = rowList.iterator();
        int rowIndex = 1;
        int keyIndex = 0;
        int commonTotalSize = rowList.get(0).size();
        List<List<Integer>> mergeParams = new ArrayList<>();
        while (iterator.hasNext()) {
            List<Object> rowDatas = iterator.next();
            int startRowIndex = rowIndex;// 记录合并的开始行
            HSSFRow bodyRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < rowDatas.size(); i++) {
                setExcelValue(bodyRow.createCell(i), rowDatas.get(i), style);
            }
            String key = onlyKeys.get(keyIndex++);
            if (subDatas != null) {
                List<List<Object>> dataLists = subDatas.get(key);//组装数据的时候至少又一个，没有数据空串填充一个数据
                List<Object> firstSub = dataLists.get(0);
                for (int i = 0; i < firstSub.size(); i++) {
                    Object value = firstSub.get(i);
                    setExcelValue(bodyRow.createCell(rowDatas.size() + i), value, style);
                }
                for (int i = 1; i < dataLists.size(); i++) {
                    List<Object> list = dataLists.get(i);
                    HSSFRow bodyRow2 = sheet.createRow(rowIndex++);
                    for (int j = 0; j < list.size(); j++) {
                        Object value = list.get(j);
                        setExcelValue(bodyRow2.createCell(rowDatas.size() + j), value, style);
                    }
                }
                if (dataLists.size() > 1) {
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
            List<List<Object>> rowList = new ArrayList<List<Object>>();
            rowList.add(Arrays.asList("张三", "男"));
            rowList.add(Arrays.asList("李四", "男"));
            rowList.add(Arrays.asList("王王", "女"));
            rowList.add(Arrays.asList("王五", "女"));
            List<String> onlyKeys = Arrays.asList("zs", "ls", "ww", "w5");
            List<String> head = Arrays.asList("部门ID", "部门", "人数", "人员ID", "姓名", "邮箱");
            Map<String, List<List<Object>>> subDatas = new HashMap<String, List<List<Object>>>();
            List<List<Object>> zsCj = new ArrayList<List<Object>>();
            zsCj.add(Arrays.asList("语文", "90", "优"));
            zsCj.add(Arrays.asList("数学", "98", "优"));
            zsCj.add(Arrays.asList("英语", "60", "中"));
            subDatas.put("zs", zsCj);
            List<List<Object>> lsCj = new ArrayList<List<Object>>();
            lsCj.add(Arrays.asList("语文", "100", "优"));
            lsCj.add(Arrays.asList("数学", "100", "优"));
//            lsCj.add(Arrays.asList("英语", "100", "优"));
            subDatas.put("ls", lsCj);
            List<List<Object>> wwCj = new ArrayList<List<Object>>();
            wwCj.add(Arrays.asList("语文", "50", "差"));
            wwCj.add(Arrays.asList("数学", "50", "差"));
            wwCj.add(Arrays.asList("外语", "50", "差"));
            subDatas.put("ww", wwCj);
            List<List<Object>> wwC = new ArrayList<List<Object>>();
            wwC.add(Arrays.asList("语文", "50", "差"));
            wwC.add(Arrays.asList("数学", "50", "差"));
            wwC.add(Arrays.asList("外语", "50", "差"));
            subDatas.put("w5", wwC);


            HSSFWorkbook wb = warpSingleWorkbook2("测试", onlyKeys, head, rowList, subDatas);
            File file = new File("/Users/tangshanyuan/test/demo1.xls");
            fileOut = new FileOutputStream(file);
            // 写入excel文件
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

    private static List<Dept> getData() {
        List<Dept> depts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Dept dept = new Dept();
            dept.setId(i + "主键");
            dept.setName(i + "部门");
            dept.setNum(i + "");
            List<TestUser> testUserList = new ArrayList<>();
            for (int j = 0; j < new Random().nextInt(10) + 1; j++) {
                TestUser testUser = new TestUser();
                testUser.setUserId(j);
                testUser.setUserName(j + "姓名");
                testUser.setEmail(j + "544416131@qq.com");
                testUserList.add(testUser);
            }
            dept.setTestUsers(testUserList);
            depts.add(dept);
        }
        return depts;
    }

    public static <T> List<List<T>> pageByNum(List<T> list, int pageSize) {
        return IntStream.range(0, list.size()).boxed().filter(t -> t % pageSize == 0).map(t -> list.stream().skip(t).limit(pageSize).map(r -> r).collect(Collectors.toList())).collect(Collectors.toList());
    }
}
