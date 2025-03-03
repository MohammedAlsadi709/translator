package com.alsadimoh.newsolutionstranslate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.newsolutionstranslate.databinding.ItemFavoriteBinding
import com.alsadimoh.newsolutionstranslate.common.FavoriteModel
import java.lang.Exception

class FavoritesAdapter(private var context: Context, var data: MutableList<FavoriteModel>) :
        RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>() {

    var onClickItemExpand:((model:FavoriteModel,position:Int)->Unit)? = null
    var onClickItemRemoveFavorite:((model:FavoriteModel,position:Int)->Unit)? = null

    inner class MyViewHolder(private val root:ItemFavoriteBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            root.btnExpandText.setOnClickListener {
                try {
                    onClickItemExpand?.invoke(data[adapterPosition],adapterPosition)
                } catch (_: Exception) { }
            }

            root.btnRemoveFavorite.setOnClickListener {
                try {
                    onClickItemRemoveFavorite?.invoke(data[adapterPosition],adapterPosition)
                } catch (_: Exception) { }
            }
        }

        fun bind(model: FavoriteModel) {
           root.txtToTranslate.text = model.sourceText
           root.txtTranslatedText.text = model.translatedText
           root.txtLangFrom.text = model.sourceLang
           root.txtLangTo.text = model.targetLang
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemFavoriteBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

}