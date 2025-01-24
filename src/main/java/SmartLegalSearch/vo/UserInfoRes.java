package SmartLegalSearch.vo;

public class UserInfoRes extends BasicRes {

    private String role;

    private String name;

    private String phone;

    private String licenseNumber;

    private String lawFirmNumber;

    private String city;

    private String address;

    private String lawFirm;

    public UserInfoRes() {
    }

    public UserInfoRes(int code, String message) {
        super(code, message);
    }

    public UserInfoRes(int code, String message, String role, String name, String phone, String licenseNumber, String lawFirmNumber, String city, String address, String lawFirm) {
        super(code, message);
        this.role = role;
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.lawFirmNumber = lawFirmNumber;
        this.city = city;
        this.address = address;
        this.lawFirm = lawFirm;
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
