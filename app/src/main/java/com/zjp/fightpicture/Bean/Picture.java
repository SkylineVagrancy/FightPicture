package com.zjp.fightpicture.Bean;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by pzh on 2017/2/21.
 */

public class Picture extends BmobObject {
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    private String pictureUrl;


    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    private String updateDate;
}
