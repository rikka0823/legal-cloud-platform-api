package SmartLegalSearch;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import SmartLegalSearch.service.ifs.CaseService;
import SmartLegalSearch.vo.SearchReq;
import SmartLegalSearch.vo.SearchRes;

@SpringBootTest
public class CaseServiceTest {

	@Autowired
	private CaseService caseService;

	@Test
	public void searchTest() {
		SearchReq req = new SearchReq();
		// 全部搜尋
		SearchRes res = caseService.searchCriminalCase(req);
		System.out.println(res.getCaseList().size());

		// 測試開始時間比結束時間晚
//		req.setVerdictStartDate(LocalDate.of(2024, 05, 30));
//		req.setVerdictEndDate(LocalDate.of(2024, 05, 10));
//		res = caseService.searchCriminalCase(req);
//		Assert.isTrue(res.getMessage().equalsIgnoreCase(ResMessage.DATE_ERROR.getMessage()), "時間順序測試失敗");
//		System.out.println("時間順序測試成功");
//		
		// 搜尋測試
		// 模糊搜尋名稱
//		req.setSearchName("客戶歷史交易清單"); 
		// id
//		req.setVerdictId("111年度金訴字第323號");
		// 開始時間
//		req.setVerdictStartDate(LocalDate.of(2024, 05, 1));
		// 結束時間
//		req.setVerdictEndDate(LocalDate.of(2024, 05, 9));
		// 案由
//		req.setCharge("違反洗錢防制法等");
		// 案件類型
//		req.setCaseType("刑事");
		// 文件類型
//		req.setDocType("判決");
		// 法條
//		req.setLawList(List.of("洗錢防制法第14條", "刑法第13條第1項"));
		// 法院
//		req.setCourtList(List.of("SLD", "AAA"));
		//

		res = caseService.searchCriminalCase(req);
		System.out.println(res.getCaseList().size());
	}
}
