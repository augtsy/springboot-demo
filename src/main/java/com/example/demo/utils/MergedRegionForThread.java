package com.example.demo.utils;

import com.example.demo.excel.ExcelModel;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 多线程合并表格
 */
public class MergedRegionForThread implements Runnable {

    private final CountDownLatch latch;
    private final List<ExcelModel> dataList;
    private final HSSFSheet sheet;

    public MergedRegionForThread(CountDownLatch latch, List<ExcelModel> dataList, HSSFSheet sheet) {
        this.latch = latch;
        this.dataList = dataList;
        this.sheet = sheet;
    }

    @Override
    public void run() {
        try {
            dataList.forEach(model -> sheet.addMergedRegionUnsafe(new CellRangeAddress(model.getFirstRow(), model.getLastRow(), model.getFirstCol(), model.getLastCol())));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != latch) {
                latch.countDown();
            }
        }
    }

}
