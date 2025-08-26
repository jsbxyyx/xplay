package com.github.jsbxyyx.xbook.data.bean;

public class Comment {

    private String id;
    private String object; //评论对象
    private String object_id; //评论对象ID
    private String parent_id; //父评论
    private String user_id; //评论用户ID
    private String text; // 内容
    private String date; //日期
    private String dateRelative; // 相对日期
    private CommentUser user = new CommentUser(); // 用户

    public static class CommentUser {
        private String name;
        private String avatar;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateRelative() {
        return dateRelative;
    }

    public void setDateRelative(String dateRelative) {
        this.dateRelative = dateRelative;
    }

    public CommentUser getUser() {
        return user;
    }

    public void setUser(CommentUser user) {
        this.user = user;
    }
}
