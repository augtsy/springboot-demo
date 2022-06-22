package com.example.demo.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Demo {

    /**
     * @param title      标题集合 tilte的长度应该与list中的model的属性个数一致
     * @param maps       内容集合
     * @param mergeIndex 合并单元格的列
     */
    public static String createExcel(String[] title, Map<String, List<Map<String, String>>> maps, int[] mergeIndex) {
        if (title.length == 0) {
            return null;
        }
        /*初始化excel模板*/
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = null;
        int n = 0;
        for (Map.Entry<String, List<Map<String, String>>> entry : maps.entrySet()) {
            try {
                sheet = workbook.createSheet();
                workbook.setSheetName(n, entry.getKey());
                workbook.setSelectedTab(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*初始化head，填值标题行（第一行）*/
            Row row0 = sheet.createRow(0);
            for (int i = 0; i < title.length; i++) {
                /*创建单元格，指定类型*/
                Cell cell_1 = row0.createCell(i);
                cell_1.setCellValue(title[i]);
            }
            /*得到当前sheet下的数据集合*/
            List<Map<String, String>> list = entry.getValue();
            /*遍历该数据集合*/
            List<PoiModel> poiModels = new ArrayList();
            if (null != workbook) {
                Iterator iterator = list.iterator();
                int index = 1;
                while (iterator.hasNext()) {
                    Row row = sheet.createRow(index);
                    /*取得当前这行的map，该map中以key，value的形式存着这一行值*/
                    Map<String, String> map = (Map<String, String>) iterator.next();
                    /*循环列数，给当前行塞值*/
                    for (int i = 0; i < title.length; i++) {
                        String old = "";
                        /*old存的是上一行统一位置的单元的值，第一行是最上一行了，所以从第二行开始记*/
                        if (index > 1) {
                            old = poiModels.get(i) == null ? "" : poiModels.get(i).getContent();
                        }
                        /*循环需要合并的列*/
                        for (int j = 0; j < mergeIndex.length; j++) {
                            if (index == 1) {
                                /*记录第一行的开始行和开始列*/
                                PoiModel poiModel = new PoiModel();
                                poiModel.setOldContent(map.get(title[i]));
                                poiModel.setContent(map.get(title[i]));
                                poiModel.setRowIndex(1);
                                poiModel.setCellIndex(i);
                                poiModels.add(poiModel);
                                break;
                            } else if (i > 0 && mergeIndex[j] == i) {/*这边i>0也是因为第一列已经是最前一列了，只能从第二列开始*/
                                /*当前同一列的内容与上一行同一列不同时，把那以上的合并, 或者在当前元素一样的情况下，前一列的元素并不一样，这种情况也合并*/
                                /*如果不需要考虑当前行与上一行内容相同，但是它们的前一列内容不一样则不合并的情况，把下面条件中　　　　　　　　　　　　　　　　　　||poiModels.get(i).getContent().equals(map.get(title[i])) && !poiModels.get(i - 1).getOldContent().equals(map.get(title[i-1]))去掉就行*/
                                if (!poiModels.get(i).getContent().equals(map.get(title[i])) || poiModels.get(i).getContent().equals(map.get(title[i])) &&
                                        !poiModels.get(i - 1).getOldContent().equals(map.get(title[i - 1]))) {
                                    /*当前行的当前列与上一行的当前列的内容不一致时，则把当前行以上的合并*/
                                    CellRangeAddress cra = new CellRangeAddress(poiModels.get(i).getRowIndex(), index - 1, poiModels.get(i).getCellIndex(), poiModels.get(i).getCellIndex());
                                    //在sheet里增加合并单元格  
                                    sheet.addMergedRegion(cra);
                                    /*重新记录该列的内容为当前内容，行标记改为当前行标记，列标记则为当前列*/
                                    poiModels.get(i).setContent(map.get(title[i]));
                                    poiModels.get(i).setRowIndex(index);
                                    poiModels.get(i).setCellIndex(i);
                                }
                            }
                            /*处理第一列的情况*/
                            if (mergeIndex[j] == i && i == 0 && !poiModels.get(i).getContent().equals(map.get(title[i]))) {
                                /*当前行的当前列与上一行的当前列的内容不一致时，则把当前行以上的合并*/
                                CellRangeAddress cra = new CellRangeAddress(poiModels.get(i).getRowIndex(), index - 1, poiModels.get(i).getCellIndex(), poiModels.get(i).getCellIndex());
                                //在sheet里增加合并单元格  
                                sheet.addMergedRegion(cra);
                                /*重新记录该列的内容为当前内容，行标记改为当前行标记*/
                                poiModels.get(i).setContent(map.get(title[i]));
                                poiModels.get(i).setRowIndex(index);
                                poiModels.get(i).setCellIndex(i);
                            }

                            /*最后一行没有后续的行与之比较，所有当到最后一行时则直接合并对应列的相同内容*/
                            if (mergeIndex[j] == i && index == list.size()) {
                                CellRangeAddress cra = new CellRangeAddress(poiModels.get(i).getRowIndex(), index, poiModels.get(i).getCellIndex(), poiModels.get(i).getCellIndex());
                                //在sheet里增加合并单元格  
                                sheet.addMergedRegion(cra);
                            }
                        }
                        Cell cell = row.createCell(i);
                        cell.setCellValue(map.get(title[i]));
                        /*在每一个单元格处理完成后，把这个单元格内容设置为old内容*/
                        poiModels.get(i).setOldContent(old);
                    }
                    index++;
                }
            }
            n++;
        }
        /*生成临时文件*/
        FileOutputStream out = null;
        String localPath = null;
        File tempFile = null;
        String fileName = String.valueOf(new Date().getTime() / 1000);
        try {
            tempFile = File.createTempFile(fileName, "/Users/tangshanyuan/test/demo.xls");
            localPath = tempFile.getAbsolutePath();
            out = new FileOutputStream(localPath);
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

    public static void main(String[] args) throws IOException {
        /*此处标题的数组则对应excel的标题*/
        String[] title = {"id", "标题", "描述", "负责人", "开始时间"};
        List<Map<String, String>> list = new ArrayList();
        /*这边是制造一些数据，注意每个list中map的key要和标题数组中的元素一致*/
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap();
            if (i > 5) {
                if (i < 7) {
                    map.put("id", "333");
                    map.put("标题", "mmmm");
                } else {
                    map.put("id", "333");
                    map.put("标题", "aaaaa");
                }
            } else if (i > 3) {
                map.put("id", "222");
                map.put("标题", "哈哈哈哈");
            } else if (i > 1 && i < 3) {
                map.put("id", "222");
                map.put("标题", "hhhhhhhh");
            } else {
                map.put("id", "222");
                map.put("标题", "bbbb");
            }
            map.put("描述", "sssssss");
            map.put("负责人", "vvvvv");
            map.put("开始时间", "2017-02-27 11:20:26");
            list.add(map);
        }
        Map<String, List<Map<String, String>>> map = new HashMap();
        map.put("测试合并数据", list);
        System.out.println(createExcel(title, map, new int[]{0, 1, 2}));
    }

}