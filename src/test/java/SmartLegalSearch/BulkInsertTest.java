package SmartLegalSearch;

import SmartLegalSearch.entity.LegalCase;
import SmartLegalSearch.repository.CaseDao;
import SmartLegalSearch.service.impl.ReadJson;
import SmartLegalSearch.vo.ReadJsonVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest
public class BulkInsertTest {

	@Autowired
	private CaseDao caseDao;

	// 檔案資料夾
//	private String folderPath = "D:\\JavaProject\\202405\\臺灣臺中地方法院刑事"; // 替換為實際目錄路徑
//	private String folderPath = "C:\\Users\\mm312\\Downloads\\臺灣基隆地方法院刑事"; // 子路由遍讀歷 路徑
	private String folderPath = "C:\\Users\\mm312\\Downloads\\202405"; // 子路由遍讀歷 路徑
	// 單筆測試
//	private String folderPath = "D:\\JavaProject\\202405\\臺灣臺中地方法院刑事\\TCDM,110,附民,460,20240529,1.json"; // 替換為實際目錄路徑

	@Test
	public void test() throws IOException {
		// 初始化 ReadJson 和保存結果的列表
		List<ReadJsonVo> dataList = new ArrayList<>();

		// 遍歷目錄中的所有 JSON 檔案
		Files.walk(new File(folderPath).toPath()).filter(Files::isRegularFile) // 只處理檔案
				.filter(path -> path.toString().endsWith(".json")) // 篩選 JSON 檔案
				.forEach(file -> {
					// 讀取 JSON 文件，並添加到 dataList
					ReadJsonVo data = ReadJson.readJsonByPath(file.toAbsolutePath().toString());
					if (data != null) {
						dataList.add(data);
					}
				});

		// 以正規化取關鍵字
		List<LegalCase> legalCaseList = new ArrayList<>();
		for (ReadJsonVo data : dataList) {
			String contentStr = data.getFull();
			if (!StringUtils.hasText(contentStr)) {
				continue;
			}
			LegalCase legalCase = new LegalCase();
			String[] courtAndCharge = courtAndCharge(data);

			System.out.println(data.getId());
			// id 案號
			legalCase.setGroupId(courtAndCharge[1]);
			legalCase.setId(courtAndCharge[1]);

			// 法院代碼
			legalCase.setCourt(courtAndCharge[2]);

			// 案由
			legalCase.setCharge(courtAndCharge[3]);

			// 判決日期
			legalCase.setVerdictDate(verdictDate(data));

			// 判決書連結
			legalCase.setUrl(httpUrl(data));

			// 判決內容
			legalCase.setContent(mainContent(contentStr));

			// 判決內容(附表以下)
			legalCase.setContent2(mainContent2(contentStr));

			// 被告姓名
			legalCase.setDefendantName(DefendantName(contentStr));

			// 法官姓名
			legalCase.setJudgeName(JudgesName(contentStr));

			// 相關法條
			legalCase.setLaw(extractAllLaws(contentStr));

			// 案件類型，如刑事、民事、行政
			legalCase.setCaseType(CaseType(data));

			// 文件類型，裁定或判決或釋字等
			legalCase.setDocType(DocType(contentStr));

			legalCaseList.add(legalCase);
		}
		caseDao.saveAll(legalCaseList);
	}

// 	處理內容正規化表達===========================================================================
	private ArrayList<String> searchAndGetInPattern(String pattern, ReadJsonVo data) {
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

	// 中文數字轉數字
	private int convertChineseToArabic(String chineseNumber) {
		// 如果輸入是純數字，直接返回其整數值
		if (chineseNumber.matches("\\d+")) {
			return Integer.parseInt(chineseNumber);
		}
		int result = 0; // 最終的結果
		int temp = 0; // 暫時累積的部分
		// 處理「百」、「十」、「個位數」
		if (chineseNumber.contains("百")) {
			String[] parts = chineseNumber.split("百");
			if (!StringUtils.hasText(chineseNumber)) {
				temp = parts[0].isEmpty() ? 1 : processDigits(parts[0]);
				result += temp * 100;
				chineseNumber = parts.length > 1 ? parts[1] : "";
			}
		}
		if (chineseNumber.contains("十")) {
			String[] parts = chineseNumber.split("十");
			if (!StringUtils.hasText(chineseNumber)) {
				temp = parts[0].isEmpty() ? 1 : processDigits(parts[0]);
				result += temp * 10;
				chineseNumber = parts.length > 1 ? parts[1] : "";
			}
		}
		if (!chineseNumber.isEmpty()) {
			result += processDigits(chineseNumber);
		}
		return result;
	}

	// 處理剩餘的數字部分
	private int processDigits(String digits) {
		int value = 0;
		// 中文數字轉換映射
		Map<String, Integer> number = Map.of("一", 1, "二", 2, "三", 3, "四", 4, "五", 5, "六", 6, "七", 7, "八", 8, "九", 9);
		for (int i = 0; i < digits.length(); i++) {
			String currentChar = digits.substring(i, i + 1);
			if (number.containsKey(currentChar)) {
				value += number.get(currentChar);
			}
		}
		return value;
	}

	// 方法：將文件內文中的中文數字轉換為阿拉伯數字
	private String convertTextChineseNumbers(String text) {
		// 匹配中文數字的正則表達式（針對 "第十四條" 和 "第十四項" 等格式）
		Pattern pattern = Pattern.compile("第([一二三四五六七八九十百零]+)(條|項)");
		Matcher matcher = pattern.matcher(text);
		// 用於存放轉換後的結果
		StringBuffer result = new StringBuffer();
		// 遍歷匹配的中文數字
		while (matcher.find()) {
			String chineseNumber = matcher.group(1); // 提取中文數字部分
			int arabicNumber = convertChineseToArabic(chineseNumber); // 轉換為阿拉伯數字
			String replacement = "第" + arabicNumber + matcher.group(2); // 組合成替換後的字符串
			matcher.appendReplacement(result, replacement); // 替換匹配到的部分
		}
		matcher.appendTail(result); // 添加剩餘部分
		return result.toString();
	}

//	=======================================================================================

	// 群組案號、唯一案號、審理法院、案由
	private String[] courtAndCharge(ReadJsonVo data) {
		String[] result = new String[4]; // 用於存放案號、法院代號、案由

		// 群組案號
		String idPattern = "([一二三四五六七八九十]|\\d){2,4}年度(.){1,6}字第([一二三四五六七八九十]|\\d){1,5}號";
		// 確認正規迴圈有找到東西，如果沒找到，group_id 會保持 null
		String group_id = null;
		ArrayList<String> check = searchAndGetInPattern(idPattern, data);
		if (!CollectionUtils.isEmpty(check)) {
			group_id = check.get(0);
		}
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
//					String date = matcher.group(3); // 日期（例如 20240516）
//					String finalNumber = matcher.group(4); // 最後的數字編號（例如 1 或 2）
				// 在文中組合查找對應的唯一案號
				String specificCasePattern = "([一二三四五六七八九十]|\\d){2,4}年度" + caseType + "字第" + uniqueId + "號";
				List<String> matchedCases = searchAndGetInPattern(specificCasePattern, data);
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

	// 判決日期
	private LocalDate verdictDate(ReadJsonVo data) {
		// 匹配日期段落
		String pattern = "中\\s*華\\s*民\\s*國\\s*([一二三四五六七八九十零百千\\d]{1,4})\\s*年\\s*([一二三四五六七八九十零\\d]{1,2})\\s*月\\s*([一二三四五六七八九十零\\d]{1,2})\\s*日";
		ArrayList<String> dateStrList = searchAndGetInPattern(pattern, data);

		if (dateStrList.isEmpty()) {
//			throw new IllegalArgumentException("未找到符合格式的判決日期！: " + data.getId());
			return null;
		}

		try {
			String firstDateStr = dateStrList.get(0).replaceAll("中\\s*華\\s*民\\s*國\\s*|\\s+", "");
			String[] dateParts = firstDateStr.split("年|月|日");
			int year = convertChineseToArabic(dateParts[0]) + 1911;
			int month = convertChineseToArabic(dateParts[1]);
			int day = convertChineseToArabic(dateParts[2]);

			// 範圍檢查
			if (year < 2000 || year > LocalDate.now().getYear() || month < 1 || month > 12 || day < 1 || day > 31) {
				firstDateStr = dateStrList.get(1).replaceAll("中\\s*華\\s*民\\s*國\\s*|\\s+", "");
				dateParts = firstDateStr.split("年|月|日");
				year = convertChineseToArabic(dateParts[0]) + 1911;
				month = convertChineseToArabic(dateParts[1]);
				day = convertChineseToArabic(dateParts[2]);
//				throw new IllegalArgumentException("日期超出範圍: " + firstDateStr);
			}

			// 回傳 LocalDate 物件
			return LocalDate.of(year, month, day);
		} catch (Exception e) {
			return null;
//			throw new RuntimeException("解析判決日期時發生錯誤: " + e.getMessage());
		}
	}

	// 提取全文中所有法條的方法
	private String extractAllLaws(String fullText) {
		List<String> laws = new ArrayList<>(); // 用於存放匹配到的法條

		// 清理空白字元並處理中文數字
		String cleanText = convertTextChineseNumbers(fullText.replaceAll("\\s+", ""));

		// 定義正則模式並匹配全文
		Pattern lawPattern = Pattern.compile("(洗錢防制法|毒品危害防制條例|陸海空軍刑法|煙害防制法|貪污治罪條例|山坡地保育利用條例|銀行法|刑法)第\\d+條(第\\d+項)?");
		Matcher matcher = lawPattern.matcher(cleanText);

		// 匹配所有符合的法條並存入列表
		while (matcher.find()) {
			laws.add(matcher.group());
		}
		String lawString = laws.stream().collect(Collectors.joining(";"));

		return lawString; // 返回所有匹配到的法條
	}

	// 被告姓名
	private String DefendantName(String fullText) {
//		String cleanedText = fullText.replaceAll("[\\s ]+", "");
//		System.out.println(fullText);
		// 正則表達式匹配多種角色名稱，並適配正規化後的文本
		Pattern pattern = Pattern.compile(//
				"受刑人\\s*([\\p{IsHan}○]{2,4}|[a-zA-Z]+(?: [a-zA-Z]+)*)|" + //
						"被告\\s*([\\p{IsHan}○]{2,4}|[a-zA-Z]+(?: [a-zA-Z]+)*)|" + //
						"被移送人\\s*([\\p{IsHan}○]{2,4}|[a-zA-Z]+(?: [a-zA-Z]+)*)|" + //
						"扣押人\\s*([\\p{IsHan}○]{2,4}|[a-zA-Z]+(?: [a-zA-Z]+)*)|" + //
						"即被告\\s*([\\p{IsHan}○]{2,4}|[a-zA-Z]+(?: [a-zA-Z]+)*)|" + //
						"受處分人\\s*([\\p{IsHan}○]{2,4}|[a-zA-Z]+(?: [a-zA-Z]+)*)"//
		);

		// 搜索匹配
		Matcher matcher = pattern.matcher(fullText);

		if (matcher.find()) {
			// 判斷哪個捕獲群組匹配成功
			for (int i = 1; i <= 6; i++) { // 共有 6 個捕獲群組
				if (matcher.group(i) != null) {
					return matcher.group(i).trim();
				}
			}
		}
		return "未知";
	}

	// 法官姓名
	private String JudgesName(String fullText) {
		// 標準化文本，移除多餘的空格（全形與半形）
		String cleanedText = fullText.replaceAll("[\\s ]+", " ").replaceAll("\\s+", "").replaceAll("　", "");
		// 定義匹配日期的正則模式 中華民國 年 月 日刑事 格式，後方 刑事可再追加其他類型
		Pattern datePattern = Pattern.compile("華\\s*民\\s*國\\s*\\d+\\s*年\\s*\\d+\\s*月\\s*\\d+\\s*日(刑事)");
		Matcher dateMatcher = datePattern.matcher(cleanedText);

		// 如果找到日期
		if (dateMatcher.find()) {
			// 截取日期後的文本
			String textAfterDate = cleanedText.substring(dateMatcher.end()).trim();
			// 定義匹配法官姓名的正則模式
			Pattern judgePattern = Pattern.compile("法\\s*官\\s*([\\u4E00-\\u9FFF]{2,3})");
			Matcher judgeMatcher = judgePattern.matcher(textAfterDate);

			// 如果找到法官姓名
			if (judgeMatcher.find()) {
				return judgeMatcher.group(1).trim();
			} else {
				System.err.println(fullText);
				System.err.println("未找到法官姓名");
				return "未知";
			}
		} else {
			// 中華民國 年 月 日刑事 的格式找不到日期的情況
			// 第二種日期匹配模式 中華民國 年 月 日
			Pattern datePattern2 = Pattern.compile("中\\s*華\\s*民\\s*國\\s*\\d+\\s*年\\s*\\d+\\s*月\\s*\\d+\\s*日");
			Matcher dateMatcher2 = datePattern2.matcher(cleanedText);

			if (dateMatcher2.find()) {
				String textAfterDate2 = cleanedText.substring(dateMatcher2.end()).trim();
				Pattern judgePattern = Pattern.compile("法\\s*官\\s*([\\u4E00-\\u9FFF]{2,3})");
				Matcher judgeMatcher = judgePattern.matcher(textAfterDate2);

				if (judgeMatcher.find()) {
					return judgeMatcher.group(1).trim();
				} else {
					System.err.println(fullText);
					System.err.println("未找到法官姓名");
					return "未知";
				}
			} else {
				System.err.println("未找到日期模式");
				return null;
			}
		}
	}

	// 案件類型，如刑事(M)、民事(V)、行政(A)
	private String CaseType(ReadJsonVo data) {
		String jid = data.getId();
		if (!jid.isEmpty() & jid != null) {
			String uniqueIdPattern = "^([一-龥\\w]*([MVAPC]))";
			Matcher matcher = Pattern.compile(uniqueIdPattern).matcher(jid);
			if (matcher.find()) {
				String caseType = matcher.group(2);
				// 刑事 : M 民事 : V 行政 : A 懲戒 : P 審裁 : C
				if (caseType.equals("M")) {
					return "刑事";
				}
				if (caseType.equals("V")) {
					return "民事";
				}
				if (caseType.equals("A")) {
					return "行政";
				}
				if (caseType.equals("P")) {
					return "懲戒";
				}
				if (caseType.equals("C")) {
					return "審裁";
				}
			}
		}
		return "未知";
	}

	// 文件類型，裁定或判決或釋字等
	private String DocType(String fullText) {

		// 正規表示式：匹配包含法院名稱和「判決」、「裁定」、「釋字」的段落
		String patternStr = "(\\S*法院\\S*)\\s*(刑事|民事|行政)?\\s*(判決|裁定|釋字)";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(fullText);
		// 檢查是否找到匹配
		if (matcher.find()) {
			// 找到標記「判決、裁定、釋字」
			String docType = matcher.group(3); // 判決/裁定/釋字
			// 拼接結果並返回
			return docType;
		}
		return "未知";
	}

	// URL 生成
	private String httpUrl(ReadJsonVo data) {
		// 假設 data.getId() 返回的 id 字串
		String id = data.getId();
		// 替換逗號為 URL 兼容格式
		String encodedId = id.replace(",", "%2c"); // 處理逗號
		return "https://judgment.judicial.gov.tw/FJUD/data.aspx?ty=JD&id=" + encodedId;
	}

	// 附表之前的內容
	private String mainContent(String fullText) {
		// 匹配「中華民國 年 月 日」
		Pattern datePattern = Pattern.compile("中[\\s\u3000]*華[\\s\u3000]*民[\\s\u3000]*國[\\s\u3000]*\\d+[\\s\u3000]*年[\\s\u3000]*\\d+[\\s\u3000]*月[\\s\u3000]*\\d+[\\s\u3000]*日");
	    Matcher dateMatcher = datePattern.matcher(fullText);
	    if (dateMatcher.find()) {
	        // 截取匹配日期之前的文本
	        return fullText.substring(0, dateMatcher.start()).trim();
	    }
	    return null; // 如果找不到匹配，返回 null
	}

	// 附錄後內容
	private String mainContent2(String fullText) {
		// 匹配「中華民國 年 月 日」
		Pattern datePattern = Pattern.compile("中[\\s\u3000]*華[\\s\u3000]*民[\\s\u3000]*國[\\s\u3000]*\\d+[\\s\u3000]*年[\\s\u3000]*\\d+[\\s\u3000]*月[\\s\u3000]*\\d+[\\s\u3000]*日");
		Matcher dateMatcher = datePattern.matcher(fullText);
//		System.out.println(fullText);
		if (dateMatcher.find()) {
			// 截取匹配日期之後的文本
			return fullText.substring(dateMatcher.end()).trim();
		}
		return null; // 如果找不到匹配，返回 null
	}
}
