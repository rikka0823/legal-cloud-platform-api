package SmartLegalSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import SmartLegalSearch.repository.CaseDao;
import SmartLegalSearch.service.impl.ReadJson;
import SmartLegalSearch.vo.ReadJsonVo;

@SpringBootTest
public class SingleInsetTest {

	@Autowired
	private CaseDao caseDao;

	@Test
	public void test() {
		ReadJsonVo res = ReadJson.readJsonByPath("C:\\Users\\mm312\\Downloads\\臺灣基隆地方法院刑事\\KLDM,112,金訴,606,20240506,1.json");
	}

	private ArrayList<String> readJson1Test(String pattern, ReadJsonVo data) {
		// 整理文章中多餘空格(一般空白、全形空白)跟跳脫符號 : 會沒辦法用 matcher
		String cleanContent = data.getFull().replaceAll("[\\r|\\n|\\s|'　']+", "");

		// 找出判決書中的符合 pattern 的段落
		Pattern lowPattern = Pattern.compile(pattern);
		// 進行比對: .group可取出符合條件的字串段、.start或.end會回傳符合條件的開始位置或結束位置
		Matcher matcher = lowPattern.matcher(cleanContent);
		// 用於蒐集所有符合條件的字串段
		ArrayList<String> lowList = new ArrayList<>();
		// 用於紀錄符合條件的字串段最後一個位置index位置
		int index = 0;
		// 透過迴圈尋找是否有符合條件的內容 (index 為上一個符合條件的文字段中最後一個字在字串中的位置)
		while (matcher.find(index)) {
			// 蒐集所有符合條件的字串段
			lowList.add(matcher.group());
			// 紀錄符合條件的字串段最後一個字在index的位置
			index = matcher.end();
		}
		// 列印出所有蒐集到的內容
//		lowList.forEach(item -> {
//			System.out.println(item);
//		});
		return lowList;
	}

	// 群組案號、唯一案號、審理法院、案由
	private String[] courtAndCharge(ReadJsonVo data) {
		String[] result = new String[4]; // 用於存放案號、法院代號、案由

		// 群組案號
		String idPattern = "([一二三四五六七八九十]|\\d){2,4}年度(.){1,6}字第([一二三四五六七八九十]|\\d){1,5}號";
		String group_id = readJson1Test(idPattern, data).get(0);
		result[0] = group_id;
		// ==========================================
		// 唯一案號的處理
		String jid = data.getId(); // 假設 data.getJID() 返回 JSON 中的 "JID" 欄位
		if (jid != null && !jid.isEmpty()) {
			// 定義更靈活的正則模式，匹配案件類型和日期時間編號
			String uniqueIdPattern = "([一-龥\\w]+),\\s*(\\d+),\\s*(\\d{8}),\\s*(\\d+)$";
			Matcher matcher = Pattern.compile(uniqueIdPattern).matcher(jid);
			if (matcher.find()) {
				String caseType = matcher.group(1); // 案件類型（例如 金訴 或 訴 等）
				String uniqueId = matcher.group(2); // 唯一案號編號
				String date = matcher.group(3); // 日期（例如 20240516）
				String finalNumber = matcher.group(4); // 最後的數字編號（例如 1 或 2）
				// 在文中組合查找對應的唯一案號
				String specificCasePattern = "([一二三四五六七八九十]|\\d){2,4}年度" + caseType + "字第" + uniqueId + "號";
				List<String> matchedCases = readJson1Test(specificCasePattern, data);
				if (!matchedCases.isEmpty()) {
					result[1] = matchedCases.get(0); // 取匹配到的第一個案號
				} else {
					result[1] = "未找到符合的唯一案號";
				}
			} else {
				result[1] = "未能從 JID 提取唯一案號相關資訊";
			}
		} else {
			result[1] = "JID 欄位為空";
		}

		// ==========================================
		// 審理法院
		String court = data.getId().substring(0, 3);
		result[2] = court;

		// 案由
		String charge = data.getTitle();
		result[3] = charge;

		return result; // 回傳結果
	}
}
