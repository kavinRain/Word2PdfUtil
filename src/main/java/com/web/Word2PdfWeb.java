package com.web;

import com.google.common.collect.Maps;
import com.util.FileUtil;
import com.util.Word2PDFUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Map;

@Controller
public class Word2PdfWeb {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${filePath}")
    private String filePath;

    @PostMapping("upLoadFile")
    @ResponseBody
    public Map<String, String> upLoadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> result = Maps.newHashMap();
        String wordFileName = file.getOriginalFilename();
        String wordFile = String.format("%s%s", filePath, wordFileName);
        String pdfFile = wordFile.replace("docx", "pdf");
        logger.info("上传word文件:{}", wordFile);
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, wordFileName);
            Word2PDFUtil.wordToPDF(wordFile, pdfFile);
            result.put("code", "10000");
            result.put("msg", pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", "10001");
            result.put("msg", String.format("word转pdf失败:%s", e.getMessage()));
        }
        return result;
    }

    @GetMapping("downLoadFile")
    public void downLoadFile(HttpServletRequest request, HttpServletResponse response) {
        int BUFFER_SIZE = 4096;
        InputStream in = null;
        OutputStream out = null;
        try {
            request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            String userName = URLDecoder.decode(request.getHeader("userName"), "UTF-8");
            String password = URLDecoder.decode(request.getHeader("password"), "UTF-8");
            String fileName = URLDecoder.decode(request.getHeader("fileName"), "UTF-8");
            logger.info("参数列表:{}", request.getParameterMap());

            //可以根据传递来的userName和passwd做进一步处理，比如验证请求是否合法等
            File file = new File(fileName);
            response.setContentLength((int) file.length());
            response.setHeader("Accept-Ranges", "bytes");
            int readLength = 0;
            in = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            out = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((readLength = in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }
            out.flush();
            response.addHeader("token", "hello 1");
        } catch (Exception e) {
            e.printStackTrace();
            response.addHeader("token", "hello 2");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
