package com.lingyun.common.util;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/5/19.
 */
public class ExcelUtil {
    public static void main(String[] args) throws Exception {
        File file = new File("D:\\develop\\projects\\ideaProjects\\anhua-heicha\\src\\main\\webapp\\statics\\template\\开户行支行信息.xls");
//        File file = new File("D:\\develop\\projects\\ideaProjects\\anhua-heicha\\src\\main\\webapp\\statics\\template\\下载收款银行名称列表（新）.xls");
        String[][] result = getData(file, 1);
        int rowLength = result.length;
        for(int i=0;i<rowLength;i++) {
            for(int j=1;j<result[i].length;j++) {
                System.out.print(result[i][j]+"\t\t");
            }
            System.out.println();
        }

    }
    /**
     * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
     * @param file 读取数据的源Excel
     * @param ignoreRows 读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
     * @return 读出的Excel中数据的内容
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String[][] getData(File file, int ignoreRows)
            throws FileNotFoundException, IOException {
        List<String[]> result = new ArrayList<String[]>();
        int rowSize = 0;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                file));
        // 打开HSSFWorkbook
        POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFCell cell = null;
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            // 第一行为标题，不取
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
                HSSFRow row = st.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                int tempRowSize = row.getLastCellNum() + 1;
                if (tempRowSize > rowSize) {
                    rowSize = tempRowSize;
                }
                String[] values = new String[rowSize];
                Arrays.fill(values, "");
                boolean hasValue = false;
                for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
                    String value = "";
                    cell = row.getCell(columnIndex);
                    if (cell != null) {
                        // 注意：一定要设成这个，否则可能会出现乱码
//                        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    if (date != null) {
                                        value = new SimpleDateFormat("yyyy-MM-dd")
                                                .format(date);
                                    } else {
                                        value = "";
                                    }
                                } else {
                                    value = new DecimalFormat("0").format(cell
                                            .getNumericCellValue());
                                }
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA:
                                // 导入时如果为公式生成的数据则无值
                                if (!cell.getStringCellValue().equals("")) {
                                    value = cell.getStringCellValue();
                                } else {
                                    value = cell.getNumericCellValue() + "";
                                }
                                break;
                            case HSSFCell.CELL_TYPE_BLANK:
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                value = "";
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                value = (cell.getBooleanCellValue() == true ? "Y"
                                        : "N");
                                break;
                            default:
                                value = "";
                        }
                    }
                    if (columnIndex == 0 && value.trim().equals("")) {
                        break;
                    }
                    values[columnIndex] = rightTrim(value);
                    hasValue = true;
                }

                if (hasValue) {
                    result.add(values);
                }
            }
        }
        in.close();
        String[][] returnArray = new String[result.size()][rowSize];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = (String[]) result.get(i);
        }
        return returnArray;
    }

    /**
     * 去掉字符串右边的空格
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */
    public static String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20) {
                break;
            }
            length--;
        }
        return str.substring(0, length);
    }
    /**
     * 生成excel并下载
     */
//    public static void exportExcel(HttpServletResponse response,String templateFilePath,String tempFilePath,List<Map<String, Object>> data,int beginRow,int beginCell,int dataColumns) {
//
//        File newFile = createNewFileFromTemplate(templateFilePath,tempFilePath);
//        // File newFile = new File("d:/ss.xls");
//
//        // 新文件写入数据，并下载*****************************************************
//        InputStream is = null;
//        XSSFWorkbook workbook = null;
//        XSSFSheet sheet = null;
//        try {
//            is = new FileInputStream(newFile);// 将excel文件转为输入流
//            //TODO 这里只能导出xls格式，如果xlsx格式则需要HSSFWorkbook
////            workbook = create(is);
//            workbook = new XSSFWorkbook(is);// 创建个workbook，
//            // 获取第一个sheet
//            sheet = workbook.getSheetAt(0);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        if (sheet != null) {
//            try {
//                // 写数据
//                FileOutputStream fos = new FileOutputStream(newFile);
//                XSSFRow row = sheet.getRow(beginRow);
//                if (row == null) {
//                    row = sheet.createRow(beginRow);
//                }
//                XSSFCell cell = row.getCell(0);
//                if (cell == null) {
//                    cell = row.createCell(0);
//                }
//
//
//                for (int m = 0; m < data.size(); m++) {
//                    Map<String, Object> map = data.get(m);
//                    row = sheet.createRow((int) m + 3);
//                    for (int i = beginCell; i < beginCell+dataColumns; i++) {
//                        String value = map.get("id" + m) + "";
//                        if (value.equals("null")) {
//                            value = "0";
//                        }
//                        cell = row.createCell(i);
//                        cell.setCellValue(value);
//                    }
//
//                }
//                workbook.write(fos);
//                fos.flush();
//                fos.close();
//
//                // 下载
//                InputStream fis = new BufferedInputStream(new FileInputStream(newFile));
//
//                byte[] buffer = new byte[fis.available()];
//                fis.read(buffer);
//                fis.close();
//                response.reset();
//                response.setContentType("text/html;charset=UTF-8");
//                OutputStream toClient = new BufferedOutputStream(
//                        response.getOutputStream());
//                response.setContentType("application/x-msdownload");
//                String newName = URLEncoder.encode(
//                        "违法案件报表" + System.currentTimeMillis() + ".xlsx",
//                        "UTF-8");
//                response.addHeader("Content-Disposition",
//                        "attachment;filename=\"" + newName + "\"");
//                response.addHeader("Content-Length", "" + newFile.length());
//                toClient.write(buffer);
//                toClient.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (null != is) {
//                        is.close();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        // 删除创建的新文件
//        // this.deleteFile(newFile);
//    }

    /**
     * 复制文件
     *
     * @param s
     *            源文件
     * @param t
     *            复制到的新文件
     */

    public static void fileChannelCopy(File s, File t) {
        try {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(s), 1024);
                out = new BufferedOutputStream(new FileOutputStream(t), 1024);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            } finally {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static String getSispPath() {
//        String classPaths = IExportService.class.getResource("/").getPath();
//        String[] aa = classPaths.split("/");
//        String sispPath = "";
//        for (int i = 1; i < aa.length - 2; i++) {
//            sispPath += aa[i] + "/";
//        }
//        return sispPath;
//    }

    /**
     * 读取excel模板，并复制到新文件中供写入和下载
     *
     * @return
     * @param templateFilePath
     */
    public static File createNewFileFromTemplate(String templateFilePath,String tempFiePath) {
        // 读取模板，并赋值到新文件************************************************************
        // 文件模板路径
//        String path = (getSispPath() + "uploadfile/违法案件报表.xlsx");
        String path = templateFilePath;
        File file = new File(path);
        // 保存文件的路径
        String realPath = tempFiePath;
        // 新的文件名
        String newFileName = System.currentTimeMillis() + ".xlsx";
        // 判断路径是否存在
        File dir = new File(realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 写入到新的excel
        File newFile = new File(realPath, newFileName);
        try {
            newFile.createNewFile();
            // 复制模板到新文件
            fileChannelCopy(file, newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFile;
    }
    private static Workbook create(InputStream in) throws
            IOException,InvalidFormatException {
        if (!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(in)) {
            return new HSSFWorkbook(in);
        }
        if (POIXMLDocument.hasOOXMLHeader(in)) {
            return new XSSFWorkbook(OPCPackage.open(in));
        }
        throw new IllegalArgumentException("你的excel版本目前poi解析不了");
    }
    /**
     * 下载成功后删除
     *
     * @param files
     */
    private void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

}

