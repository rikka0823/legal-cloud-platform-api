package SmartLegalSearch.controller;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import SmartLegalSearch.constants.ResMessage;
import SmartLegalSearch.service.ifs.CaseService;
import SmartLegalSearch.vo.SearchReq;
import SmartLegalSearch.vo.SearchRes;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("case/")
@RestController
@Controller
public class CaseServiceController {

	@Autowired
	private CaseService caseService;

	// 搜尋功能
	@PostMapping(value = "search")
	public SearchRes searchCriminalCase(@RequestBody SearchReq req) {
		// 模糊搜尋
		if (!StringUtils.hasText(req.getSearchName())) {
			req.setSearchName("");
		}

		// 裁判字號
		if (!StringUtils.hasText(req.getVerdictId())) {
			req.setVerdictId("%");
		}

		// 開始時間
		if (req.getVerdictStartDate() == null) {
			req.setVerdictStartDate(LocalDate.of(1950, 1, 1));
		}

		// 結束時間
		if (req.getVerdictEndDate() == null) {
			req.setVerdictEndDate(LocalDate.of(9999, 12, 31));
		}

		// 確認開始時間不能比結束時間晚
		if (req.getVerdictStartDate().isAfter(req.getVerdictEndDate())) {
			return new SearchRes(ResMessage.DATE_ERROR.getCode(), //
					ResMessage.DATE_ERROR.getMessage());
		}

		// 案由
		if (!StringUtils.hasText(req.getCharge())) {
			req.setCharge("%");
		}

		// 案件類型
		if (!StringUtils.hasText(req.getCaseType())) {
			req.setCaseType("%");
		}

		// 文件類型
		if (!StringUtils.hasText(req.getDocType())) {
			req.setDocType("%");
		}

		// 法條
		if (CollectionUtils.isEmpty(req.getLawList())) {
			req.setLawList(new ArrayList<>());
		}

		// 法院
		if (CollectionUtils.isEmpty(req.getCourtList())) {
			req.setCourtList(new ArrayList<>());
		}
		
		return caseService.searchCriminalCase(req);
	};

	
	@GetMapping(value = "judgmentid")
	public SearchRes getJudgmentIdContentContent2Url(@RequestParam("groupId") String groupId, @RequestParam("id") String id, @RequestParam("court") String court) {
		return caseService.JudgmentidText(groupId, id, court);
	}
}
