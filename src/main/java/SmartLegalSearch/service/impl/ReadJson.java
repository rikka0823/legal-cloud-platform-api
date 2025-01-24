package SmartLegalSearch.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import SmartLegalSearch.vo.ReadJsonVo;

public class ReadJson {

	  /**
     * 讀取 JSON 文件並轉換為 ReadJsonVo
     * 
     * @param filePosition 檔案的絕對路徑
     * @return ReadJsonVo 如果文件正確轉換；否則返回 null
     */
    public static ReadJsonVo readJsonByPath(String filePosition) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        	// 讀取 JSON 檔案並轉換為 ReadJsonVo 類別
        	String rawJson = new String(Files.readAllBytes(Paths.get(filePosition)), "UTF-8");
        	rawJson = rawJson.replaceAll("\\s", "");
            ReadJsonVo data = objectMapper.readValue(rawJson, ReadJsonVo.class);
            return data;
        } catch (IOException e) {
            System.err.println("Error reading JSON file at: " + filePosition);
            e.printStackTrace();
        }
        return null;
    }
	
	


}
