package top.baixiaoshengzjj.mygraduationapp.http.response;

public class GetPersonalDataBean {

    /** 姓名 */
    private String name;
    /** 身份 */
    private Integer identity;
    /** 性别 */
    private Integer gender;
    /** 年龄 */
    private Integer age;
    /** 手机号 */
    private String telephone;
    /** 紧急联系人 */
    private String contact;
    /** 地区 */
    private String address;
    /** 详细地址 */
    private String fulladdress;



    public GetPersonalDataBean() {
    }

    public String getName() {
        return name;
    }

    public Integer getIdentity() {
        return identity;
    }

    public Integer getGender() {
        return gender;
    }

    public Integer getAge() {
        return age;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public String getFulladdress() {
        return fulladdress;
    }
}
