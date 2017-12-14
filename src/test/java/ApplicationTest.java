import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.util.HttpFileClientUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Test
    public void testUploadFile() {
        Map<String, String> uploadParams = Maps.newLinkedHashMap();
        uploadParams.put("fileContentType", "file");
        uploadParams.put("fileName", String.format("%s.docx", System.currentTimeMillis()));
        try {
            String resultStr = HttpFileClientUtil.getInstance().uploadFileImpl(
                    "http://localhost:7761/upLoadFile", "C:/Users/lenovo/Desktop/应收账款融资申请书--（正保理-供应商）.docx",
                    "file", null);
            Map<String, String> resultMap = JSON.parseObject(resultStr, Map.class);
            if (resultMap.get("code").equals("10000")) {
                Map<String, String> params = Maps.newHashMap();
                params.put("userName", "admin");
                params.put("password", "admin");
                params.put("fileName", resultMap.get("msg"));
                String path = "C:/Users/lenovo/Desktop/";
                String filePath = String.format("%s%s.pdf", path, System.currentTimeMillis());
                HttpFileClientUtil.getInstance().download("http://localhost:7761/downLoadFile", filePath, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
