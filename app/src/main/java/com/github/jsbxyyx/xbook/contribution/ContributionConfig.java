package com.github.jsbxyyx.xbook.contribution;

import android.graphics.Paint;

import java.util.Calendar;

/**
 * 配置
 */
public class ContributionConfig {
    private int[] rankColor = new int[]{0xFFEBEDF0, 0xFF9BE9A8, 0xFF40C463, 0xFF30A14E, 0xFF216E39};
    private int[] rank = new int[]{0, 1, 3, 5, 7};
    private Paint[] paints;

    private int borderColor = 0xFF9E9E9E;
    private Paint borderPaint = new Paint();
    private int borderWidth = 2;

    private int txtColor = 0xFF9E9E9E;
    private Paint txtPaint = new Paint();
    private int txtSize = 16;

    private int selectItem = -1;
    private int selectColor = 0xFFFFFF00;
    private Paint selectPaint = new Paint();

    private String[] weeks = new String[]{"周一", "", "周三", "", "周五", "", "周天"};
    private String[] months = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};

    private int startOfWeek = Calendar.MONDAY;
    private int startEmpty;

    private int itemRound = 5;
    private int padding = 4;
    private int itemWidth;

    public static ContributionConfig defaultConfig() {
        return new ContributionConfig()
                .setBorderWidth(2)
                .setBorderColor(0xFF9E9E9E)
                .setItemRound(5)
                .setMonths(new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"})
                .setPadding(4)
                .setRank(new int[]{0, 2, 5, 8, 10})
                .setRankColor(new int[]{0xFFEBEDF0, 0xFF9BE9A8, 0xFF40C463, 0xFF30A14E, 0xFF216E39})
                .setWeeks(new String[]{"", "周一", "", "周三", "", "周五", ""})
                .setStartOfWeek(Calendar.SUNDAY)
                .setTxtColor(0xFFFF0000);
    }
    public int getSelectColor() {
        return selectColor;
    }

    public ContributionConfig setSelectColor(int selectColor) {
        this.selectColor = selectColor;
        return this;
    }

    Paint getSelectPaint() {
        return selectPaint;
    }

    ContributionConfig setSelectPaint(Paint selectPaint) {
        this.selectPaint = selectPaint;
        return this;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public ContributionConfig setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public int[] getRank() {
        return rank;
    }

    public ContributionConfig setRank(int[] rank) {
        this.rank = rank;
        return this;
    }

    public int getItemRound() {
        return itemRound;
    }

    /**
     * 单个矩形，圆角半径
     *
     * @param itemRound
     * @return
     */
    public ContributionConfig setItemRound(int itemRound) {
        this.itemRound = itemRound;
        return this;
    }

    Paint getBorderPaint() {
        return borderPaint;
    }

    ContributionConfig setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
        return this;
    }

    String[] getWeeks() {
        return weeks;
    }

    /**
     * 设置周字符串，可以为空字符串，长度为7
     *
     * @param weeks
     * @return
     */
    public ContributionConfig setWeeks(String[] weeks) {
        this.weeks = weeks;
        return this;
    }

    public String[] getMonths() {
        return months;
    }

    /**
     * 设置月字符串，可以为空字符串，长度为7
     *
     * @param months
     * @return
     */
    public ContributionConfig setMonths(String[] months) {
        this.months = months;
        return this;
    }

    int getTxtColor() {
        return txtColor;
    }

    /**
     * 设置文字颜色
     *
     * @param txtColor
     * @return
     */
    public ContributionConfig setTxtColor(int txtColor) {
        this.txtColor = txtColor;
        return this;
    }

    Paint getTxtPaint() {
        return txtPaint;
    }

    ContributionConfig setTxtPaint(Paint txtPaint) {
        this.txtPaint = txtPaint;
        return this;
    }

    Paint[] getPaints() {
        return paints;
    }

    ContributionConfig setPaints(Paint[] paints) {
        this.paints = paints;
        return this;
    }

    int getItemWidth() {
        return itemWidth;
    }

    ContributionConfig setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
        return this;
    }

    int getStartEmpty() {
        return startEmpty;
    }

    ContributionConfig setStartEmpty(int startEmpty) {
        this.startEmpty = startEmpty;
        return this;
    }

    int[] getRankColor() {
        return rankColor;
    }

    /**
     * 等级颜色数组，长度5，颜色越来越深
     *
     * @param rankColor
     * @return
     */
    public ContributionConfig setRankColor(int[] rankColor) {
        this.rankColor = rankColor;
        return this;
    }

    public int getBorderColor() {
        return borderColor;
    }

    /**
     * 单个方格边框颜色
     *
     * @param borderColor
     * @return
     */
    public ContributionConfig setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }


    public int getStartOfWeek() {
        return startOfWeek;
    }

    /**
     * 第一个星期为周一或者周日
     *
     * @param startOfWeek Calendar.MONDAY 或者 SUNDAY
     * @return
     */
    public ContributionConfig setStartOfWeek(int startOfWeek) {
        this.startOfWeek = startOfWeek;
        return this;
    }

    public int getPadding() {
        return padding;
    }

    /**
     * 设置方格之前的空隙的宽度
     *
     * @param padding
     * @return
     */
    public ContributionConfig setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    int getSelectItem() {
        return selectItem;
    }

    /**
     * 设置选中方格
     * // TODO: 2021/3/24 未实现
     *
     * @param selectItem
     * @return
     */
    public ContributionConfig setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        return this;
    }

    int getTxtSize() {
        return txtSize;
    }

    ContributionConfig setTxtSize(int txtSize) {
        this.txtSize = txtSize;
        return this;
    }

}