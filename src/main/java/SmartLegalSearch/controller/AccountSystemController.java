package SmartLegalSearch.controller;

import SmartLegalSearch.repository.AccountSystemDao;
import SmartLegalSearch.service.ifs.AccountSystemService;
import SmartLegalSearch.vo.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:4200")
@Validated
@RequestMapping("/accountSystem")
@RestController
public class AccountSystemController {

	@Autowired
	private AccountSystemService accountSystemService;

	@Autowired
	private AccountSystemDao accountSystemDao;

	// 註冊的路徑為 /accountSystem/register
	@PostMapping("register")
	public RegisterRes register(@Valid @RequestBody RegisterReq req) {
		return accountSystemService.register(req);
	}

	// 驗證 email 的路徑為 /accountSystem/verify-email
	@GetMapping("verify-email")
	public ResponseEntity<String> verifyEmail(@RequestParam(name = "token") String token) {
		return accountSystemService.verifyEmail(token);
	}

	// 登入的路徑為 /accountSystem/login
	@PostMapping("login")
	public BasicRes login(@Valid @RequestBody LoginReq req, HttpSession session) {
		return accountSystemService.login(req, session);
	}

	// 更新用戶資訊的路徑為 /accountSystem/update-profile
	@PostMapping("update-profile")
	public BasicRes updateProfile(@Valid @RequestBody UpdateProfileReq req, HttpSession session) {
		return accountSystemService.updateProfile(req, session);
	}

	// 更新用戶頭像的路徑為 /accountSystem/update-profile-picture?email=
	@PostMapping("update-profile-picture")
	public BasicRes updateProfile(
			@RequestParam(name = "email") @NotBlank(message = "Email cannot be blank.") @Email(message = "Invalid email format.") String email,
			@RequestParam(name = "profilePicture") MultipartFile profilePicture, HttpSession session) {
		return accountSystemService.updateProfilePicture(email, profilePicture, session);
	}

	// 登出的路徑為 /accountSystem/logout
	@PostMapping("logout")
	public BasicRes logout(HttpSession session) {
		return accountSystemService.logout(session);
	}

	// 忘記密碼的路徑為 /accountSystem/forgot-password
	@PostMapping("forgot-password")
	public BasicRes forgetPassword(@Valid @RequestBody ForgotPasswordReq req, HttpSession session) {
		return accountSystemService.forgotPassword(req, session);
	}

	// 驗證重設密碼 token 的路徑為 /accountSystem/verify-password-reset-token
	@GetMapping("verify-password-reset-token")
	public ResponseEntity<String> verifyPasswordResetToken(@RequestParam(name = "token") String token) {
		return accountSystemService.verifyPasswordResetToken(token);
	}

	// 重置密碼的路徑為 /accountSystem/reset-password
	@PostMapping("reset-password")
	public BasicRes resetPassword(@Valid @RequestBody ResetPasswordReq req, HttpSession session) {
		return accountSystemService.resetPassword(req, session);
	}

	// 獲取用戶資訊的路徑為 /accountSystem/get-user-info
	@PostMapping("get-user-info")
	public UserInfoRes getUserInfo(@Valid @RequestBody ForgotPasswordReq req, HttpSession session) {
		return accountSystemService.getUserInfo(req, session);
	}

	// 獲取用戶頭像的路徑為 /accountSystem/get-profile-picture
	@PostMapping("get-profile-picture")
	public ResponseEntity<byte[]> getProfilePicture(@Valid @RequestBody ForgotPasswordReq req, HttpSession session) {
		return accountSystemService.getProfilePicture(req, session);
	}

	// 刪除用戶資料的路徑為 /accountSystem/delete-user
	@PostMapping("delete-user")
	public BasicRes deleteUser(@Valid @RequestBody DeleteAccountReq req, HttpSession session) {
        System.out.println(req);
		return accountSystemService.deleteUser(req, session);
	}

	// 儲存書籤 /accountSystem/bookmark
	@PostMapping("bookmark")
	public BasicRes seveBookmark(@RequestBody BookmarkReq req) {
		return accountSystemService.seveBookmark(req);
	}

	// 儲存螢光筆文字判決書為 /accountSystem/seve-highlighte
	@PostMapping("seve-highlighte")
	public BasicRes seveHighlighte(@RequestBody HighlighteReq req) {
		return accountSystemService.seveHighlighte(req);
	}

	// 刪除螢光筆單筆 /accountSystem/delete-highlighte
	@PostMapping("delete-highlighte")
	public BasicRes deleteHighlighte(@RequestBody HighlighteReq req) {
		return accountSystemService.deleteHighlighte(req);
	};

	// 用 email get 所有屬於該 email 的書籤 /accountSystem/email-all-bookmark
	@GetMapping("email-all-bookmark")
	public BasicRes getEmilAllBookmark(@RequestParam("email") String email) {
		return accountSystemService.getEmailBookmark(email);
	}

	// 刪除該 email 的單筆書籤 全文頁面 /accountSystem/delete-bookmark
	@PostMapping("delete-bookmark")
	public BasicRes deleteBookmark(@Valid @RequestBody BookmarkReq req) {
		return accountSystemService.deleteBookmark(req);
	};

	// 用email get email 的單筆書籤 /accountSystem/bookmark-already-exists
	@GetMapping("bookmark-already-exists")
	public BasicRes getEmilBookmarkAlreadyExists(@RequestParam("email") String email,
			@RequestParam("groupId") String groupId, @RequestParam("id") String id,
			@RequestParam("court") String court) {
		return accountSystemService.getBookmarkAlreadyExists(email, groupId, id, court);
	}

	// 用email get 所有屬於該 email 的螢光筆書籤 /accountSystem/email-all-highlighte
	@GetMapping("email-all-highlighte")
	public BasicRes getEmailHighlighte(@RequestParam("email") String email) {
		return accountSystemService.getEmailHighlighte(email);
	}

	// 取得該 email 的單筆螢光筆書籤 /accountSystem/highlighte-already-exists
	@GetMapping("highlighte-already-exists")
	public BasicRes getHighlighteAlreadyExists(@RequestParam("email") String email,
			@RequestParam("groupId") String groupId, @RequestParam("id") String id,
			@RequestParam("court") String court) {
		return accountSystemService.getHighlighteAlreadyExists(email, groupId, id, court);
	}

}