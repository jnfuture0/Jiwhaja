package org.jiwhaja

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_map.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.jiwhaja.Json.RetrofitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import net.daum.mf.map.api.MapView
import org.jiwhaja.Adapter.RecyclerAdapterMapKeyword
import org.jiwhaja.Json.DataClass
import org.jiwhaja.Struct.BoardKeyword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NullPointerException
import java.util.*

class MapFragment : Fragment(), MapView.MapViewEventListener, MapView.POIItemEventListener {


    val GPS_ENABLE_REQUEST_CODE = 2001
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    var latilongiMap : MutableMap<String,Array<Double>> = mutableMapOf()


    val KEYWORD_TYPE_NORMAL = 0
    val KEYWORD_TYPE_EMPTY = 1
    val keywordList:MutableList<BoardKeyword> = mutableListOf(
        BoardKeyword("","", -0, KEYWORD_TYPE_EMPTY),
        BoardKeyword("icon_food","음식점", 0, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_cafe","카페", 1, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_hospital","병원", 2, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_medicine","약국", 3, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_mart","편의점/마트", 4, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_bakery","베이커리", 5, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_beauti","미용", 6, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_flower","꽃집", 7, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_gas_station","주유소", 8, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_service","서비스", 9, KEYWORD_TYPE_NORMAL),
        BoardKeyword("icon_sports","스포츠/레져", 10, KEYWORD_TYPE_NORMAL),
        BoardKeyword("","", -0, KEYWORD_TYPE_EMPTY)
    )

    var filterLocation :String = ""
    var filterBig : String = ""
    var filterSmall : String = ""
    lateinit var nowPOIItem : MapPOIItem

    companion object{
        val regionRetrofit = Retrofit.Builder()
            .baseUrl("https://openapi.gg.go.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var regionServer: RetrofitService? = regionRetrofit.create(RetrofitService::class.java)
        val key = "93a19977f603430c8f308a47a19053af"

        var wholeNameList :MutableList<String> = mutableListOf()
        /*val searchRetrofit = Retrofit.Builder()
            .baseUrl("daummaps://")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var searchServer: RetrofitService? = searchRetrofit.create(RetrofitService::class.java)*/

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*권한 체크*/
        //if(!checkLocationServiceStatus()){ showDialogForLocationServiceSetting() } else{ checkRunTimePermission() }


        var permissionCheck1 = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        var permissionCheck2= ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION)

        if(permissionCheck1 != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(this.requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
            }
        }

        if(permissionCheck2 != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(this.requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
            }
        }



        latilongiMap["가평군"] = arrayOf(37.831283, 127.509549)
        latilongiMap["고양시"] = arrayOf(37.658351, 126.831971)
        latilongiMap["과천시"] = arrayOf(37.429188, 126.987590)
        latilongiMap["광명시"] = arrayOf(37.478552, 126.864672)
        latilongiMap["광주시"] = arrayOf(37.429396, 127.255134)
        latilongiMap["구리시"] = arrayOf(37.594299, 127.129574)
        latilongiMap["군포시"] = arrayOf(37.361664, 126.935174)
        latilongiMap["김포시"] = arrayOf(37.615239, 126.715629)
        latilongiMap["남양주시"] = arrayOf(37.635974, 127.216522)
        latilongiMap["동두천시"] = arrayOf(37.903556, 127.060379)
        latilongiMap["부천시"] = arrayOf(37.503363, 126.765956)
        latilongiMap["성남시"] = arrayOf(37.419988, 127.126608)
        latilongiMap["수원시"] = arrayOf(37.263389, 127.028563)
        latilongiMap["시흥시"] = arrayOf(37.380085, 126.802876)
        latilongiMap["안산시"] = arrayOf(37.321840, 126.830868)
        latilongiMap["안성시"] = arrayOf(37.007990, 127.279813)
        latilongiMap["안양시"] = arrayOf(37.394300, 126.956844)
        latilongiMap["양주시"] = arrayOf(37.785279, 127.045782)
        latilongiMap["양평군"] = arrayOf(37.491685, 127.487512)
        latilongiMap["여주시"] = arrayOf(37.298131, 127.637329)
        latilongiMap["연천군"] = arrayOf(38.096393, 127.075045)
        latilongiMap["오산시"] = arrayOf(37.149824, 127.077471)
        latilongiMap["용인시"] = arrayOf(37.241015, 127.177932)
        latilongiMap["의왕시"] = arrayOf(37.344690, 126.968310)
        latilongiMap["의정부시"] = arrayOf(37.738079, 127.033716)
        latilongiMap["이천시"] = arrayOf(37.272234, 127.435049)
        latilongiMap["파주시"] = arrayOf(37.759852, 126.779871)
        latilongiMap["평택시"] = arrayOf(36.992324, 127.112694)
        latilongiMap["포천시"] = arrayOf(37.894903, 127.200352)
        latilongiMap["하남시"] = arrayOf(37.539316, 127.214946)
        latilongiMap["화성시"] = arrayOf(37.199426, 126.831685)



        val mapView = MapView(activity)
        val mapViewContainer = map_view
        mapViewContainer.addView(mapView)

        /*var lm :LocationManager= activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var userLocation:Location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        var latitude = userLocation.latitude
        var longitude = userLocation.longitude*/



        val pref : SharedPreferences = context!!.getSharedPreferences("prefs", 0)
        filterLocation = pref.getString("location", "가평군")


        Log.e("CHECK_LONG_ARRAY", "filterLocation = $filterLocation")
        Log.e("CHECK_LONG_ARRAY", "array = ${latilongiMap[filterLocation]}")
        if(filterLocation != "") {
            var longlatiArr: Array<Double> = latilongiMap[filterLocation]!!
            var latitude = longlatiArr[0]
            var longitude = longlatiArr[1]
            val nowPosition = MapPoint.mapPointWithGeoCoord(latitude, longitude)
            mapView.setMapCenterPoint(nowPosition, true)
        }


        var keywordAdapter = RecyclerAdapterMapKeyword(context!!, keywordList){
            mapView.removeAllPOIItems()
            detail_layout.visibility = View.GONE

            when(it.index){
                0 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("음식") ||
                                        category.contains("중식") ||
                                        category.contains("분식")) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())
                            }
                        }
                    })
                }}  //음식점
                1 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("서양")||
                                        category.contains("서양음식") ||
                                        category.contains("디저트") ||
                                        category.contains("커피") ||
                                        category.contains("카페") ||
                                        category.contains("베이커리")) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())

                            }
                        }
                    })
                }}  //카페
                2 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("병원") ||
                                        category.contains("의원") ||
                                        category.contains("산모") ||
                                        category.contains("의료")) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())


                            }
                        }
                    })
                }}  //병원
                3 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("약국")) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())

                            }
                        }
                    })
                }}  //약국
                4 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("유통업") ||
                                        category.contains("마트") ||
                                        category.contains("시장") ||
                                        category.contains("편의점") ||
                                        category.contains("편 의 점") ||
                                        category.contains("슈퍼") ) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())


                            }
                        }
                    })
                }}  //편의점, 마트
                5 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("제과점") ||
                                        category.contains("제과") ||
                                        category.contains("베이커리") ) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())


                            }
                        }
                    })
                }}  //베이커리
                6 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("미용") ||
                                        category.contains("보건위생") ||
                                        category.contains("네일") ||
                                        category.contains("피부") ||
                                        category.contains("헤어") ) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())


                            }
                        }
                    })
                }}  //미용
                7 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("꽃") ||
                                        category.contains("화원") ||
                                        category.contains("화훼")) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())

                            }
                        }
                    })
                }}  //꽃집
                8 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("연료판매점") ||
                                        category.contains("주유소") ) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())

                            }
                        }
                    })
                }}  //주유소
                9 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("서비스") ) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())

                            }
                        }
                    })
                }}  //서비스
                10 -> {for(i in 1..150) {
                    regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            if (testMap?.get("row") != null) {
                                for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                    var testMap2 = testMap["row"]?.get(i)
                                    val name = testMap2?.get("CMPNM_NM").toString()
                                    var category: String = ""
                                    if (testMap2?.get("INDUTYPE_NM") != null) {
                                        category = testMap2?.get("INDUTYPE_NM").toString()
                                    } else {
                                        category = ""
                                    }

                                    if ( category.contains("레져") ||
                                        category.contains("회원제") ||
                                        category.contains("스포츠") ) {
                                        var latitudeString: String = ""
                                        var longitudeString: String = ""
                                        var latitudeDouble: Double = 0.0
                                        var longitudeDouble: Double = 0.0

                                        /*위도,경도,주소 값 null인지 아닌지 확인*/
                                        if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                            continue
                                        } else {
                                            latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                            longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                            latitudeDouble = latitudeString.toDouble()
                                            longitudeDouble = longitudeString.toDouble()
                                        }

                                        var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                        if (testMap2?.get("TELNO") != null) {
                                            userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                        } else {
                                            userObjectMap["tel"] = ""
                                        }


                                        if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                            userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                        } else {
                                            if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                            } else {
                                                userObjectMap["address"] = "* 주소 정보가 없습니다."
                                            }
                                        }

                                        if (category != "") {
                                            userObjectMap["category"] = category
                                        } else {
                                            userObjectMap["category"] = "* 업종 정보가 없습니다"
                                        }

                                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                                            latitudeDouble,
                                            longitudeDouble
                                        )
                                        val marker = MapPOIItem()
                                        marker.customImageResourceId =
                                            R.drawable.marker_pink_new
                                        marker.customSelectedImageResourceId =
                                            R.drawable.marker_blue_new
                                        marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                        marker.tag = 0
                                        marker.mapPoint = mapPoint
                                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                        marker.userObject = userObjectMap

                                        markerMutableList.add(marker)
                                    }
                                }
                                mapView.addPOIItems(markerMutableList.toTypedArray())

                            }
                        }
                    })
                }}  //스포츠,레져
                else -> {}
            }
        }
        map_page_keyword_recyclerView.adapter = keywordAdapter
        map_page_keyword_recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        /*필터 스피너 세팅*/
        setSpinner(map_page_spinner_1, R.array.filter_spinner)
        setSecondSpinner(map_page_spinner_2, R.array.filter_small_spinner)


        map_page_search_btn.setOnClickListener {
            val searchKeyWord:String = map_page_editText.text.toString()
            detail_layout.visibility = View.GONE
            mapView.removeAllPOIItems()
            if(searchKeyWord.equals("")) {
                if(filterBig == ""){ // 키워드x 필터x
                    var random :Random = Random()
                    var randomInt = random.nextInt(10) + 1
                    regionServer?.getInfoWithLocation("json", key, randomInt, 300, filterLocation)?.enqueue(object : Callback<DataClass> {
                        override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                        override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                            var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                            var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                            for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                var testMap2 = testMap["row"]?.get(i)
                                var latitudeString: String = ""
                                var longitudeString: String = ""
                                var latitudeDouble: Double = 0.0
                                var longitudeDouble: Double = 0.0

                                /*위도,경도,주소 값 null인지 아닌지 확인*/
                                if(testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null){
                                    continue
                                }else{
                                    latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                    longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                    latitudeDouble = latitudeString.toDouble()
                                    longitudeDouble = longitudeString.toDouble()
                                }

                                var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                if(testMap2?.get("TELNO") != null) {
                                    userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                }else{
                                    userObjectMap["tel"] = ""
                                }


                                if(testMap2?.get("REFINE_ROADNM_ADDR") != null){
                                    userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                }else{
                                    if(testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                        userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                    }else {
                                        userObjectMap["address"] = "* 주소 정보가 없습니다."
                                    }
                                }

                                if (testMap2?.get("INDUTYPE_NM") != null) {
                                    userObjectMap["category"] = testMap2?.get("INDUTYPE_NM").toString()
                                }else{
                                    userObjectMap["category"] = "* 업종 정보가 없습니다"
                                }

                                val mapPoint = MapPoint.mapPointWithGeoCoord(latitudeDouble, longitudeDouble)
                                val marker = MapPOIItem()
                                marker.customImageResourceId = R.drawable.marker_pink_new
                                marker.customSelectedImageResourceId = R.drawable.marker_blue_new
                                marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                marker.tag = 0
                                marker.mapPoint = mapPoint
                                marker.markerType = MapPOIItem.MarkerType.CustomImage
                                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                marker.userObject = userObjectMap
                                markerMutableList.add(marker)
                            }
                            mapView.addPOIItems(markerMutableList.toTypedArray())
                        }
                    })
                    Toast.makeText(context, "검색 결과가 너무 많아 임의의 300개만 표시하였습니다.", Toast.LENGTH_LONG).show()
                }else{  // 대분류 있음
                    if(filterSmall != ""){ //대분류, 소분류 있음
                        for(i in 1..150) {
                            regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                                override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                                override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                                    var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                                    var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                                    if (testMap?.get("row") != null) {
                                        for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                            var testMap2 = testMap["row"]?.get(i)
                                            val name = testMap2?.get("CMPNM_NM").toString()
                                            var category: String = ""
                                            if (testMap2?.get("INDUTYPE_NM") != null) {
                                                category = testMap2?.get("INDUTYPE_NM").toString()
                                            } else {
                                                category = ""
                                            }

                                            if ( category.contains(filterSmall) && category.contains(filterBig)) {
                                                var latitudeString: String = ""
                                                var longitudeString: String = ""
                                                var latitudeDouble: Double = 0.0
                                                var longitudeDouble: Double = 0.0

                                                /*위도,경도,주소 값 null인지 아닌지 확인*/
                                                if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                                    continue
                                                } else {
                                                    latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                                    longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                                    latitudeDouble = latitudeString.toDouble()
                                                    longitudeDouble = longitudeString.toDouble()
                                                }

                                                var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                                if (testMap2?.get("TELNO") != null) {
                                                    userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                                } else {
                                                    userObjectMap["tel"] = ""
                                                }


                                                if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                                    userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                                } else {
                                                    if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                        userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                                    } else {
                                                        userObjectMap["address"] = "* 주소 정보가 없습니다."
                                                    }
                                                }

                                                if (category != "") {
                                                    userObjectMap["category"] = category
                                                } else {
                                                    userObjectMap["category"] = "* 업종 정보가 없습니다"
                                                }

                                                val mapPoint = MapPoint.mapPointWithGeoCoord(
                                                    latitudeDouble,
                                                    longitudeDouble
                                                )
                                                val marker = MapPOIItem()
                                                marker.customImageResourceId =
                                                    R.drawable.marker_pink_new
                                                marker.customSelectedImageResourceId =
                                                    R.drawable.marker_blue_new
                                                marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                                marker.tag = 0
                                                marker.mapPoint = mapPoint
                                                marker.markerType = MapPOIItem.MarkerType.CustomImage
                                                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                                marker.userObject = userObjectMap

                                                markerMutableList.add(marker)
                                            }
                                        }
                                        mapView.addPOIItems(markerMutableList.toTypedArray())


                                    }
                                }
                            })
                        }
                    }else{  //대분류만 있음
                        for(i in 1..150) {
                            regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                                override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                                override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                                    var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                                    var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                                    if (testMap?.get("row") != null) {
                                        for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                            var testMap2 = testMap["row"]?.get(i)
                                            val name = testMap2?.get("CMPNM_NM").toString()
                                            var category: String = ""
                                            if (testMap2?.get("INDUTYPE_NM") != null) {
                                                category = testMap2?.get("INDUTYPE_NM").toString()
                                            } else {
                                                category = ""
                                            }

                                            if ( category.contains(filterBig)) {
                                                var latitudeString: String = ""
                                                var longitudeString: String = ""
                                                var latitudeDouble: Double = 0.0
                                                var longitudeDouble: Double = 0.0

                                                /*위도,경도,주소 값 null인지 아닌지 확인*/
                                                if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                                    continue
                                                } else {
                                                    latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                                    longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                                    latitudeDouble = latitudeString.toDouble()
                                                    longitudeDouble = longitudeString.toDouble()
                                                }

                                                var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                                if (testMap2?.get("TELNO") != null) {
                                                    userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                                } else {
                                                    userObjectMap["tel"] = ""
                                                }


                                                if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                                    userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                                } else {
                                                    if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                        userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                                    } else {
                                                        userObjectMap["address"] = "* 주소 정보가 없습니다."
                                                    }
                                                }

                                                if (category != "") {
                                                    userObjectMap["category"] = category
                                                } else {
                                                    userObjectMap["category"] = "* 업종 정보가 없습니다"
                                                }

                                                val mapPoint = MapPoint.mapPointWithGeoCoord(
                                                    latitudeDouble,
                                                    longitudeDouble
                                                )
                                                val marker = MapPOIItem()
                                                marker.customImageResourceId =
                                                    R.drawable.marker_pink_new
                                                marker.customSelectedImageResourceId =
                                                    R.drawable.marker_blue_new
                                                marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                                marker.tag = 0
                                                marker.mapPoint = mapPoint
                                                marker.markerType = MapPOIItem.MarkerType.CustomImage
                                                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                                marker.userObject = userObjectMap

                                                markerMutableList.add(marker)
                                            }
                                        }
                                        mapView.addPOIItems(markerMutableList.toTypedArray())


                                    }
                                }
                            })
                        }
                    }
                }
            }
            else{
                if(filterBig == ""){ // 키워드o 필터x
                    for(i in 1..150) {
                        regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                            override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                            override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                                var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                                var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                                if (testMap?.get("row") != null) {
                                    for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                        var testMap2 = testMap["row"]?.get(i)
                                        val name = testMap2?.get("CMPNM_NM").toString()
                                        var category: String = ""
                                        if (testMap2?.get("INDUTYPE_NM") != null) {
                                            category = testMap2?.get("INDUTYPE_NM").toString()
                                        } else {
                                            category = ""
                                        }

                                        if (name.contains(searchKeyWord) || category.contains(searchKeyWord)) {
                                            var latitudeString: String = ""
                                            var longitudeString: String = ""
                                            var latitudeDouble: Double = 0.0
                                            var longitudeDouble: Double = 0.0

                                            /*위도,경도,주소 값 null인지 아닌지 확인*/
                                            if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                                continue
                                            } else {
                                                latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                                longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                                latitudeDouble = latitudeString.toDouble()
                                                longitudeDouble = longitudeString.toDouble()
                                            }

                                            var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                            if (testMap2?.get("TELNO") != null) {
                                                userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                            } else {
                                                userObjectMap["tel"] = ""
                                            }


                                            if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                                userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                            } else {
                                                if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                    userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                                } else {
                                                    userObjectMap["address"] = "* 주소 정보가 없습니다."
                                                }
                                            }

                                            if (category != "") {
                                                userObjectMap["category"] = category
                                            } else {
                                                userObjectMap["category"] = "* 업종 정보가 없습니다"
                                            }

                                            val mapPoint = MapPoint.mapPointWithGeoCoord(
                                                latitudeDouble,
                                                longitudeDouble
                                            )
                                            val marker = MapPOIItem()
                                            marker.customImageResourceId =
                                                R.drawable.marker_pink_new
                                            marker.customSelectedImageResourceId =
                                                R.drawable.marker_blue_new
                                            marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                            marker.tag = 0
                                            marker.mapPoint = mapPoint
                                            marker.markerType = MapPOIItem.MarkerType.CustomImage
                                            marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                            marker.userObject = userObjectMap

                                            markerMutableList.add(marker)
                                        }
                                    }
                                    mapView.addPOIItems(markerMutableList.toTypedArray())


                                }
                            }
                        })
                    }
                }else{  // 대분류 있음
                    if(filterSmall != ""){ //대분류, 소분류 있음
                        for(i in 1..150) {
                            regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                                override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                                override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                                    var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                                    var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                                    if (testMap?.get("row") != null) {
                                        for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                            var testMap2 = testMap["row"]?.get(i)
                                            val name = testMap2?.get("CMPNM_NM").toString()
                                            var category: String = ""
                                            if (testMap2?.get("INDUTYPE_NM") != null) {
                                                category = testMap2?.get("INDUTYPE_NM").toString()
                                            } else {
                                                category = ""
                                            }

                                            if ((name.contains(searchKeyWord) || category.contains(searchKeyWord)) && category.contains(filterSmall) && category.contains(filterBig)) {
                                                var latitudeString: String = ""
                                                var longitudeString: String = ""
                                                var latitudeDouble: Double = 0.0
                                                var longitudeDouble: Double = 0.0

                                                /*위도,경도,주소 값 null인지 아닌지 확인*/
                                                if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                                    continue
                                                } else {
                                                    latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                                    longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                                    latitudeDouble = latitudeString.toDouble()
                                                    longitudeDouble = longitudeString.toDouble()
                                                }

                                                var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                                if (testMap2?.get("TELNO") != null) {
                                                    userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                                } else {
                                                    userObjectMap["tel"] = ""
                                                }


                                                if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                                    userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                                } else {
                                                    if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                        userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                                    } else {
                                                        userObjectMap["address"] = "* 주소 정보가 없습니다."
                                                    }
                                                }

                                                if (category != "") {
                                                    userObjectMap["category"] = category
                                                } else {
                                                    userObjectMap["category"] = "* 업종 정보가 없습니다"
                                                }

                                                val mapPoint = MapPoint.mapPointWithGeoCoord(
                                                    latitudeDouble,
                                                    longitudeDouble
                                                )
                                                val marker = MapPOIItem()
                                                marker.customImageResourceId =
                                                    R.drawable.marker_pink_new
                                                marker.customSelectedImageResourceId =
                                                    R.drawable.marker_blue_new
                                                marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                                marker.tag = 0
                                                marker.mapPoint = mapPoint
                                                marker.markerType = MapPOIItem.MarkerType.CustomImage
                                                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                                marker.userObject = userObjectMap

                                                markerMutableList.add(marker)
                                            }
                                        }
                                        mapView.addPOIItems(markerMutableList.toTypedArray())


                                    }
                                }
                            })
                        }
                    }else{  //대분류만 있음
                        for(i in 1..150) {
                            regionServer?.getInfoWithLocation("json", key, i, 999, filterLocation)?.enqueue(object : Callback<DataClass> {
                                override fun onFailure(call: Call<DataClass>, t: Throwable) { Log.e("GET_INFO_LOCATE_FAILED", t.toString()) }
                                override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                                    var markerMutableList: MutableList<MapPOIItem> = mutableListOf()
                                    var testMap = response?.body()?.RegionMnyFacltStus?.get(1)
                                    if (testMap?.get("row") != null) {
                                        for (i in 0 until testMap?.get("row")?.size!!.toInt()) {
                                            var testMap2 = testMap["row"]?.get(i)
                                            val name = testMap2?.get("CMPNM_NM").toString()
                                            var category: String = ""
                                            if (testMap2?.get("INDUTYPE_NM") != null) {
                                                category = testMap2?.get("INDUTYPE_NM").toString()
                                            } else {
                                                category = ""
                                            }

                                            if ((name.contains(searchKeyWord) || category.contains(searchKeyWord)) && category.contains(filterBig)) {
                                                var latitudeString: String = ""
                                                var longitudeString: String = ""
                                                var latitudeDouble: Double = 0.0
                                                var longitudeDouble: Double = 0.0

                                                /*위도,경도,주소 값 null인지 아닌지 확인*/
                                                if (testMap2?.get("REFINE_WGS84_LAT") == null || testMap2?.get("REFINE_WGS84_LOGT") == null) {
                                                    continue
                                                } else {
                                                    latitudeString = testMap2?.get("REFINE_WGS84_LAT").toString()
                                                    longitudeString = testMap2?.get("REFINE_WGS84_LOGT").toString()
                                                    latitudeDouble = latitudeString.toDouble()
                                                    longitudeDouble = longitudeString.toDouble()
                                                }

                                                var userObjectMap: MutableMap<String, Any> = mutableMapOf()

                                                if (testMap2?.get("TELNO") != null) {
                                                    userObjectMap["tel"] = testMap2?.get("TELNO").toString()
                                                } else {
                                                    userObjectMap["tel"] = ""
                                                }


                                                if (testMap2?.get("REFINE_ROADNM_ADDR") != null) {
                                                    userObjectMap["address"] = testMap2?.get("REFINE_ROADNM_ADDR").toString()
                                                } else {
                                                    if (testMap2?.get("REFINE_LOTNO_ADDR") != null) {
                                                        userObjectMap["address"] = testMap2?.get("REFINE_LOTNO_ADDR").toString()
                                                    } else {
                                                        userObjectMap["address"] = "* 주소 정보가 없습니다."
                                                    }
                                                }

                                                if (category != "") {
                                                    userObjectMap["category"] = category
                                                } else {
                                                    userObjectMap["category"] = "* 업종 정보가 없습니다"
                                                }

                                                val mapPoint = MapPoint.mapPointWithGeoCoord(
                                                    latitudeDouble,
                                                    longitudeDouble
                                                )
                                                val marker = MapPOIItem()
                                                marker.customImageResourceId =
                                                    R.drawable.marker_pink_new
                                                marker.customSelectedImageResourceId =
                                                    R.drawable.marker_blue_new
                                                marker.itemName = testMap2?.get("CMPNM_NM").toString()
                                                marker.tag = 0
                                                marker.mapPoint = mapPoint
                                                marker.markerType = MapPOIItem.MarkerType.CustomImage
                                                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                                                marker.userObject = userObjectMap

                                                markerMutableList.add(marker)
                                            }
                                        }
                                        mapView.addPOIItems(markerMutableList.toTypedArray())

                                    }
                                }
                            })
                        }
                    }
                }
            }
        }

        /*맨위 필터 버튼*/
        map_page_filter_btn.setOnClickListener{ changeLayoutVisibility(map_page_filter_layout) }
        /*필터 완료 버튼*/
        map_page_filter_finish_btn.setOnClickListener { map_page_filter_layout.visibility = View.GONE }
        /*줌인 버튼*/
        zoom_in_btn.setOnClickListener { mapView.zoomIn(true) }
        /*줌아웃 버튼*/
        zoom_out_btn.setOnClickListener { mapView.zoomOut(true) }
        /*위치 버튼*/
        map_page_location_btn.setOnClickListener {
            var permissionCheck1 = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
            var permissionCheck2= ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION)
            if(permissionCheck1 == PackageManager.PERMISSION_GRANTED && permissionCheck2 == PackageManager.PERMISSION_GRANTED) {
                var lm: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                try {
                    if (mapView.currentLocationTrackingMode == MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving) {
                        mapView.currentLocationTrackingMode =
                            MapView.CurrentLocationTrackingMode.TrackingModeOff
                        mapView.setShowCurrentLocationMarker(false)
                        map_page_location_img.setImageResource(R.drawable.location_icon)
                    } else {
                        mapView.currentLocationTrackingMode =
                            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
                        mapView.setShowCurrentLocationMarker(true)
                        map_page_location_img.setImageResource(R.drawable.location2_icon)
                        var userNowLocation: Location =
                            lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        var uLatitude = userNowLocation.latitude
                        var uLongitude = userNowLocation.longitude
                        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)
                        mapView.setMapCenterPoint(uNowPosition, true)
                    }
                }catch(e:NullPointerException){
                    Log.e("LOCATION_ERROR", e.toString())
                    activity?.let{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity(it)
                        }else{
                            ActivityCompat.finishAffinity(it)
                        }
                    }
                    activity?.let{
                        val intent = Intent(it, MainActivity::class.java)
                        it.startActivity(intent)
                    }
                    System.exit(0)
                }
            }else{
                Toast.makeText(context, "위치 권한이 없습니다.\n[설정 - 어플리케이션 - 지화자]에서 위치 권한을 켜주세요.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this.requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
            }
        }
        /*전화번호 버튼*/
        main_call_btn.setOnClickListener {
            val map: MutableMap<String, Any> = nowPOIItem.userObject as MutableMap<String, Any>
            if(map["tel"] != ""){
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + map["tel"].toString()))
                Toast.makeText(context, "${nowPOIItem.itemName}에 전화합니다.", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            } else{
                Toast.makeText(context, "등록된 전화번호가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        /*주소 버튼*/
        address_textView.setOnClickListener {
            val clipboard :ClipboardManager = context?.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData :ClipData = ClipData.newPlainText("label", address_textView.text)
            clipboard.primaryClip = clipData
            Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
        /*주소 옆 복사 버튼*/
        map_page_duplicate_btn.setOnClickListener {
            val clipboard :ClipboardManager = context?.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData :ClipData = ClipData.newPlainText("label", address_textView.text)
            clipboard.primaryClip = clipData
            Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
        /*마커 클릭 시*/
        mapView.setPOIItemEventListener(this)
        /*맵 터치 시*/
        mapView.setMapViewEventListener(this)
    }




    fun setSecondSpinner(spinner:Spinner, spinnerArray:Int){
        val items = resources.getStringArray(spinnerArray)
        val locationSpinnerAdapter = object: ArrayAdapter<String>(context, R.layout.form_spinner_text){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    //마지막 포지션의 textView 를 힌트 용으로 사용합니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).text = ""
                    //아이템의 마지막 값을 불러와 hint로 추가해 줍니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                //마지막 아이템은 힌트용으로만 사용하기 때문에 getCount에 1을 빼줍니다.
                return super.getCount() - 1
            }
        }
        locationSpinnerAdapter.addAll(items.toMutableList())
        locationSpinnerAdapter.add("소분류")
        spinner.adapter = locationSpinnerAdapter
        spinner.setSelection(locationSpinnerAdapter.count)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if( position != items.size) {
                    filterSmall = items[position]
                }
            }
        }
    }


    fun setSpinner(spinner : Spinner, spinnerArray : Int){
        val items = resources.getStringArray(spinnerArray)
        val locationSpinnerAdapter = object: ArrayAdapter<String>(context, R.layout.form_spinner_text){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    //마지막 포지션의 textView 를 힌트 용으로 사용합니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).text = ""
                    //아이템의 마지막 값을 불러와 hint로 추가해 줍니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                //마지막 아이템은 힌트용으로만 사용하기 때문에 getCount에 1을 빼줍니다.
                return super.getCount() - 1
            }
        }
        locationSpinnerAdapter.addAll(items.toMutableList())
        locationSpinnerAdapter.add("대분류")
        spinner.adapter = locationSpinnerAdapter
        spinner.setSelection(locationSpinnerAdapter.count)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if( position != items.size) {
                    filterBig = items[position]
                    filterSmall = ""
                    when(position){
                        0 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_0_spinner)}
                        1 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_1_spinner)}
                        2 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_2_spinner)}
                        3 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_3_spinner)}
                        4 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_4_spinner)}
                        5 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_5_spinner)}
                        6 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_6_spinner)}
                        7 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_7_spinner)}
                        8 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_8_spinner)}
                        9 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_9_spinner)}
                        10 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_10_spinner)}
                        11 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_11_spinner)}
                        12 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_12_spinner)}
                        13 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_13_spinner)}
                        14 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_14_spinner)}
                        15 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_15_spinner)}
                        16 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_16_spinner)}
                        17 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_17_spinner)}
                        18 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_18_spinner)}
                        19 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_19_spinner)}
                        20 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_20_spinner)}
                        21 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_21_spinner)}
                        22 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_22_spinner)}
                        23 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_23_spinner)}
                        24 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_24_spinner)}
                        25 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_25_spinner)}
                        26 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_26_spinner)}
                        27 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_27_spinner)}
                        28 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_28_spinner)}
                        29 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_29_spinner)}
                        30 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_30_spinner)}
                        31 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_31_spinner)}
                        32 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_32_spinner)}
                        33 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_33_spinner)}
                        34 -> {setSecondSpinner(map_page_spinner_2, R.array.filter_34_spinner)}
                        else -> {setSecondSpinner(map_page_spinner_2, R.array.filter_small_spinner)}
                    }
                }
            }
        }
    }


    fun changeLayoutVisibility(layout :LinearLayout){
        if(layout.visibility == View.VISIBLE) layout.visibility = View.GONE
        else layout.visibility = View.VISIBLE
    }

    /*setMapViewEventListener*/
    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewInitialized(p0: MapView?) {}
    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) { detail_layout.visibility = View.GONE }
    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}
    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}

    /*setPOIItemEventListener*/
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {}
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        detail_layout.visibility = View.VISIBLE
        name_textView.text = p1?.itemName.toString()
        var userObjectMap : MutableMap<String, Any> = p1?.userObject as MutableMap<String, Any>
        category_textView.text = userObjectMap["category"].toString()
        /*var underline :SpannableString = SpannableString(userObjectMap["address"].toString())
        underline.setSpan(UnderlineSpan(), 0, underline.length, 0)*/
        address_textView.text = userObjectMap["address"].toString()
        nowPOIItem = p1!!
    }



    /*private fun checkLocationServiceStatus():Boolean{
        var lm:LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //var lm:LocationManager = getSystemService(this.context!!) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showDialogForLocationServiceSetting(){
        var builder :AlertDialog.Builder = AlertDialog.Builder(this.context!!)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?")
        builder.setCancelable(true)
        builder.setPositiveButton("설정"){dialog, which ->
            val callGPSSettingIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소"){dialog, which ->
            dialog.cancel()
        }
        builder.create().show()
    }

    fun checkRunTimePermission(){
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){

        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), REQUIRED_PERMISSIONS[0])){
                Toast.makeText(context, "위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(this.requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }else{
                ActivityCompat.requestPermissions(this.requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }
        }
    }*/


}

