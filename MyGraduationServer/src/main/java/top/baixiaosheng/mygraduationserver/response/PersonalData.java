package top.baixiaosheng.mygraduationserver.response;

public class PersonalData {
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

    public PersonalData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFulladdress() {
        return fulladdress;
    }

    public void setFulladdress(String fulladdress) {
        this.fulladdress = fulladdress;
    }
}
