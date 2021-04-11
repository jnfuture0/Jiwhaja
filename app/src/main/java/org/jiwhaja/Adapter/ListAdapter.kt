package org.jiwhaja.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.form_list_native_ad.*
import org.jiwhaja.R
import org.jiwhaja.Struct.BoardList
import java.lang.NullPointerException

class ListAdapter (val context: Context, val boardList: MutableList<BoardList>): BaseAdapter() {

    private val ITEM_TYPE_AD = 0
    private val ITEM_TYPE_NORMAL = 1



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder
        var viewType = getItemViewType(position)


        if(convertView == null){
            if (viewType == ITEM_TYPE_AD){
                view = LayoutInflater.from(context).inflate(R.layout.form_list_native_ad, null)
                holder = ViewHolder()
                //holder.ad_template = view.findViewById(R.id.my_template)
                holder.ad_banner = view.findViewById(R.id.adViewList)
                view.tag = holder


            } else{
                view = LayoutInflater.from(context).inflate(R.layout.form_list, null)
                holder = ViewHolder()
                holder.name = view.findViewById(R.id.form_list_name_textView)
                holder.category= view.findViewById(R.id.form_list_category_textView)
                holder.address = view.findViewById(R.id.form_list_address_textView)
                holder.call = view.findViewById(R.id.form_list_call_btn)
                view.tag = holder
            }


        }else{
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val boardSearch = boardList[position]
        holder.name?.text = boardSearch.name
        holder.address?.text = boardSearch.address
        holder.category?.text = boardSearch.category

        holder.call?.setOnClickListener {

            var tel = boardSearch.tel
            if(tel != "") {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
                Toast.makeText(context, "${boardSearch.name}에 전화합니다.", Toast.LENGTH_SHORT).show()
                context.startActivity(intent)
            }else{
                Toast.makeText(context, "등록된 전화번호가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        MobileAds.initialize(context, "ca-app-pub-3127429846651021~8497143095")
        var adRequest : AdRequest = AdRequest.Builder().build()
        holder.ad_banner?.loadAd(adRequest)
        return view
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return boardList[position].type_0_ad_1_list
    }

    fun showHide(view: View?){
        view?.visibility = if(view?.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }
    }

    override fun getItem(p0: Int): Any {
        return boardList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return boardList.size
    }

    private class ViewHolder{
        //var ad_template : TemplateView? = null
        var ad_banner: AdView? = null
        var call : ConstraintLayout? = null
        var address : TextView? = null
        var name : TextView? = null
        var category : TextView? = null
    }


}