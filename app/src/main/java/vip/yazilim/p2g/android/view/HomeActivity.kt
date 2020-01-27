package vip.yazilim.p2g.android.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_error.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.viewmodel.HomeViewModel
import vip.yazilim.p2g.android.viewmodel.ViewModelFactory
import vip.yazilim.p2g.di.Injection

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter

    /**
     //Consider this, if you need to call the service once when activity was created.
        Log.v(TAG,"savedInstanceState $savedInstanceState")
        if(savedInstanceState==null){
            viewModel.loadMuseums()
        }
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupViewModel()
        setupUI()
    }

    //ui
    private fun setupUI(){
        adapter= HomeAdapter(viewModel.roomModels.value?: emptyList())
        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.adapter= adapter
    }

    //viewmodel
    /**
        //Consider this if ViewModel class don't require parameters.
        viewModel = ViewModelProviders.of(this).get(MuseumViewModel::class.java)

        //if you require any parameters to  the ViewModel consider use a ViewModel Factory
        viewModel = ViewModelProviders.of(this,ViewModelFactory(Injection.providerRepository())).get(MuseumViewModel::class.java)

        //Anonymous observer implementation
        viewModel.museums.observe(this,Observer<List<Museum>> {
            Log.v("CONSOLE", "data updated $it")
            adapter.update(it)
        })
     */
    private fun setupViewModel(){
        viewModel = ViewModelProvider(this, ViewModelFactory(Injection.roomProviderRepository())).get(
            HomeViewModel::class.java)
        viewModel.roomModels.observe(this,renderRoomModels)

        viewModel.isViewLoading.observe(this,isViewLoadingObserver)
        viewModel.onMessageError.observe(this,onMessageErrorObserver)
        viewModel.isEmptyList.observe(this,emptyListObserver)
    }

    //observers
    private val renderRoomModels= Observer<List<RoomModel>> {
        Log.v(LOG_TAG, "data updated $it")
        layoutError.visibility=View.GONE
        layoutEmpty.visibility=View.GONE
        adapter.update(it)
    }

    private val isViewLoadingObserver= Observer<Boolean> {
        Log.v(LOG_TAG, "isViewLoading $it")
        val visibility=if(it)View.VISIBLE else View.GONE
        progressBar.visibility= visibility
    }

    private val onMessageErrorObserver= Observer<Any> {
        Log.v(LOG_TAG, "onMessageError $it")
        layoutError.visibility=View.VISIBLE
        layoutEmpty.visibility=View.GONE
        textViewError.text= "Error $it"
    }

    private val emptyListObserver= Observer<Boolean> {
        Log.v(LOG_TAG, "emptyListObserver $it")
        layoutEmpty.visibility=View.VISIBLE
        layoutError.visibility=View.GONE
    }


     //If you require updated data, you can call the method "loadMuseum" here
     override fun onResume() {
        super.onResume()
        viewModel.loadRooms()
     }

}
