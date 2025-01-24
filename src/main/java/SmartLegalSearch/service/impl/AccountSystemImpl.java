package SmartLegalSearch.service.impl;

import SmartLegalSearch.constants.ResMessage;
import SmartLegalSearch.entity.AccountSystem;
import SmartLegalSearch.entity.Bookmark;
import SmartLegalSearch.entity.Highlighte;
import SmartLegalSearch.repository.AccountSystemDao;
import SmartLegalSearch.repository.BookmarksDao;
import SmartLegalSearch.repository.HighlighteDao;
import SmartLegalSearch.service.ifs.AccountSystemService;
import SmartLegalSearch.vo.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountSystemImpl implements AccountSystemService {

	@Autowired
	private AccountSystemDao accountSystemDao; // 資料庫操作

	@Autowired
	private BookmarksDao bookmarksDao;

	@Autowired
	private HighlighteDao highlighteDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; // 密碼加密

	@Autowired
	private JavaMailSender mailSender; // 注入的內容在 properties

	// 寄信
	private void sendEmail(String receiver, String subject, String text) {
		try {
			/*
			 * 創建 MimeMessage 來發送郵件 MimeMessageHelper 為 Spring 框架中的輔助類，簡化 MimeMessage
			 * 的構建過程（郵件相關細節，如主旨等） 將 message 參數傳入協作， true 則表示郵件會包含附件或其他非文本內容（例如，圖片、文件等）
			 */
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			// 設定郵件內容
			helper.setTo(receiver); // 收件人
			helper.setSubject(subject); // 主旨
			helper.setText(text, true); // 內文，true 表示該內容是 HTML 格式

			// 寄出郵件
			mailSender.send(message);
		} catch (Exception e) {
			throw new IllegalArgumentException("Email error.");
		}
	}

	// 註冊
	@Transactional
	@Override
	public RegisterRes register(RegisterReq req) {
		// 取得使用者資料
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());

		// 檢查 email 是否存在以及是否已被驗證
		if (user != null && user.isEmailVerified()) {
			return new RegisterRes(ResMessage.EMAIL_DUPLICATED.getCode(), ResMessage.EMAIL_DUPLICATED.getMessage());
		}

		// 生成驗證 token 和驗證時間（15分鐘）
		String verificationToken = UUID.randomUUID().toString();
		LocalDateTime tokenExpiry = LocalDateTime.now().plusMinutes(15);

		// 寄送身分驗證 email
		try {
			sendEmail(req.getEmail(), "會員申請確認",
					"<html>" + "<body>" + "<h2>親愛的用戶，</h2>"
							+ "<h2>感謝您註冊我們的服務！我們已收到您的會員申請。如果是您本人提出的申請，請點擊以下連結來確認您的註冊：</h2>"
							+ "<h2><a href='http://localhost:8080/accountSystem/verify-email?token=" + verificationToken
							+ "'>點擊此處確認您的會員申請</a></h2>" + "<h2>此連結的有效期限為15分鐘。若您未提出此申請，請忽略此郵件，您的帳戶將保持安全。</h2>"
							+ "<h2>如果您有任何疑問或遇到任何問題，請隨時聯繫我們的客服團隊。</h2>" + "<h2>謝謝！</h2>" + "</body>" + "</html>");
		} catch (Exception e) {
			return new RegisterRes(ResMessage.EMAIL_SEND_FAILED.getCode(), ResMessage.EMAIL_SEND_FAILED.getMessage());
		}

		// 更新已存在用戶的 token 資訊
		if (user != null && !user.isEmailVerified()) {
			user.setPassword(passwordEncoder.encode(req.getPassword()));
			user.setEmailVerificationToken(verificationToken);
			user.setTokenExpiry(tokenExpiry);
			accountSystemDao.save(user);
		}

		// 更新新帳戶資訊
		if (user == null) {
			AccountSystem newUser = new AccountSystem();
			newUser.setEmail(req.getEmail());
			newUser.setPassword(passwordEncoder.encode(req.getPassword()));
			newUser.setEmailVerified(false);
			newUser.setEmailVerificationToken(verificationToken);
			newUser.setTokenExpiry(tokenExpiry);
			accountSystemDao.save(newUser);
		}

		return new RegisterRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), req.getEmail(),
				RegisterRes.RegisterStatus.EMAIL_VERIFICATION_PENDING);
	}

	// 根據 token 驗證 email
	@Transactional
	@Override
	public ResponseEntity<String> verifyEmail(String token) {
		// 根據 token 查找用戶
		AccountSystem user = accountSystemDao.findByEmailVerificationToken(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification link.");
		}

		// 檢查 token 是否過期
		if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The verification link has expired.");
		}

		// 更新用戶狀態
		user.setAvailable(true);
		user.setEmailVerified(true);
		user.setEmailVerificationToken(null);
		user.setTokenExpiry(null);
		accountSystemDao.save(user);

		// 若驗證成功，重新導向到登入頁面
		String redirectUrl = "http://localhost:4200/login";
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY) // HTTP 301永久導向
				.header(HttpHeaders.LOCATION, redirectUrl) // 指示導向的位置
				.build();
	}

	// 登入
	@Override
	public BasicRes login(LoginReq req, HttpSession session) {
		// 取得使用者資料
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());

		// 檢查 email 是否存在或是否被驗證、啟用
		if (user == null || !user.isEmailVerified() || !user.isAvailable()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查是否已有登入
		String attr = (String) session.getAttribute("role");
		if (attr != null) {
			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
		}

		// 核對密碼
		if (!(passwordEncoder.matches(req.getPassword(), user.getPassword()))) {
			return new BasicRes(ResMessage.PASSWORD_ERROR.getCode(), ResMessage.PASSWORD_ERROR.getMessage());
		}

		// 若登入成功，設定 session 的 attribute 和有效時間
		session.setAttribute("role", user.getRole());
		session.setMaxInactiveInterval(3600); // 60分鐘

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 更新用戶資訊
	@Transactional
	@Override
	public BasicRes updateProfile(UpdateProfileReq req, HttpSession session) {
		// 取得使用者資料
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());

		// 檢查 email 是否存在及被驗證、啟用
		if (user == null || !user.isEmailVerified() || !user.isAvailable()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查是否為登入狀態
		String attr = (String) session.getAttribute("role");
		if (attr == null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查是否為指定的 role
		if (!req.getRole().equals("guest") && !req.getRole().equals("user") && !req.getRole().equals("lawFirm")
				&& !req.getRole().equals("lawyer")) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 若用戶為使用者，不得填寫律師證書字號、事務所統編、地址；必填 city
		if (req.getRole().equals("user")
				&& ((req.getLicenseNumber() != null || req.getLawFirmNumber() != null || req.getAddress() != null))) {
			return new BasicRes(ResMessage.INCORRECTLY_FILLED_IN.getCode(),
					ResMessage.INCORRECTLY_FILLED_IN.getMessage());
		}
		if (req.getRole().equals("user") && req.getCity() == null) {
			return new BasicRes(ResMessage.INCORRECTLY_FILLED_IN.getCode(),
					ResMessage.INCORRECTLY_FILLED_IN.getMessage());
		}

		// 若用戶為事務所，不得填寫律師證書字號、city；必填事務所統編、地址
		if ((req.getRole().equals("lawFirm") && ((req.getLicenseNumber() != null) || req.getCity() != null))) {
			return new BasicRes(ResMessage.INCORRECTLY_FILLED_IN.getCode(),
					ResMessage.INCORRECTLY_FILLED_IN.getMessage());
		}
		if (req.getRole().equals("lawFirm") && ((req.getLawFirmNumber() == null || req.getAddress() == null))) {
			return new BasicRes(ResMessage.INCORRECTLY_FILLED_IN.getCode(),
					ResMessage.INCORRECTLY_FILLED_IN.getMessage());
		}

		// 若用戶為律師，不得填寫事務所統編、city；必填律師證書字號、事務所
		if (req.getRole().equals("lawyer") && ((req.getLawFirmNumber() != null || req.getCity() != null))) {
			return new BasicRes(ResMessage.INCORRECTLY_FILLED_IN.getCode(),
					ResMessage.INCORRECTLY_FILLED_IN.getMessage());
		}
		if ((req.getRole().equals("lawyer") && ((req.getLicenseNumber() == null) || req.getLawFirm() == null))) {
			return new BasicRes(ResMessage.INCORRECTLY_FILLED_IN.getCode(),
					ResMessage.INCORRECTLY_FILLED_IN.getMessage());
		}

		// 更新用戶身分、姓名、電話
		user.setRole(req.getRole());
		user.setName(req.getName());
		user.setPhone(req.getPhone());

		// 更新一般用戶資訊
		if (req.getRole().equals("user")) {
			// 設定基本資訊
			user.setCity(req.getCity());

			// 處理變更身分的情形
			if (user.getLawFirm() != null) {
				user.setLawFirm(null);
			}
			if (user.getLawFirmNumber() != null) {
				user.setLawFirmNumber(null);
			}
			if (user.getLicenseNumber() != null) {
				user.setLicenseNumber(null);
			}
			if (user.getAddress() != null) {
				user.setAddress(null);
			}
		}

		// 更新事務所資訊
		if (req.getRole().equals("lawFirm")) {
			// 設定基本資訊
			user.setLawFirmNumber(req.getLawFirmNumber());
			user.setAddress(req.getAddress());

			// 處理變更身分的情形
			if (user.getCity() != null) {
				user.setCity(null);
			}
			if (user.getLicenseNumber() != null) {
				user.setLicenseNumber(null);
			}
			if (user.getLawFirm() != null) {
				user.setLawFirm(null);
			}
		}

		// 更新律師資訊
		if (req.getRole().equals("lawyer")) {
			// 設定基本資訊
			user.setLicenseNumber(req.getLicenseNumber());
			user.setLawFirm(req.getLawFirm());

			// 處理變更身分的情形
			if (user.getCity() != null) {
				user.setCity(null);
			}
			if (user.getLawFirmNumber() != null) {
				user.setLawFirmNumber(null);
			}
			if (user.getAddress() != null) {
				user.setAddress(null);
			}
		}

		// 儲存到資料庫
		accountSystemDao.save(user);

		// 更新設定 session 的 attribute 和有效時間
		session.setAttribute("role", user.getRole());
		session.setMaxInactiveInterval(3600); // 60分鐘

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 更新用戶頭像
	@Transactional
	@Override
	public BasicRes updateProfilePicture(String email, MultipartFile profilePicture, HttpSession session) {
		// 取得使用者資料
		AccountSystem user = accountSystemDao.findByEmail(email);

		// 檢查 email 是否存在及被驗證、啟用
		if (user == null || !user.isEmailVerified() || !user.isAvailable()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查是否為登入狀態
		String attr = (String) session.getAttribute("role");
		if (attr == null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 更新用戶頭像
		if (profilePicture != null && !profilePicture.isEmpty()) {
			try {
				// 檢查檔案格式 (只允許 jpg 和 png)
				String contentType = profilePicture.getContentType();
				if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
					return new BasicRes(ResMessage.INVALID_FILE_FORMAT.getCode(),
							ResMessage.INVALID_FILE_FORMAT.getMessage());
				}

				// 將圖片轉為 byte[]，並儲存到資料庫
				byte[] profilePictureByte = profilePicture.getBytes();
				user.setProfilePicture(profilePictureByte);

				// 儲存到資料庫
				accountSystemDao.save(user);
			} catch (IOException e) {
				return new BasicRes(ResMessage.FILE_UPLOAD_FAILED.getCode(),
						ResMessage.FILE_UPLOAD_FAILED.getMessage());
			}
		}

		// 更新設定 session 的 attribute 和有效時間
		session.setAttribute("role", user.getRole());
		session.setMaxInactiveInterval(3600); // 60分鐘

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 登出
	@Override
	public BasicRes logout(HttpSession session) {
		// 檢查是否為登入狀態
		String attr = (String) session.getAttribute("role");
		if (attr == null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 若為登入狀態，則使 session 失效
		session.invalidate();

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 忘記密碼，寄信驗證
	@Transactional
	@Override
	public BasicRes forgotPassword(ForgotPasswordReq req, HttpSession session) {
		// 檢查是否為登出狀態
		String attr = (String) session.getAttribute("role");
		if (attr != null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 取得使用者資料
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());

		// 檢查 email 是否存在及被驗證、啟用
		if (user == null || !user.isEmailVerified() || !user.isAvailable()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 生成驗證 token 和驗證時間（30分鐘）
		String verificationToken = UUID.randomUUID().toString();
		LocalDateTime tokenExpiry = LocalDateTime.now().plusMinutes(30);
		user.setEmailVerificationToken(verificationToken);
		user.setTokenExpiry(tokenExpiry);

		// 寄送身分驗證 email
		try {
			sendEmail(req.getEmail(), "重設您的帳戶密碼",
					"<html>" + "<body>" + "<h2>親愛的用戶，</h2>" + "<h2>我們收到您重設密碼的請求。如果是您本人提出的請求，請點擊以下連結來重設您的帳戶密碼：</h2>"
							+ "<h2><a href='http://localhost:8080/accountSystem/verify-password-reset-token?token="
							+ verificationToken + "'>點擊此處重設您的密碼</a></h2>"
							+ "<h2>此連結的有效期限為30分鐘。若您未提出此請求，請忽略此郵件，您的帳戶將保持安全。</h2>"
							+ "<h2>如果您有任何疑問或遇到任何問題，請隨時聯繫我們的客服團隊。</h2>" + "<h2>謝謝！</h2>" + "</body>" + "</html>");
		} catch (Exception e) {
			return new RegisterRes(ResMessage.EMAIL_SEND_FAILED.getCode(), ResMessage.EMAIL_SEND_FAILED.getMessage());
		}

		// 保存到資料庫
		accountSystemDao.save(user);

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 驗證重設密碼 token
	@Transactional
	@Override
	public ResponseEntity<String> verifyPasswordResetToken(String token) {
		// 根據 token 查找用戶
		AccountSystem user = accountSystemDao.findByEmailVerificationToken(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification link.");
		}

		// 檢查 token 是否過期
		if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The verification link has expired.");
		}

		// 更新用戶狀態
		user.setEmailVerificationToken(null);
		accountSystemDao.save(user);

		// 若驗證成功，重新導向到重置密碼的網址
		String redirectUrl = "http://localhost:4200/reset-password";
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY) // HTTP 301永久導向
				.header(HttpHeaders.LOCATION, redirectUrl) // 指示導向的位置
				.build();
	}

	// 重置密碼
	@Transactional
	@Override
	public BasicRes resetPassword(ResetPasswordReq req, HttpSession session) {
		// 檢查是否為登出狀態
		String attr = (String) session.getAttribute("role");
		if (attr != null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 取得使用者資料
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());

		// 檢查 email 是否存在及被驗證、檢查 token 是否「不存在」、檢查 token 期限是否存在、是否被啟用
		if (user == null || !user.isEmailVerified() || user.getEmailVerificationToken() != null
				|| user.getTokenExpiry() == null || !user.isAvailable()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查 token 是否過期
		if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
			return new BasicRes(ResMessage.LINK_HAS_EXPIRED.getCode(), ResMessage.LINK_HAS_EXPIRED.getMessage());
		}

		// 更新用戶密碼、token 狀態
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		user.setTokenExpiry(null);
		accountSystemDao.save(user);

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 獲取用戶資訊
	@Override
	public UserInfoRes getUserInfo(ForgotPasswordReq req, HttpSession session) {
		// 檢查 email 是否存在、被啟用
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());
		if (user == null || !user.isAvailable()) {
			return new UserInfoRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查是否為登入狀態
		if (session.getAttribute("role") == null) {
			return new UserInfoRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		return new UserInfoRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), user.getRole(),
				user.getName(), user.getPhone(), user.getLicenseNumber(), user.getLawFirmNumber(), user.getCity(),
				user.getAddress(), user.getLawFirm());
	}

	// 獲取用戶頭像
	@Override
	public ResponseEntity<byte[]> getProfilePicture(ForgotPasswordReq req, HttpSession session) {
		// 檢查 email 是否存在、啟用
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());
		if (user == null || !user.isAvailable()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found".getBytes());
		}

		// 檢查是否為登入狀態
		if (session.getAttribute("role") == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in.".getBytes());
		}

		// 取得用戶頭像
		byte[] profilePicture = user.getProfilePicture();

		// 檢查是否存在
		if (profilePicture == null || profilePicture.length == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile picture not found.".getBytes());
		}

		// 判斷圖片格式
		String contentType = "image/jpeg"; // 默認為 JPEG
		try {
			// 讀取圖片的格式
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(profilePicture));
			if (image != null) {
				/*
				 * getImageReaders(image): 獲取能夠讀取指定圖像格式的圖像讀取器 hasNext(): 確定是否有可用的讀取器 next():
				 * 獲取第一個讀取器，如針對 JPEG、PNG 等 getFormatName(): 返回圖像格式名稱
				 */
				Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
				if (readers.hasNext()) {
					String formatName = readers.next().getFormatName();
					if ("PNG".equalsIgnoreCase(formatName)) {
						contentType = "image/png"; // 如果是 PNG，則設置為 image/png
					}
				}
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading image.".getBytes());
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)) // 根據判斷的格式設置 Content-Type
				.body(profilePicture);
	}

	// 刪除用戶資料
	@Transactional
	@Override
	public BasicRes deleteUser(DeleteAccountReq req, HttpSession session) {
		// 檢查 email 是否存在、啟用
		AccountSystem user = accountSystemDao.findByEmail(req.getEmail());
		if (user == null || !user.isAvailable()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查是否為登入狀態
		if (session.getAttribute("role") == null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 檢查用戶密碼
		if (req.getPassword() == null ||
				!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
			return new BasicRes(ResMessage.PASSWORD_ERROR.getCode(), ResMessage.PASSWORD_ERROR.getMessage());
		}

		// 刪除用戶資料
		user.setAvailable(false);
		user.setEmailVerified(false);

		// 使 session 失效
		session.invalidate();

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 存入書籤
	@Override
	public BasicRes seveBookmark(BookmarkReq req) {
		if (req.getEmail() == null || req.getGroupId() == null || req.getId() == null || req.getCourt() == null) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		try {
			// 檢查資料是否已經存在
			Optional<Bookmark> existingBookmark = bookmarksDao.findByEmailAndGroupIdAndIdAndCourt(req.getEmail(),
					req.getGroupId(), req.getId(), req.getCourt());

			if (existingBookmark.isPresent()) {
				// 如果資料已經存在，則返回提示訊息
				return new BasicRes(ResMessage.ALREADY_EXISTS.getCode(), "該書籤已經存在，無需重複存入。");
			}

			// 如果資料不存在，則進行保存
			Bookmark bookmark = new Bookmark();
			bookmark.setEmail(req.getEmail());
			bookmark.setGroupId(req.getGroupId());
			bookmark.setId(req.getId());
			bookmark.setCourt(req.getCourt());
			bookmark.setVerdictDate(req.getVerdictDate());
			bookmark.setCharge(req.getCharge());
			bookmark.setJudgeName(req.getJudgeName());
			bookmark.setDefendantName(req.getDefendantName());
			bookmark.setCaseType(req.getCaseType());
			bookmark.setDocType(req.getDocType());
			bookmarksDao.save(bookmark);

			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new BasicRes(500, "保存書籤失敗：" + e.getMessage());
		}
	}

	// 存入螢光筆
	@Override
	public BasicRes seveHighlighte(HighlighteReq req) {
		if (req.getEmail() == null || req.getGroupId() == null || req.getId() == null || req.getCourt() == null
				|| req.getHighlights() == null || req.getHighlights().isEmpty()) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		try {
			for (HighlighteReq.Highlight highlight : req.getHighlights()) {
				// 檢查每個 highlight 是否已經存在資料
				Optional<Highlighte> existingHighlight = highlighteDao.findByEmailAndGroupIdAndIdAndCourtAndStartOffset(
						req.getEmail(), req.getGroupId(), req.getId(), req.getCourt(), highlight.getStartOffset());

				if (existingHighlight.isPresent()) {
					// 如果資料已經存在，則跳過這筆資料
					continue; // 跳過已經存在的高亮
				}

				// 如果資料不存在，則儲存這筆新的螢光筆資料
				Highlighte newHighlight = new Highlighte(req.getEmail(), req.getGroupId(), req.getId(), req.getCourt(),
						highlight.getHighlighterColor(), highlight.getSelectText(), highlight.getStartOffset(),
						highlight.getEndOffset());
				highlighteDao.save(newHighlight);
			}

			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return new BasicRes(500, "保存螢光筆資料失敗：" + e.getMessage());
		}
	}

	// 取得該email的書籤
	@Override
	public BookmarkRes getEmailBookmark(String email) {
		// 1. 檢查 email 是否為空或 null
		if (email == null || email.trim().isEmpty()) {
			return new BookmarkRes(ResMessage.EMAIL_ERROR.getCode(), ResMessage.EMAIL_ERROR.getMessage());
		}

		// 2. 嘗試查詢資料庫，並捕獲異常
		List<Bookmark> bookmarks = null;
		try {
			bookmarks = bookmarksDao.getEmailBookmark(email);
		} catch (Exception e) {
			e.printStackTrace();
			return new BookmarkRes(500, "查詢書籤資料時發生異常：" + e.getMessage());
		}

		// 3. 檢查結果是否為空
		if (bookmarks == null || bookmarks.isEmpty()) {
			return new BookmarkRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// 4. 成功返回結果
		return new BookmarkRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), bookmarks);

	}

	// 取得該email的螢光筆書籤
	@Override
	public HighlighteRes getEmailHighlighte(String email) {
		// 1. 檢查 email 是否為空或 null
		if (email == null || email.trim().isEmpty()) {
			return new HighlighteRes(ResMessage.EMAIL_ERROR.getCode(), ResMessage.EMAIL_ERROR.getMessage());
		}

		// 2. 嘗試查詢資料庫，並捕獲異常
		List<Highlighte> highlighters = null;
		try {
			highlighters = highlighteDao.getEmailHighlighte(email);
		} catch (Exception e) {
			e.printStackTrace();
			return new HighlighteRes(500, "查詢螢光筆資料時發生異常：" + e.getMessage());
		}

		// 3. 檢查結果是否為空
		if (highlighters == null || highlighters.isEmpty()) {
			return new HighlighteRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// 4. 成功返回結果
		return new HighlighteRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), highlighters);
	}

	@Override
	public SearchRes getemailJudgmentidText(String groupId, String id, String court) {
		// TODO Auto-generated method stub
		return null;
	}

	// 取得單筆書籤
	@Override
	public BasicRes getBookmarkAlreadyExists(String email, String groupId, String id, String court) {
		if (email == null || groupId == null || id == null || court == null) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		Optional<Bookmark> existingBookmark = bookmarksDao.findByEmailAndGroupIdAndIdAndCourt(email, groupId, id,
				court);
		if (existingBookmark.isPresent()) {
			// 如果資料已經存在，則返回提示訊息
			return new BasicRes(ResMessage.ALREADY_EXISTS.getCode(), ResMessage.ALREADY_EXISTS.getMessage());
		}

		return null;
	}

	// 單筆書籤刪除
	@Override
	public BasicRes deleteBookmark(BookmarkReq req) {
		// 防呆檢查，確認請求參數完整性
		if (req == null || req.getEmail() == null || req.getGroupId() == null || req.getId() == null
				|| req.getCourt() == null) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		try {
			// 檢查書籤是否存在
			Optional<Bookmark> existingBookmark = bookmarksDao.findByEmailAndGroupIdAndIdAndCourt(req.getEmail(),
					req.getGroupId(), req.getId(), req.getCourt());

			if (!existingBookmark.isPresent()) {
				return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
			}

			// 書籤存在，執行刪除
			bookmarksDao.deleteByEmailAndGroupIdAndIdAndCourt(req.getEmail(), req.getGroupId(), req.getId(),
					req.getCourt());

			// 刪除成功
			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

		} catch (Exception e) {
			// 捕獲異常時，直接回傳 null
			return null;
		}
	}

	// 全文頁面 螢光筆提取
	@Override
	public HighlighteRes getHighlighteAlreadyExists(String email, String groupId, String id, String court) {
		// 1. 防呆檢查：集中處理所有條件變數
		if (email == null || email.trim().isEmpty() || groupId == null || groupId.trim().isEmpty() || id == null
				|| id.trim().isEmpty() || court == null || court.trim().isEmpty()) {
			return new HighlighteRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// 2. 查詢資料庫以確認資料是否存在
		List<Highlighte> highlighters = highlighteDao.getHighlighteAlreadyExists(email, groupId, id, court);
		if (highlighters.isEmpty()) {
			return new HighlighteRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}

		// 3. 成功回傳資料
		return new HighlighteRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), highlighters);
	}

	@Override
	public BasicRes deleteHighlighte(HighlighteReq req) {
		// 防呆檢查，確認請求參數完整性
		if (req == null || req.getEmail() == null || req.getGroupId() == null || req.getId() == null
				|| req.getCourt() == null) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		try {
			// 檢查書籤是否存在
			// 2. 查詢資料庫以確認資料是否存在
			List<Highlighte> highlighters = highlighteDao.getHighlighteAlreadyExists(req.getEmail(), req.getGroupId(), req.getId(), req.getCourt());
			if (highlighters.isEmpty()) {
				return new HighlighteRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
			}

			// 書籤存在，執行刪除
			highlighteDao.deleteByEmailAndGroupIdAndIdAndCourt(req.getEmail(), req.getGroupId(), req.getId(),
					req.getCourt());

			// 刪除成功
			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

		} catch (Exception e) {
			// 捕獲異常時，直接回傳 null
			return null;
		}
	}

}