package com.zjp.fightpicture.net;

import com.zjp.fightpicture.Bean.ResultBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by pzh on 2017/2/13.
 */

public interface NetServer {
    @GET("http://image.baidu.com/search/avatarjson?tn=resultjsonavatarnew")
    Call<ResultBean> getPicture(@Query("rn") int rn, @Query("pn") int pn, @Query("word") String word);
}
