package org.jiwhaja

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.android.synthetic.main.form_list_native_ad.*
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jiwhaja.Adapter.ListAdapter
import org.jiwhaja.Adapter.RecyclerAdapterList
//import org.jiwhaja.Adapter.RecyclerAdapterList
import org.jiwhaja.Json.DataClass
import org.jiwhaja.Json.RetrofitService
import org.jiwhaja.Struct.BoardList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

class ListFragment : Fragment() {

    var boardList:MutableList<BoardList> = mutableListOf()
    var filterLocation = ""

    val regionRetrofit = Retrofit.Builder()
        .baseUrl("https://openapi.gg.go.kr")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val server: RetrofitService? = regionRetrofit.create(RetrofitService::class.java)
    val key = "93a19977f603430c8f308a47a19053af"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pref : SharedPreferences = context!!.getSharedPreferences("prefs", 0)
        filterLocation = pref.getString("location", "가평군")



        /*MobileAds.initialize(context, getString(R.string.admob_app_id))
        var adLoader:AdLoader = AdLoader.Builder(context, getString(R.string.banner_ad_unit_id_for_test))
            .forUnifiedNativeAd{ ad : UnifiedNativeAd ->
            }
            .withAdListener(object:AdListener(){
                override fun onAdFailedToLoad(errorCode: Int) {
                    super.onAdFailedToLoad(errorCode)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())*/








        list_page_search_btn.setOnClickListener {
            val searchKeyWord:String = list_page_editText.text.toString()
            if(searchKeyWord.equals("")) {
                    server?.getInfoWithLocation("json", key, 1, 300, filterLocation)
                        ?.enqueue(object : Callback<DataClass> {
                            override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }

                            override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                                var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                                boardList = mutableListOf()
                                for (j in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(j)
                                    var name = testMap2?.get("CMPNM_NM").toString()

                                    var telno = ""
                                    if (testMap2?.get("TELNO") != null) {
                                        telno = testMap2?.get("TELNO").toString()
                                    } else {
                                        telno = ""
                                    }

                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = "* 업종 정보가 없습니다"
                                    }

                                    var address: String = ""
                                    if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                        address = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                    } else {
                                        if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                            address = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                        } else {
                                            address = "* 주소 정보가 없습니다."
                                        }
                                    }
                                    if(boardList.size % 20 == 0){
                                        boardList.add(BoardList("", "", "", "", 0))
                                    }
                                    boardList.add(
                                        BoardList(name, category, address, telno, 1)
                                    )
                                }
                                try {
                                    var listAdapter = ListAdapter(context!!, boardList) /*{
                                    try {
                                        var tel = it.tel
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
                                        Toast.makeText(context, "${it.name}에 전화합니다.", Toast.LENGTH_SHORT).show()
                                        startActivity(intent)
                                    } catch (e: NullPointerException) {
                                        Log.e("ERROR_CALL", e.toString())
                                    }
                                }*/
                                    list_page_recycler_view.adapter = listAdapter
                                }catch (e:Exception){}
                                catch (e:NullPointerException){}
                            }
                        })
            }
            else{
                boardList = mutableListOf()
                for(i in 1..120) {
                    server?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                            override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                            override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                                val testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                                if (testMap?.get("row") != null) {
                                    for (j in 0 until testMap?.get("row")?.size!!.toInt()) {
                                        val testMap2 = testMap["row"]?.get(j)
                                        val name = testMap2?.get("CMPNM_NM").toString()
                                        var category: String = ""
                                        if (testMap2?.get("INDUTYPE_NM") != null) {
                                            category = testMap2?.get("INDUTYPE_NM").toString()
                                        } else {
                                            category = ""
                                        }

                                        if (name.contains(searchKeyWord) || category.contains(searchKeyWord)) {
                                            var telno = ""
                                            if (testMap2?.get("TELNO") != null) {
                                                telno = testMap2?.get("TELNO").toString()
                                            } else {
                                                telno = ""
                                            }

                                            if (category == "") {
                                                category = "* 업종 정보가 없습니다"
                                            }

                                            var address: String = ""
                                            if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                                address =
                                                    testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                            } else {
                                                if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                    address = testMap2?.get("REFINE_LOTNO_ADDR")
                                                        .toString()
                                                } else {
                                                    address = "* 주소 정보가 없습니다."
                                                }
                                            }

                                            if(boardList.size % 20 == 0){
                                                boardList.add(BoardList("", "", "", "", 0))
                                            }
                                            boardList.add(BoardList(name, category, address, telno,1))

                                            try {
                                                var listAdapter = ListAdapter(context!!, boardList) /*{
                                                try {
                                                    var tel = it.tel
                                                    val intent = Intent(
                                                        Intent.ACTION_DIAL,
                                                        Uri.parse("tel:$tel")
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "${it.name}에 전화합니다.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    startActivity(intent)
                                                } catch (e: NullPointerException) {
                                                    Log.e("ERROR_CALL", e.toString())
                                                }
                                            }*/
                                                list_page_recycler_view.adapter = listAdapter
                                            }catch (e:Exception){}
                                            catch (e:NullPointerException){}
                                        }

                                    }
                                }
                            }
                        })
                }

            }
        }


        if(boardList.size == 0) {
            var random: Random = Random()
            var randomInt = random.nextInt(10) + 1
            for (i in 1..5) {
                server?.getInfo("json", key, randomInt, 20)?.enqueue(object : Callback<DataClass> {
                    override fun onFailure(call: Call<DataClass>?, t: Throwable?) {
                        Log.e("GET_INFO_FAILED", t.toString())
                    }

                    override fun onResponse(
                        call: Call<DataClass>?,
                        response: Response<DataClass>?
                    ) {
                        boardList.add(BoardList("", "", "", "", 0))
                        var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                        for (j in 0 until testMap?.get("row")?.size!!.toInt()) {
                            var testMap2 = testMap["row"]?.get(j)
                            var name = testMap2?.get("CMPNM_NM").toString()
                            var telno = ""
                            if (testMap2?.get("TELNO") != null) {
                                telno = testMap2?.get("TELNO").toString()
                            } else {
                                telno = ""
                            }

                            var category: String = ""
                            if (testMap2?.get("INDUTYPE_NM") != null) {
                                category = testMap2?.get("INDUTYPE_NM").toString()
                            } else {
                                category = "* 업종 정보가 없습니다"
                            }

                            var address: String = ""
                            if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                address = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                            } else {
                                if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                    address = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                } else {
                                    address = "* 주소 정보가 없습니다."
                                }
                            }
                            boardList.add(
                                BoardList(name, category, address, telno, 1)
                            )
                        }
                        try {
                            var listAdapter = ListAdapter(context!!, boardList)/*{
                        if(it.tel != ""){
                            var tel = it.tel
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
                            Toast.makeText(context, "${it.name}에 전화합니다.", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                        }else{
                            Toast.makeText(context, "등록된 전화번호가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }*/
                            list_page_recycler_view.adapter = listAdapter
                        }catch(e:Exception){}
                        catch (e:NullPointerException){}
                        //Toast.makeText(context, "전화합니다.", Toast.LENGTH_SHORT).show()
                        Log.e("TIME_TEST", "in_response")
                    }
                })
            }
            //list_page_recycler_view.layoutManager = LinearLayoutManager(context)
        }
    }
}
