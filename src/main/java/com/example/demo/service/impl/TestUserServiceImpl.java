package com.example.demo.service.impl;

import com.example.demo.entity.Dept;
import com.example.demo.entity.TestUser;
import com.example.demo.excel.ExcelModel;
import com.example.demo.mapper.TestUserMapper;
import com.example.demo.service.TestUserService;
import com.example.demo.utils.MergedRegionForThread;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

@Service(value = "testUserService")
public class TestUserServiceImpl implements TestUserService {

    private int dataLength = 0;

    private static Logger logger = LoggerFactory.getLogger(TestUserServiceImpl.class);

    @Autowired
    private TestUserMapper testUserMapper;
    @Resource(name = "exportTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Override
//    @Cacheable(value = "userCache", key = "'user.findAll'")
    public List<TestUser> queryAllUser() throws Exception {
        logger.info("queryAllUser 日志打印");
        System.out.println("从Mysql中查询");
//        List<TestUser> testUsers = redisTemplate.opsForList().range("allUser", 0, -1);
//        if (null == testUsers || testUsers.size() == 0) {
        List<TestUser> testUsers = testUserMapper.queryAllUser();
//            redisTemplate.opsForList().rightPush("allUser", testUsers);
//        }
        dataLength = +testUsers.size();
        System.out.println(dataLength);
        System.out.println("testUsers = " + testUsers);
        return testUsers;
    }

    /**
     * @param testUser
     * @return
     * @author tsy
     */
    @Override
    @Transactional(readOnly = false)
    public Integer saveUser(TestUser testUser) throws Exception {
        List<TestUser> byUserName = testUserMapper.queryByUserName(testUser.getUserName());
        if (byUserName != null && byUserName.size() > 0) {
            throw new RuntimeException("用户已存在");
        }
        Integer integer = testUserMapper.saveUser(testUser);
        return integer;
    }

    @Override
    public void export() {
        test();
    }

    private void test() {
        FileOutputStream fileOut = null;
        try {
            List<List<Object>> rowList = new ArrayList<List<Object>>();
            List<String> head = Arrays.asList("部门ID", "部门", "人数", "人员ID", "姓名", "邮箱");
            Map<String, List<List<Object>>> subDatas = new HashMap<String, List<List<Object>>>();

            List<Dept> depts = getData();
            List<String> onlyKeys = new ArrayList<>();
            for (int i = 0; i < depts.size(); i++) {
                Dept dept = depts.get(i);
                List<Object> list = new ArrayList<>();
                list.add(dept.getId());
                list.add(dept.getName());
                list.add(dept.getNum());
                rowList.add(list);
                onlyKeys.add(i + "");
            }


            for (int i = 0; i < onlyKeys.size(); i++) {
                List<List<Object>> list = new ArrayList<>();
                List<TestUser> testUsers = depts.get(i).getTestUsers();
                for (int j = 0; j < testUsers.size(); j++) {
                    TestUser testUser = testUsers.get(j);
                    List<Object> objects = new ArrayList<>();
                    objects.add(testUser.getUserId());
                    objects.add(testUser.getUserName());
                    objects.add(testUser.getEmail());
                    list.add(objects);
                }
                subDatas.put(i + "", list);
            }


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
    public HSSFWorkbook warpSingleWorkbook2(String title, List<String> onlyKeys, List<String> head,
                                            List<List<Object>> rowList, Map<String, List<List<Object>>> subDatas) throws Exception {
        long startTime = System.currentTimeMillis();
        String filename = title;
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
        List<ExcelModel> mergeParams = new ArrayList<>();
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
                        ExcelModel build = ExcelModel.builder().firstRow(startRowIndex).lastRow(rowIndex - 1).firstCol(i).lastCol(i).build();
                        mergeParams.add(build);
                    }
                }
            }

        }

        if (mergeParams.size() > 0) {
            BlockingQueue<List<ExcelModel>> queue = new ArrayBlockingQueue<>(mergeParams.size());
            //以先入先出的顺序排序的阻塞队列
            queue.addAll(Collections.singleton(mergeParams));

            CountDownLatch latch = new CountDownLatch(queue.size());
            int rowNum = 0;
            while (queue.size() > 0) {
                try {
                    System.out.println(rowNum);
                    rowNum++;
                    taskExecutor.execute(new MergedRegionForThread(latch, queue.take(), sheet));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        return book;
    }

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

    private static List<Dept> getData() {
        List<Dept> depts = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
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
}
