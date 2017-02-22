package com.zjp.fightpicture.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by pzh on 2017/2/21.
 */

public class Relation extends BmobObject {
    private String pictureUrl;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    private String tagName;

}
