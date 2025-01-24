package SmartLegalSearch;

import SmartLegalSearch.constants.ResMessage;
import SmartLegalSearch.entity.AccountSystem;
import SmartLegalSearch.repository.AccountSystemDao;
import SmartLegalSearch.service.ifs.AccountSystemService;
import SmartLegalSearch.vo.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
public class AccountSystemTest {

    @Autowired
    private AccountSystemService accountSystemService;

    @Autowired
    private AccountSystemDao accountSystemDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 測試註冊
    @Transactional
    @Test
    public void testRegister() {
        // 模擬 RegisterReq 和 RegisterRes
        RegisterReq req = new RegisterReq("abc@gmail.com","abc123");
        RegisterRes res = accountSystemService.register(req);

        // 測試 res 是否為 null，以及 code、message、email、status 是否正確
        Assert.notNull(res, "res is null");
        Assert.isTrue(res.getCode() == ResMessage.SUCCESS.getCode(), "code error");
        Assert.isTrue(res.getMessage().equals(ResMessage.SUCCESS.getMessage()), "message error");
        Assert.isTrue(res.getEmail().equals("abc@gmail.com"), "email error");
        Assert.isTrue(res.getStatus().equals(RegisterRes.RegisterStatus.EMAIL_VERIFICATION_PENDING), "status error");
    }

    // 測試更新用戶資訊
    @Transactional
    @Test
    public void testUpdateProfile() {
        // 模擬 UpdateProfileReq、BasicRes
        UpdateProfileReq req = new UpdateProfileReq("abc@gmail.com", "user", "New Name",
                "0912345678", null, null, null, null, null);
        MockHttpSession session = new MockHttpSession();
        BasicRes res = accountSystemService.updateProfile(req, session);

        // 模擬已存在的帳戶
        AccountSystem user = new AccountSystem();
        user.setEmail("abc@gmail.com");
        user.setName("Old Name");
        user.setPhone("0987654321");
        user.setEmailVerified(true);
        accountSystemDao.save(user);

        // 模擬 session 設定
        session.setAttribute("role", "user");

        // 測試 res 是否為 null，以及 code、message 是否正確
        Assert.notNull(res, "res is null");
        Assert.isTrue(res.getCode() == ResMessage.SUCCESS.getCode(), "code error");
        Assert.isTrue(res.getMessage().equals(ResMessage.SUCCESS.getMessage()), "message error");

        // 測試帳戶是否被更新
        AccountSystem updatedUser = accountSystemDao.findByEmail("abc@gmail.com");
        Assert.isTrue(updatedUser.getName().equals("New Name"), "Name not updated");
        Assert.isTrue(updatedUser.getPhone().equals("0912345678"), "Phone not updated");
        Assert.isTrue(updatedUser.getRole().equals("user"),"Role not updated");

        //  測試 session 的 role 是否被設定
        Assert.isTrue(Objects.equals(session.getAttribute("role"), "user"), "Session role not set");
    }

    // 測試登入
    @Transactional
    @Test
    public void testLogin() {
        // 模擬測試資料
        String email = "abc@gmail.com";
        String password = "abc123";

        // 模擬 LoginReq、BasicRes
        LoginReq req = new LoginReq(email, password);
        MockHttpSession session = new MockHttpSession();
        BasicRes res = accountSystemService.login(req, session);

        // 模擬已存在的帳戶
        AccountSystem user = new AccountSystem();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmailVerified(true);
        accountSystemDao.save(user);

        // 模擬 session 設定
        session.setAttribute("role", "user");

        // 測試登入結果
        Assert.notNull(res, "res is null");
        Assert.isTrue(res.getCode() == ResMessage.SUCCESS.getCode(), "code error");
        Assert.isTrue(res.getMessage().equals(ResMessage.SUCCESS.getMessage()), "message error");

        //  測試 session 的 role 是否被設定
        Assert.isTrue(Objects.equals(session.getAttribute("role"), "user"), "Session role not set");
    }

    // 測試登出
    @Test
    public void testLogout() {
        // 模擬 session
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("role", "user");

        // BasicRes
        BasicRes res = accountSystemService.logout(session);

        // 測試登出結果
        Assert.notNull(res, "res is null");
        Assert.isTrue(res.getCode() == ResMessage.SUCCESS.getCode(), "code error");
        Assert.isTrue(res.getMessage().equals(ResMessage.SUCCESS.getMessage()), "message error");

        // 測試登出後，session 已經失效
        Assert.isNull(session.getAttribute("role"), "Session role should be null after logout");
    }

    // 測試忘記密碼，寄信驗證
    @Transactional
    @Test
    public void testForgotPassword() {
        // 模擬測試資料
        String email = "abc@gmail.com";
        String password = "abc123";

        // 模擬 ForgotPasswordReq、BasicRes
        ForgotPasswordReq req = new ForgotPasswordReq(email);
        BasicRes res = accountSystemService.forgotPassword(req, new MockHttpSession());

        // 模擬已存在的帳戶
        AccountSystem user = new AccountSystem();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmailVerified(true);
        accountSystemDao.save(user);

        // 測試 res
        Assert.notNull(res, "res is null");
        Assert.isTrue(res.getCode() == ResMessage.SUCCESS.getCode(), "code error");
        Assert.isTrue(res.getMessage().equals(ResMessage.SUCCESS.getMessage()), "message error");

        // 測試 token 是否已經存在且設定時限
        AccountSystem updatedUser = accountSystemDao.findByEmail(email);
        Assert.notNull(updatedUser.getEmailVerificationToken(), "Verification token is null");
        Assert.isTrue(updatedUser.getTokenExpiry().isAfter(LocalDateTime.now()), "Token expiry is incorrect");
    }

    // 測試重置密碼
    @Transactional
    @Test
    public void testResetPassword() {
        // 模擬測試資料
        String email = "abc@gmail.com";
        String newPassword  = "newAbc123";
        String verificationToken = UUID.randomUUID().toString();

        // 模擬 ResetPasswordReq、BasicRes
        ResetPasswordReq req = new ResetPasswordReq(email, newPassword);
        BasicRes res = accountSystemService.resetPassword(req, new MockHttpSession());

        // 模擬已存在的帳戶
        AccountSystem user = new AccountSystem();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("oldAbc123"));
        user.setEmailVerified(true);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(30));
        accountSystemDao.save(user);

        // 測試密碼是否已經更新
        AccountSystem updatedUser = accountSystemDao.findByEmail(email);
        Assert.isTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()),
                "Password was not updated correctly");

        // 測試 token 期限是否已被清空
        Assert.isNull(updatedUser.getTokenExpiry(), "Token expiry should be null");
    }
}