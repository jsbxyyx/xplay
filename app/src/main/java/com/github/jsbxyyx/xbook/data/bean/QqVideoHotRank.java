package com.github.jsbxyyx.xbook.data.bean;

import java.util.List;

public class QqVideoHotRank {

    private HotRankResult hotRankResult;
    private String tabName;
    private String tabId;

    public HotRankResult getHotRankResult() {
        return hotRankResult;
    }

    public void setHotRankResult(HotRankResult hotRankResult) {
        this.hotRankResult = hotRankResult;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public static class HotRankResult {
        private List<RankItem> rankItemList;
        private String totalSize;

        public List<RankItem> getRankItemList() {
            return rankItemList;
        }

        public void setRankItemList(List<RankItem> rankItemList) {
            this.rankItemList = rankItemList;
        }

        public String getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(String totalSize) {
            this.totalSize = totalSize;
        }
    }

    public static class RankItem {
        private String changeOrder;
        private String dataType;
        private String title;

        public String getChangeOrder() {
            return changeOrder;
        }

        public void setChangeOrder(String changeOrder) {
            this.changeOrder = changeOrder;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
