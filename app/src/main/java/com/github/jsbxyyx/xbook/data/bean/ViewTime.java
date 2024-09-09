package com.github.jsbxyyx.xbook.data.bean;

/**
 * @author jsbxyyx
 */
public class ViewTime {

    private Long id;
    private String targetId;
    private String targetType;
    private Long time;
    private String created;
    private String user;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ViewTime{" +
                "id=" + id +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", time=" + time +
                ", created='" + created + '\'' +
                ", user='" + user + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

}
