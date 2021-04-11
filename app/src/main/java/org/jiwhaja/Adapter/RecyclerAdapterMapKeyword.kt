package org.jiwhaja.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.form_map_page_keyword_recyclerview.view.*
import org.jiwhaja.R
import org.jiwhaja.Struct.BoardKeyword

class RecyclerAdapterMapKeyword (val context: Context, val boardList: MutableList<BoardKeyword>, val itemClick:(BoardKeyword) -> Unit): RecyclerView.Adapter<RecyclerAdapterMapKeyword.MainViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> 1
            12 -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : MainViewHolder {
        return when(viewType){
            0 -> MainViewHolder(parent, itemClick, R.layout.form_map_page_keyword_recyclerview) // 0일 경우 기본 키워드
            else -> MainViewHolder(parent, itemClick, R.layout.form_map_page_keyword_recyclerview_temp)
        }
    }

    override fun getItemCount(): Int = boardList.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder?.bind(boardList[position], context)
    }



    inner class MainViewHolder(parent: ViewGroup, itemClick:(BoardKeyword) -> Unit, layout : Int) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layout, parent, false)
    ) {
        val boardIcon = itemView.form_map_keyword_image
        val boardText = itemView.form_map_keyword_text
        val boardLayout = itemView.form_map_whole_layout

        fun bind(board:BoardKeyword, context: Context){
            if(board.icon != ""){
                val resourceId = context.resources.getIdentifier(board.icon, "drawable", context.packageName)
                boardIcon?.setImageResource(resourceId)
            }else{
                boardIcon?.setImageResource(R.mipmap.ic_launcher)
            }
            boardText?.text = board.text
            boardLayout.setOnClickListener { itemClick(board) }

        }
    }

}