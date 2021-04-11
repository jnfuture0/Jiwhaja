package org.jiwhaja.Json

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("/RegionMnyFacltStus")
    fun getInfo(@Query("Type")Type:String,
                @Query("KEY")KEY:String,
                @Query("pIndex")pIndex:Int,
                @Query("pSize")pSize:Int) : Call<DataClass>

    @GET("/RegionMnyFacltStus")
    fun getInfoWithLocation(@Query("Type")Type:String,
                            @Query("KEY")KEY:String,
                            @Query("pIndex")pIndex:Int,
                            @Query("pSize")pSize:Int,
                            @Query("SIGUN_NM")SIGUN_NM:String) : Call<DataClass>

    @GET("search")
    fun searchMap(@Query("q")q:String) : Call<SearchMap>
}
