package vip.yazilim.p2g.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.viewmodel.ViewModelFactory
import vip.yazilim.p2g.di.Injection


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        super.onCreate(savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        setupViewModel()

        // setupUI
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter= HomeAdapter(viewModel.roomModels.value ?: emptyList())
        recyclerView.adapter= adapter

        return root
    }


    //viewmodel
    private fun setupViewModel(){
        viewModel = ViewModelProvider(this, ViewModelFactory(Injection.roomProviderRepository())).get(HomeViewModel::class.java)

        viewModel.roomModels.observe(this,renderRoomModels)
        viewModel.isViewLoading.observe(this,isViewLoadingObserver)
        viewModel.onMessageError.observe(this,onMessageErrorObserver)
        viewModel.isEmptyList.observe(this,emptyListObserver)
    }


    //observers
    private val renderRoomModels= Observer<List<RoomModel>> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility=View.GONE
        layoutEmpty.visibility=View.GONE
        adapter.update(it)
    }

    private val isViewLoadingObserver= Observer<Boolean> {
        Log.v(GeneralConstants.LOG_TAG, "isViewLoading $it")
        val visibility=if(it)View.VISIBLE else View.GONE
        progressBar.visibility= visibility
    }

    @SuppressLint("SetTextI18n")
    private val onMessageErrorObserver= Observer<Any> {
        Log.v(GeneralConstants.LOG_TAG, "onMessageError $it")
        layoutError.visibility=View.VISIBLE
        layoutEmpty.visibility=View.GONE
        textViewError.text= "Error $it"
    }

    private val emptyListObserver= Observer<Boolean> {
        Log.v(GeneralConstants.LOG_TAG, "emptyListObserver $it")
        layoutEmpty.visibility=View.VISIBLE
        layoutError.visibility=View.GONE
    }


    //If you require updated data, you can call the method "loadMuseum" here
    override fun onResume() {
        super.onResume()
        viewModel.loadRooms()
    }
}