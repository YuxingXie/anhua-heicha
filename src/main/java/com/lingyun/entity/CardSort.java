package com.lingyun.entity;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by Administrator on 2015/12/15.
 */
public class CardSort {
    @Field
    private String noStart;
    @Field
    private String cardSort;
    @Field
    private String cardName;

    public String getNoStart() {
        return noStart;
    }

    public void setNoStart(String noStart) {
        this.noStart = noStart;
    }

    public String getCardSort() {
        return cardSort;
    }

    public void setCardSort(String cardSort) {
        this.cardSort = cardSort;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
