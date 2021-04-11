package org.jiwhaja.Adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.form_list.view.form_list_address_textView
import kotlinx.android.synthetic.main.form_list.view.form_list_call_btn
import kotlinx.android.synthetic.main.form_list.view.form_list_category_textView
import kotlinx.android.synthetic.main.form_list.view.form_list_name_textView
import kotlinx.android.synthetic.main.form_list_native_ad.view.*
import org.jiwhaja.R
import org.jiwhaja.Struct.BoardList

class RecyclerAdapterList (val context: Context, val boardList: MutableList<BoardList>, val itemClick:(BoardList) -> Unit): RecyclerView.Adapter<RecyclerAdapterList.MainViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> 0
            else -> 1
        }   //test. 0-ad , 1-list
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : MainViewHolder{
        return when(viewType){
            0 -> MainViewHolder(parent, itemClick, R.layout.form_list_native_ad, 0)
            else -> MainViewHolder(parent, itemClick, R.layout.form_list, 1)
        }
    }

    override fun getItemCount(): Int = boardList.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder?.bind(boardList[position], context)
    }

    inner class MainViewHolder(parent: ViewGroup, itemClick:(BoardList) -> Unit, layout : Int, flag:Int) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layout, parent, false)
    ) {
        val flag = flag
        val boardName = itemView.form_list_name_textView
        val boardCategory = itemView.form_list_category_textView
        val boardAddress = itemView.form_list_address_textView
        val boardButton = itemView.form_list_call_btn
        val boardAdView = itemView.adViewList

        fun bind(board:BoardList, context: Context){
            if(flag == 1){
                boardName?.text = board.name
                boardCategory?.text = board.category
                boardAddress?.text = board.address

                boardButton.setOnClickListener { itemClick(board) }
                boardAddress.setOnClickListener {
                    val clipboard : ClipboardManager = context?.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData : ClipData = ClipData.newPlainText("label", boardAddress.text)
                    clipboard.primaryClip = clipData
                    Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }else{
                MobileAds.initialize(context, "ca-app-pub-3127429846651021~8497143095")
                var adRequest : AdRequest = AdRequest.Builder().build()
                boardAdView?.loadAd(adRequest)

            }
        }
    }


}