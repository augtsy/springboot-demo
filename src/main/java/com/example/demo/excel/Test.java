package com.example.demo.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by zelei.fan on 2017/3/14.
 */
public class Test {

    /**
     * @param title      标题集合 tilte的长度应该与list中的model的属性个数一致
     * @param maps       内容集合
     * @param mergeIndex 合并单元格的列
     */
    @SneakyThrows
    public static String createExcel(String[] title, Map<String/*sheet名*/, List<Map<String/*对应title的值*/, String>>> maps, int[] mergeIndex) {
        if (title.length == 0) {
            return null;
        }
        /*初始化excel模板*/
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = null;
        int n = 0;
        /*循环sheet页*/
        for (Map.Entry<String, List<Map<String/*对应title的值*/, String>>> entry : maps.entrySet()) {
            /*实例化sheet对象并且设置sheet名称，book对象*/
            try {
                sheet = workbook.createSheet();
                workbook.setSheetName(n, entry.getKey());
                workbook.setSelectedTab(n);
                n++;
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
            List<Map<String/*对应title的值*/, String>> list = entry.getValue();
            /*遍历该数据集合*/
            List<PoiModel> poiModels = Lists.newArrayList();
            if (null != workbook) {
                Iterator iterator = list.iterator();
                int index = 1;/*这里1是从excel的第二行开始，第一行已经塞入标题了*/
                while (iterator.hasNext()) {
                    Row row = sheet.createRow(index);
                    /*取得当前这行的map，该map中以key，value的形式存着这一行值*/
                    Map<String, String> map = (Map<String, String>) iterator.next();
                    /*循环列数，给当前行塞值*/
                    for (int i = 0; i < title.length; i++) {

                        String titleName = title[i];

                        String old = "";
                        /*old存的是上一行统一位置的单元的值，第一行是最上一行了，所以从第二行开始记*/
                        if (index > 1) {
                            old = poiModels.get(i) == null ? "" : poiModels.get(i).getContent();
                        }

                        /*循环需要合并的列*/
                        for (int k : mergeIndex) {
                            if (index == 1) {
                                // 记录第一行的开始行和开始列
                                PoiModel build = PoiModel.builder()
                                        .oldContent(map.get(titleName))
                                        .primaryKey(map.get(title[0]))
                                        .content(map.get(titleName))
                                        .rowIndex(1)
                                        .cellIndex(i)
                                        .build();
                                poiModels.add(build);
                                break;
                            } else if (i > 0 && k == i) {/*这边i>0也是因为第一列已经是最前一列了，只能从第二列开始*/
                                /*当前同一列的内容与上一行同一列不同时，把那以上的合并, 或者在当前元素一样的情况下，前一列的元素并不一样，这种情况也合并*/
                                /*如果不需要考虑当前行与上一行内容相同，但是它们的前一列内容不一样则不合并的情况，把下面条件中||poiModel.getContent().equals(map.get(titleName)) && !poiModels.get(i - 1).getOldContent().equals(map.get(title[i-1]))去掉就行*/
                                PoiModel poiModel = poiModels.get(i);
                                if (!poiModel.getContent().equals(map.get(titleName)) || poiModel.getContent().equals(map.get(titleName)) && !poiModels.get(i).getPrimaryKey().equals(map.get(title[0]))) {
                                    /*当前行的当前列与上一行的当前列的内容不一致时，则把当前行以上的合并*/
                                    /*从第二行开始 到第几行 从某一列开始 到第几列 */
                                    if (poiModel.getRowIndex() == index - 1) {
                                        break;
                                    }
                                    CellRangeAddress cra = new CellRangeAddress(poiModel.getRowIndex(), index - 1, poiModel.getCellIndex(), poiModel.getCellIndex());
                                    //在sheet里增加合并单元格
                                    sheet.addMergedRegion(cra);
                                    /*重新记录该列的内容为当前内容，行标记改为当前行标记，列标记则为当前列*/
                                    poiModel.setContent(map.get(titleName));
                                    poiModel.setRowIndex(index);
                                    poiModel.setCellIndex(i);
                                    poiModel.setPrimaryKey(map.get(title[0]));
                                    break;
                                }
                            }
                            PoiModel poiModel = poiModels.get(i);
                            /*处理第一列的情况*/
                            if (k == i && i == 0 && !poiModel.getContent().equals(map.get(titleName))) {
                                /*当前行的当前列与上一行的当前列的内容不一致时，则把当前行以上的合并*/
                                if (poiModel.getRowIndex() == index - 1) {
                                    break;
                                }
                                CellRangeAddress cra = new CellRangeAddress(poiModel.getRowIndex()/*从第二行开始*/, index - 1/*到第几行*/, poiModel.getCellIndex()/*从某一列开始*/, poiModel.getCellIndex()/*到第几列*/);
                                //在sheet里增加合并单元格
                                sheet.addMergedRegion(cra);
                                /*重新记录该列的内容为当前内容，行标记改为当前行标记*/
                                poiModel.setContent(map.get(titleName));
                                poiModel.setRowIndex(index);
                                poiModel.setCellIndex(i);
                                break;
                            }

                            /*最后一行没有后续的行与之比较，所有当到最后一行时则直接合并对应列的相同内容*/
                            if (k == i && index == list.size()) {
                                CellRangeAddress cra = new CellRangeAddress(poiModel.getRowIndex()/*从第二行开始*/, index/*到第几行*/, poiModel.getCellIndex()/*从某一列开始*/, poiModel.getCellIndex()/*到第几列*/);
                                //在sheet里增加合并单元格
                                sheet.addMergedRegion(cra);
                                break;
                            }
                        }
                        Cell cell = row.createCell(i);
                        cell.setCellValue(map.get(titleName));
                        /*在每一个单元格处理完成后，把这个单元格内容设置为old内容*/
                        poiModels.get(i).setOldContent(old);
                    }
                    index++;
                }
            }
            n++;
        }

        File file = new File("/Users/tangshanyuan/test/demo.xls");
        FileOutputStream fout = new FileOutputStream(file);
        workbook.write(fout);
        fout.close();

        return file.getAbsolutePath();
    }

    public static void main(String[] args) throws IOException {
        /*此处标题的数组则对应excel的标题*/
        String[] title = {"id", "标题", "描述", "负责人", "开始时间"};
        List<Map<String, String>> list = Lists.newArrayList();
        /*这边是制造一些数据，注意每个list中map的key要和标题数组中的元素一致*/
        for (int i = 0; i < 20; i++) {
            HashMap<String, String> map = Maps.newHashMap();
            map.put("标题", "");
            if (i > 5) {
                if (i < 7) {
                    map.put("id", "333");
//                    map.put("标题", "mmmm");
                } else if (i > 13 && i < 16) {
                    map.put("id", "444");
//                    map.put("标题", "mmmm");
                } else {
                    map.put("id", "444");
//                    map.put("标题", "aaaaa");
                }
            } else if (i > 3) {
                map.put("id", "222");
//                map.put("标题", "哈哈哈哈");
            } else if (i == 2) {
                map.put("id", "222");
                map.put("标题", "hhhhhhhh");
            } else {
                map.put("id", "222");
                map.put("标题", "bbbb");
            }
            map.put("描述", "");
            map.put("负责人", "vvvvv");
            map.put("开始时间", "2017-02-27 11:20:26");
            list.add(map);
        }

        Map<String, List<Map<String, String>>> map = Maps.newHashMap();
        map.put("测试合并数据-1", list);
        map.put("测试合并数据-2", list);

        List<List<Map<String, String>>> groups = pageByNum(list, 5);

        System.out.println(createExcel(title, map, new int[]{0, 1, 2}/*此处数组为需要合并的列，可能有的需求是只需要某些列里面相同内容合并*/));
    }

    public static <T> List<List<T>> pageByNum(List<T> list, int pageSize) {
        return IntStream.range(0, list.size()).boxed().filter(t -> t % pageSize == 0).map(t -> list.stream().skip(t).limit(pageSize).map(r -> r).collect(Collectors.toList())).collect(Collectors.toList());
    }

}