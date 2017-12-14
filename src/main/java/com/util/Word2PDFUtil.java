package com.util;

/**
 * @author qhwang
 * @date 2017-11-02 下午7:19
 */

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Word转PDF工具类，利用docx4j-export-fo工具提供的功能实现
 * 比poi的实现方式简洁很多
 */
public class Word2PDFUtil {

    private static final Logger logger = LoggerFactory.getLogger(Word2PDFUtil.class);

    public static void wordToPDF(String sfileName, String toFileName) {
        logger.info("启动Word...");
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            app = new ActiveXComponent("Word.Application");
            // 设置word不可见
            app.setProperty("Visible", new Variant(false));
            // 打开word文件
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[]{
                    sfileName, new Variant(false), new Variant(true)}, new int[1]).toDispatch();
            logger.info("打开文档..." + sfileName);
            logger.info("转换文档到PDF..." + toFileName);
            File tofile = new File(toFileName);
            if (tofile.exists()) {
                tofile.delete();
            }
//         // 作为html格式保存到临时文件：：参数 new Variant(8)其中8表示word转html;7表示word转txt;44表示Excel转html;17表示word转成pdf。。
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{
                    toFileName, new Variant(17)}, new int[1]);
            long end = System.currentTimeMillis();
            logger.info("转换完成..用时：" + (end - start) + "ms.");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("word转换pdf出错：errorMsg = " + e, e);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // 关闭word
            Dispatch.call(doc, "Close", false);
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[]{});
        }
        //如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
    }

}
