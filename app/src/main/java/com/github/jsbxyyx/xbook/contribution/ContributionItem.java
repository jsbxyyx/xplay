package com.github.jsbxyyx.xbook.contribution;

import java.util.Date;

/**
 * 方格
 */
public class ContributionItem {

    private Date time;
    private int number;
    private int row;
    private int col;

    private Object data;

    public ContributionItem(Date time, int number) {
        this.time = time;
        this.number = number;
    }

    public ContributionItem(Date time, int number, Object data) {
        this.time = time;
        this.number = number;
        this.data = data;
    }

    public Date getTime() {
        return time;
    }

    public ContributionItem setTime(Date time) {
        this.time = time;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public ContributionItem setNumber(int number) {
        this.number = number;
        return this;
    }

    public int getRow() {
        return row;
    }

    public ContributionItem setRow(int row) {
        this.row = row;
        return this;
    }

    public int getCol() {
        return col;
    }

    public ContributionItem setCol(int col) {
        this.col = col;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ContributionItem{" +
                "time=" + time +
                ", number=" + number +
                ", row=" + row +
                ", col=" + col +
                ", data=" + data +
                '}';
    }

}