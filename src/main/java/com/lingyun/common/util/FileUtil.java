package com.lingyun.common.util;


import com.lingyun.entity.User;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-11-8
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {
    public static void writeFile(String string,String filePath) throws IOException {
        String content = string;
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();

    }
    public static void fileDownload(HttpServletResponse response, String filePath) throws IOException {
        // path是指欲下载的文件的路径。
        File file = new File(filePath);
        // 取得文件名。
        String filename = file.getName();
        // 取得文件的后缀名。
        //String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

        // 以流的形式下载文件。
        InputStream fis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        // 清空response
        response.reset();
        // 设置response的Header
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("utf-8"), "ISO-8859-1"));
        response.addHeader("Content-Length", "" + file.length());
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        toClient.write(buffer);
        toClient.flush();
        toClient.close();

    }
    public static File createfile(String path) {
        File file = new File(path);
        return file;
    }

    public static boolean isExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static InputStream getFileInputStream(String path) {
        try {
            if (isExist(path)) {
                InputStream is = new FileInputStream(path);
                return is;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 把文本编码为Html代码
     *
     * @param target
     * @return 编码后的字符串
     */
    public static String htmEncode(String target) {
        StringBuffer stringbuffer = new StringBuffer();
        int j = target.length();
        for (int i = 0; i < j; i++) {
            char c = target.charAt(i);
            switch (c) {
                case 60:
                    stringbuffer.append("&lt;");
                    break;
                case 62:
                    stringbuffer.append("&gt;");
                    break;
                case 38:
                    stringbuffer.append("&amp;");
                    break;
                case 34:
                    stringbuffer.append("&quot;");
                    break;
                case 169:
                    stringbuffer.append("&copy;");
                    break;
                case 174:
                    stringbuffer.append("&reg;");
                    break;
                case 165:
                    stringbuffer.append("&yen;");
                    break;
                case 8364:
                    stringbuffer.append("&euro;");
                    break;
                case 8482:
                    stringbuffer.append("&#153;");
                    break;
                case 13:
                    if (i < j - 1 && target.charAt(i + 1) == 10) {
                        stringbuffer.append("<br>");
                        i++;
                    }
                    break;
                case 32:
                    if (i < j - 1 && target.charAt(i + 1) == ' ') {
                        stringbuffer.append(" &nbsp;");
                        i++;
                        break;
                    }
                default:
                    stringbuffer.append(c);
                    break;
            }
        }
        return new String(stringbuffer.toString());
    }







    public static byte[] getBytesFromFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);

// 获取文件大小

        long length = file.length();

        if (length > Integer.MAX_VALUE) {

            // 文件太大，无法读取

            throw new IOException("File is to large " + file.getName());

        }

// 创建一个数据来保存文件数据

        byte[] bytes = new byte[(int) length];

// 读取数据到byte数组中

        int offset = 0;

        int numRead = 0;

        while (offset < bytes.length

                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {

            offset += numRead;

        }

// 确保所有数据均被读取

        if (offset < bytes.length) {

            throw new IOException("Could not completely read file " + file.getName());

        }

// Close the input stream and return bytes

        is.close();

        return bytes;

    }



    public static void main(String[] arg) {


    }
}
