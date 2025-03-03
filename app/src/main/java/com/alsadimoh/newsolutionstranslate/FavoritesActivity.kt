package com.alsadimoh.newsolutionstranslate

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.newsolutionstranslate.adapter.FavoritesAdapter
import com.alsadimoh.newsolutionstranslate.databinding.ActivityFavoritesBinding
import com.alsadimoh.newsolutionstranslate.room.DatabaseBuilder
import com.alsadimoh.newsolutionstranslate.room.helper.DatabaseHelperImpl
import com.alsadimoh.newsolutionstranslate.room.utils.Status
import com.alsadimoh.newsolutionstranslate.room.utils.ViewModelFactory
import com.alsadimoh.newsolutionstranslate.room.viewModel.RoomViewModel
import com.alsadimoh.newsolutionstranslate.common.Common
import com.alsadimoh.newsolutionstranslate.common.Common.hideProgressDialog
import com.alsadimoh.newsolutionstranslate.common.Common.showExpandCardDialog
import com.alsadimoh.newsolutionstranslate.common.Common.showProgressDialog
import com.alsadimoh.newsolutionstranslate.common.FavoriteModel


class FavoritesActivity : AppCompatActivity() {

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var binding: ActivityFavoritesBinding
    private var lastPositionDeleted = -1
    private lateinit var adapter :FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val actionBar = supportActionBar
        if (actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)

            binding.toolbar.setNavigationIcon(R.drawable.ic_back_for_nav)
            binding.toolbar.setNavigationOnClickListener{
                finish()
            }
        }

        initViewModel()
        observeGetFavorites()
        observeDeleteFavorites()
        roomViewModel.getFavorites()

    }


    private fun initViewModel() {
        roomViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            ),
        )[RoomViewModel::class.java]
    }


    private fun observeGetFavorites() {
        roomViewModel.getFavoritesResponse().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e(Common.TAG, "onCreate:observeGetInsertFavorite SUCCESS ")
                    setFavoritesItems(it.data as MutableList<FavoriteModel>)
                    hideProgressDialog()
                }
                Status.LOADING -> {
                    Log.e(Common.TAG, "onCreate:observeGetInsertFavorite LOADING")
                    showProgressDialog(this)
                }
                Status.ERROR -> {
                    Log.e(Common.TAG, "onCreate: ERROR to observeGetInsertFavorite")
                    hideProgressDialog()
                }
            }
        }
    }


    private fun observeDeleteFavorites() {
        roomViewModel.getDeleteFavoriteStatus().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e(Common.TAG, "onCreate:observeDeleteFavorites SUCCESS ")
                    adapter.data.removeAt(lastPositionDeleted)
                    adapter.notifyItemRemoved(lastPositionDeleted)
                    checkIsEmpty(adapter)
                    hideProgressDialog()
                }
                Status.LOADING -> {
                    Log.e(Common.TAG, "onCreate:observeDeleteFavorites LOADING")
                    showProgressDialog(this)
                }
                Status.ERROR -> {
                    Log.e(Common.TAG, "onCreate: ERROR to observeDeleteFavorites")
                    hideProgressDialog()
                }
            }
        }
    }

   private fun setFavoritesItems(data:MutableList<FavoriteModel>){
       adapter = FavoritesAdapter(this,data)
       binding.rvFavorites.adapter=adapter
       binding.rvFavorites.layoutManager = LinearLayoutManager(this)
       checkIsEmpty(adapter)

       adapter.onClickItemExpand = { model, _ ->
            showExpandCardDialog(this,model.translatedText,model.sourceText,model.targetLang.uppercase(),model.sourceLang.uppercase(),false)
       }

       adapter.onClickItemRemoveFavorite = { model, position ->
           lastPositionDeleted = position
           roomViewModel.deleteFavorite(model.id)
       }
    }


    private fun checkIsEmpty(adapter: FavoritesAdapter){
        if (adapter.data.isEmpty()){
            binding.txtEmpty.visibility = View.VISIBLE
        }else{
            binding.txtEmpty.visibility = View.GONE
        }
    }
}