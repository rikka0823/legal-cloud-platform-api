package SmartLegalSearch.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SmartLegalSearch.entity.CaseId;
import SmartLegalSearch.entity.LegalCase;
import SmartLegalSearch.vo.BasicRes;

@Repository
public interface CaseDao extends JpaRepository<LegalCase, CaseId> {

	/**
	 * 搜尋
	 * 
	 * @param name      模糊搜尋名稱
	 * @param startDate 開始時間
	 * @param endDate   結束時間
	 * @param id        案號
	 * @param charge    法院
	 * @param caseType  案件類型
	 * @param docType   文件類型
	 * @param law       法條
	 * @param courtList 法院陣列
	 * @return Case 陣列
	 */
	public default List<LegalCase> searchByConditions(Connection connection, String name, //
			LocalDate startDate, LocalDate endDate, String id, //
			String charge, String caseType, String docType, //
			List<String> courtList, List<String> lawList) {

		// 1. 撰寫 SQL like 語法(native query)
		String sqlStr = "select group_id, id, court, verdict_date, url, charge, judge_name, " //
				+ " defendant_name, content, content2, law, case_type, doc_type " //
				+ " from legal_case " //
				+ " where content like concat('%', ?, '%') " //
				+ " and verdict_date between ? and ?" // date 在開始時間跟結束時間之間
				+ " and id like concat('%', ?, '%') " //
				+ " and charge like ? " //
				+ " and case_type like ? " //
				+ " and doc_type like ? ";//
		StringBuffer sbf = new StringBuffer(sqlStr);
		// 1.1 串接 concat 中的動態參數
		// 法院
		if (courtList.size() > 0) {
			sbf.append(" and court regexp concat(");
		}
		for (int i = 0; i < courtList.size(); i++) {
			// 當 i "不等於" keywordList 的 size - 1 時
			sbf.append("?");
			if (i != courtList.size() - 1) {
				sbf.append(", '|' ,");
			}
			// 當 i "等於" keywordList 的 size - 1 時
			if (i == courtList.size() - 1) {
				sbf.append(")");
			}
		}
		// 法條
		if (lawList.size() > 0) {
			sbf.append(" and law regexp concat(");
		}
		for (int i = 0; i < lawList.size(); i++) {
			// 當 i "不等於" keywordList 的 size - 1 時
			sbf.append("?");
			if (i != lawList.size() - 1) {
				sbf.append(", '|' ,");
			}
			// 當 i "等於" keywordList 的 size - 1 時
			if (i == lawList.size() - 1) {
				sbf.append(")");
			}
		}
		// 回傳前1000筆
		sbf.append(" limit 500");
		// (預備用)4. 要回傳的 res
		List<LegalCase> res = new ArrayList<>();
		// 2. 透過 PreparedStatement 使用 query regexp 語法並設定對應的參數值
		try (PreparedStatement pstmt = connection.prepareStatement(sbf.toString())) {
			pstmt.setString(1, name);
			pstmt.setDate(2, Date.valueOf(startDate));
			pstmt.setDate(3, Date.valueOf(endDate));
			pstmt.setString(4, id);
			pstmt.setString(5, charge);
			pstmt.setString(6, caseType);
			pstmt.setString(7, docType);
			int courtParamIndex = 8;
			for (String court : courtList) {
				pstmt.setString(courtParamIndex, court);
				courtParamIndex++;
			}
			int lawParamIndex = courtParamIndex;
			for (String law : lawList) {
				pstmt.setString(lawParamIndex, law);
				lawParamIndex++;
			}
			// 3. 執行查詢
			ResultSet resSet = pstmt.executeQuery();
			// 查詢(read)以外的才要提交(commit)
			// 4. 把 ResulySet 中的結果轉換成 list<Atm1>;
			while (resSet.next()) {// resSet.next(): 判斷 resSet 中是否還有東西，若有，會將指針直到下一個目標
				LegalCase legalcase = new LegalCase();
				// 依語法中 select 欄位參數位置決定數字
				legalcase.setGroupId(resSet.getString(1));
				legalcase.setId(resSet.getString(2));
				legalcase.setCourt(resSet.getString(3));
				legalcase.setVerdictDate(resSet.getDate(4).toLocalDate());
				legalcase.setUrl(resSet.getString(5));
				legalcase.setCharge(resSet.getString(6));
				legalcase.setJudgeName(resSet.getString(7));
				legalcase.setDefendantName(resSet.getString(8));
				legalcase.setContent(resSet.getString(9));
				legalcase.setContent2(resSet.getString(10));
				legalcase.setLaw(resSet.getString(11));
				legalcase.setCaseType(resSet.getString(12));
				legalcase.setDocType(resSet.getString(13));
				res.add(legalcase);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	};

	// 全文頁面
	@Query(value = "select group_id, id, court, verdict_date, url, charge, judge_name, defendant_name, "
			+ " content, content2, law, case_type, doc_type from legal_case where group_id = ?1 and id = ?2 and court = ?3", nativeQuery = true)
	public List<LegalCase> getJudgmentIdContentContent2Url(String groupId, String id, String court);

//	@Query(value = "", nativeQuery = true)
//	public List<LegalCase> findByBookmarks(List<BasicRes> bookmarks);
}