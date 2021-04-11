package org.jiwhaja

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_first_page.*

class FirstActivity : AppCompatActivity() {

    var filterLocation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_page)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val pref : SharedPreferences = getSharedPreferences("prefs", 0)
        filterLocation = pref.getString("location", "지역")!!
        setSpinner(first_page_spinner, R.array.phone_spinner)


        first_page_finish_btn.setOnClickListener {
            if(filterLocation == "지역"){
                Toast.makeText(this, "지역화폐 사용 지역을 선택해주세요.", Toast.LENGTH_LONG).show()
            }else{
                pref.edit().putString("location", filterLocation).apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        first_page_save_btn.setOnClickListener {
            pref.edit().putString("memo", first_page_edit_text.text.toString()).apply()
            Toast.makeText(this, "저장되었습니다. 설정 페이지에서 변경하실 수 있습니다.", Toast.LENGTH_LONG).show()
        }
    }

    fun setSpinner(spinner : Spinner, spinnerArray : Int){
        val items = resources.getStringArray(spinnerArray)
        val locationSpinnerAdapter = object: ArrayAdapter<String>(this, R.layout.form_spinner_text){
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
        locationSpinnerAdapter.add(filterLocation)
        spinner.adapter = locationSpinnerAdapter
        spinner.setSelection(locationSpinnerAdapter.count)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if( p2 != items.size) {
                    filterLocation = items[p2]
                }
            }
        }
    }
}
