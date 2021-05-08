package top.baixiaosheng.mygraduationserver.response;

public class OtpCode {
    private String telephone;
    private String email;

    private String otpCode;

    public OtpCode(String email, String otpCode) {
        this.email = email;
        this.otpCode = otpCode;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
