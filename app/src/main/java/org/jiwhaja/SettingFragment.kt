package org.jiwhaja

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
/*import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds*/
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

    companion object{
        var filterLocation : String = "가평군"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref : SharedPreferences = context!!.getSharedPreferences("prefs", 0)
        filterLocation = pref.getString("location", "가평군")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pref : SharedPreferences = context!!.getSharedPreferences("prefs", 0)
        setting_page_memo_textView.text = pref.getString("memo","수정 버튼을 눌러보세요")
        setting_page_btn.setOnClickListener {
            changeView(setting_page_memo_textView, setting_page_memo_editText, setting_page_btn, pref)
        }

        MobileAds.initialize(context, getString(R.string.admob_app_id))
        var adRequest : AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)


        val items = resources.getStringArray(R.array.phone_spinner)
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
        locationSpinnerAdapter.add(filterLocation)
        setting_page_spinner.adapter = locationSpinnerAdapter
        setting_page_spinner.setSelection(locationSpinnerAdapter.count)
        setting_page_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if( p2 != items.size) {
                    filterLocation = items[p2]
                    pref.edit().putString("location", filterLocation).apply()
                }
            }
        }

    }

    fun changeView(textView : TextView, editText:EditText, btn :Button, pref :SharedPreferences){
        if(textView.visibility == View.GONE && editText.visibility == View.VISIBLE) { // 완료 버튼 눌렀을 때
            textView.text = editText.text
            textView.visibility = View.VISIBLE
            editText.visibility = View.GONE
            pref.edit().putString("memo",editText.text.toString()).apply()
            btn.text = "수정"
        }else if(editText.visibility == View.GONE && textView.visibility == View.VISIBLE){
            val text = textView.text.toString()
            if(editText.text != "수정 버튼을 눌러보세요".toEditable()) {
                editText.text = text.toEditable()
            }
            editText.visibility = View.VISIBLE
            textView.visibility = View.GONE
            btn.text = "저장"
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}
