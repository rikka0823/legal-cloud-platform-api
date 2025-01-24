package SmartLegalSearch.service.ifs;

import SmartLegalSearch.vo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AccountSystemService {

    // 註冊
    RegisterRes register(RegisterReq req);

    // 根據 token 驗證 email
    ResponseEntity<String> verifyEmail(String token);

    // 登入
    BasicRes login(LoginReq req, HttpSession session);

    // 更新用戶資訊
    BasicRes updateProfile(UpdateProfileReq req, HttpSession session);

    // 更新用戶頭像
    BasicRes updateProfilePicture(String email, MultipartFile profilePicture, HttpSession session);

    // 登出
    BasicRes logout(HttpSession session);

    // 忘記密碼，寄信驗證
    BasicRes forgotPassword(ForgotPasswordReq req, HttpSession session);

    // 驗證重設密碼 token
    ResponseEntity<String> verifyPasswordResetToken(String token);

    // 重置密碼
    BasicRes resetPassword(ResetPasswordReq req, HttpSession session);

    // 獲取用戶資訊
    UserInfoRes getUserInfo(ForgotPasswordReq req, HttpSession session);

    // 獲取用戶頭像
    ResponseEntity<byte[]> getProfilePicture(ForgotPasswordReq req, HttpSession session);

    // 刪除用戶資料
    BasicRes deleteUser(DeleteAccountReq req, HttpSession session);
    
    // 儲存書籤
    BasicRes seveBookmark(BookmarkReq req);
    
    // 刪除書籤
    BasicRes deleteBookmark(BookmarkReq req);
    
    // 刪除螢光筆
    BasicRes deleteHighlighte(HighlighteReq req);
    
    // 取得單筆書籤
    BasicRes getBookmarkAlreadyExists(String email, String groupId, String id, String court);
    
    // 儲存螢光筆判決書資料
    BasicRes seveHighlighte(HighlighteReq req);
    
    // 取得該email的書籤
    BasicRes getEmailBookmark(String email);
    
    // 取得該email的螢光筆書籤
    BasicRes getEmailHighlighte(String email);
    
    // 取得全文頁面螢光筆書籤
    BasicRes getHighlighteAlreadyExists(String email, String groupId, String id, String court);

    // 取得判決書部分內容
    SearchRes getemailJudgmentidText(String groupId, String id, String court);
}