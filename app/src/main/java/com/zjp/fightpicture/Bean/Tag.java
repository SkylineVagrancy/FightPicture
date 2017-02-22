package com.zjp.fightpicture.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by pzh on 2017/2/21.
 */

public class Tag extends BmobObject {
    private String tagName;
    private String updateDate;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
