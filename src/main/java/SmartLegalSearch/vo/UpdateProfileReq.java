package SmartLegalSearch.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class UpdateProfileReq {

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email; // email 不得為空白字串、空字串、null，且須符合格式

    @NotBlank(message = "Role cannot be blank.")
    private String role; // role 不得為空白字串、空字串、null

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, max = 25, message = "Name must be between 2 and 25 characters.")
    private String name; // name 不得為空白字串、空字串、null，且最少2個字最多25個字

    @NotBlank(message = "Phone cannot be blank")
    @Size(min = 10, max = 10, message = "Phone must be exactly 10 digits")
    private String phone; // phone 不得為空白字串、空字串、null，且必須為10碼

    @Pattern(regexp = "^\\d{1,3}臺檢證字第\\d{1,5}號$", message = "Invalid license number format.")
    private String licenseNumber; // 律師證書字號

    @Pattern(regexp = "^\\d{8}$", message = "Invalid law firm number format.")
    private String lawFirmNumber; //事務所統編

    private String city; // 用戶所在縣市

    private String address; // 用戶地址

    private String lawFirm; // 所屬事務所

    public UpdateProfileReq() {
    }

    public UpdateProfileReq(String email, String role, String name, String phone, String licenseNumber, String lawFirmNumber, String city, String address, String lawFirm) {
        this.email = email;
        this.role = role;
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.lawFirmNumber = lawFirmNumber;
        this.city = city;
        this.address = address;
        this.lawFirm = lawFirm;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getLawFirmNumber() {
        return lawFirmNumber;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getLawFirm() {
        return lawFirm;
    }
}
