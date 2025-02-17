package com.github.jsbxyyx.xbook.data.bean;

import java.util.List;

/**
 * @author jsbxyyx
 */
public class QqVideo {

    private String name;
    private String id;
    private String coverImage;
    private String descText;
    private String player;
    private List<QqPlaylist> playlist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public List<QqPlaylist> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<QqPlaylist> playlist) {
        this.playlist = playlist;
    }

    @Override
    public String toString() {
        return "QqVideo{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", descText='" + descText + '\'' +
                ", player='" + player + '\'' +
                ", playlist=" + playlist +
                '}';
    }

    public static class QqPlaylist {
        private String id;
        private String dataType; // --
        private String url;
        private String title;
        private String markLabel;
        private String asnycParams; // --
        private String displayType; // --
        private String checkUpTime; // --
        private String imgUrl; // --
        private String duration;
        private String rawTags; // --

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMarkLabel() {
            return markLabel;
        }

        public void setMarkLabel(String markLabel) {
            this.markLabel = markLabel;
        }

        public String getAsnycParams() {
            return asnycParams;
        }

        public void setAsnycParams(String asnycParams) {
            this.asnycParams = asnycParams;
        }

        public String getDisplayType() {
            return displayType;
        }

        public void setDisplayType(String displayType) {
            this.displayType = displayType;
        }

        public String getCheckUpTime() {
            return checkUpTime;
        }

        public void setCheckUpTime(String checkUpTime) {
            this.checkUpTime = checkUpTime;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getRawTags() {
            return rawTags;
        }

        public void setRawTags(String rawTags) {
            this.rawTags = rawTags;
        }

        @Override
        public String toString() {
            return "QqPlaylist{" +
                    "id='" + id + '\'' +
                    ", dataType='" + dataType + '\'' +
                    ", url='" + url + '\'' +
                    ", title='" + title + '\'' +
                    ", markLabel='" + markLabel + '\'' +
                    ", asnycParams='" + asnycParams + '\'' +
                    ", displayType='" + displayType + '\'' +
                    ", checkUpTime='" + checkUpTime + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", duration='" + duration + '\'' +
                    ", rawTags='" + rawTags + '\'' +
                    '}';
        }
    }
}
