package SmartLegalSearch.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import SmartLegalSearch.constants.ResMessage;
import SmartLegalSearch.entity.LegalCase;
import SmartLegalSearch.repository.CaseDao;
import SmartLegalSearch.service.ifs.CaseService;
import SmartLegalSearch.vo.SearchReq;
import SmartLegalSearch.vo.SearchRes;
import jakarta.transaction.Transactional;

@Service
public class CaseImpl implements CaseService {

	@Autowired
	private CaseDao caseDao;

	// 動態資料查詢用
	@Autowired
	private DataSource dataSource;

	@Cacheable(cacheNames = "case_search", //
			key = "#p0.searchName + '-' + #p0.verdictStartDate.toString() "//
					+ "+ '-' + #p0.verdictEndDate.toString() + '-' + #p0.verdictId "
					+ "+ '-' + #p0.charge + '-' + #p0.caseType " //
					+ "+ '-' + #p0.docType + '-' + #p0.courtList.toString() " //
					+ "+ '-' + #p0.lawList.toString()", //
			unless = "#result.code != 200")
	@Override
	public SearchRes searchCriminalCase(SearchReq req) {
		// 為了cache，資料驗證改至 controller 執行
		List<LegalCase> res = new ArrayList<>();
		try (Connection connection = dataSource.getConnection()) {
			res = caseDao.searchByConditions(connection, req.getSearchName(), req.getVerdictStartDate(),
					req.getVerdictEndDate(), //
					req.getVerdictId(), req.getCharge(), req.getCaseType(), //
					req.getDocType(), req.getCourtList(), req.getLawList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new SearchRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), res) //
		;
	}

	@Transactional
	@Override
	public LegalCase saveJudgment(LegalCase res) {
		caseDao.save(res);
		return null;
	}
//	取得判決書內容
	@Override
	public SearchRes JudgmentidText(String groupId, String id, String court) {
		List<LegalCase> res = new ArrayList<>();
		res = caseDao.getJudgmentIdContentContent2Url(groupId, id, court);
		return new SearchRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), res);
	}
	
	
	

}
