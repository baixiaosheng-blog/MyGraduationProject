package top.baixiaosheng.mygraduationserver.service.model;



public class MedicalRecordPathModel {
    /** id */
    private Integer id;
    /** 用户id */
    private Integer uid;
    /** 病历文件路径 */
    private String medicalRecordPath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getMedicalRecordPath() {
        return medicalRecordPath;
    }

    public void setMedicalRecordPath(String medicalRecordPath) {
        this.medicalRecordPath = medicalRecordPath;
    }

    @Override
    public String toString() {
        return "MedicalRecordPathModel{" +
                "uid=" + uid +
                ", medicalRecordPath='" + medicalRecordPath + '\'' +
                '}';
    }
}
