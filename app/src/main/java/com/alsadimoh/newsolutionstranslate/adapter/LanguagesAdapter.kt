package com.alsadimoh.newsolutionstranslate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.newsolutionstranslate.databinding.ItemLanguageChooseBinding
import java.lang.Exception

class LanguagesAdapter(private var context: Context, private var data: Array<String>) :
        RecyclerView.Adapter<LanguagesAdapter.MyViewHolder>() {

    var onClickItem:((position:Int)->Unit)? = null

    inner class MyViewHolder(private val root:ItemLanguageChooseBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            root.item.setOnClickListener {
                try {
                    onClickItem?.invoke(adapterPosition)
                } catch (_: Exception) { }
            }
        }

        fun bind(name: String) {
            root.item.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemLanguageChooseBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

}