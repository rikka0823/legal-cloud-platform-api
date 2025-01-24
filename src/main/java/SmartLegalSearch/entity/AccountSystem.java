package SmartLegalSearch.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;


@Entity
@Table(name = "account_system")
public class AccountSystem {

    @Id
    @Column(name = "email", nullable = false)
    private String email; // 用戶帳戶，不能重複

    @Column(name = "name" , nullable = false)
    private String name = "guest"; // 用戶名稱，預設為 guest

    @Column(name = "password", nullable = false)
    private String password; // 用戶密碼（加密後儲存）

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false; // 用戶帳號狀態，預設 false，第一次需要 email 認證

    @Column(name = "role", nullable = false)
    private String role = "guest"; // 用戶角色，預設 guest，事務所為 lawFirm、律師為 lawyer、一般民眾為 user

    @Column(name = "phone")
    private String phone = "0"; // 電話，預設0，表示尚未email驗證及更新資料

    @Column(name = "email_verification_token")
    private String emailVerificationToken; // email 驗證 token

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry; // 儲存 token 過期時間

    @Lob  // 儲存圖片資料，LONGBLOB 格式
    @Column(name = "profile_picture")
    private byte[] profilePicture; // 儲存個人頭像的二進位資料

    @Column(name = "license_number")
    private String licenseNumber; // 律師證書字號

    @Column(name = "law_firm_number")
    private String lawFirmNumber; //事務所統編

    @Column(name = "city")
    private String city; // 用戶所在縣市

    @Column(name = "address")
    private String address; // 用戶地址

    @Column(name = "law_firm")
    private String lawFirm; // 所屬事務所

    @Column(name = "available")
    private boolean available; // 帳號是否啟用

    public AccountSystem() {
    }

    public AccountSystem(String email, String name, String password, boolean emailVerified, String role, String phone, String emailVerificationToken, LocalDateTime tokenExpiry, byte[] profilePicture, String licenseNumber, String lawFirmNumber, String city, String address, String lawFirm, boolean available) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.emailVerified = emailVerified;
        this.role = role;
        this.phone = phone;
        this.emailVerificationToken = emailVerificationToken;
        this.tokenExpiry = tokenExpiry;
        this.profilePicture = profilePicture;
        this.licenseNumber = licenseNumber;
        this.lawFirmNumber = lawFirmNumber;
        this.city = city;
        this.address = address;
        this.lawFirm = lawFirm;
        this.available = available;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public LocalDateTime getTokenExpiry() {
        return tokenExpiry;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public void setTokenExpiry(LocalDateTime tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLawFirmNumber() {
        return lawFirmNumber;
    }

    public void setLawFirmNumber(String lawFirmNumber) {
        this.lawFirmNumber = lawFirmNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLawFirm() {
        return lawFirm;
    }

    public void setLawFirm(String lawFirm) {
        this.lawFirm = lawFirm;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}