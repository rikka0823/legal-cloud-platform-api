package SmartLegalSearch.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = "")
@RequestMapping("/api/")
public class SmartLegalSearchController {

    @GetMapping("hello")
    public String getHello()



    {
        return "{\"message\": \"Hello from Java\"}";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadJson(@RequestBody Map<String, Object> jsonData) {
        // 輸出接收到的 JSON 資料
        System.out.println("接收到的 JSON 資料：" + jsonData);

        // 範例：提取某些欄位進行處理
        String jid = (String) jsonData.get("JID");
        String title = (String) jsonData.get("JTITLE");
        System.out.println("案件 ID: " + jid + ", 標題: " + title);

        // 回應成功訊息
        return ResponseEntity.ok("JSON 資料接收成功！");
    }
}
