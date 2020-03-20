package vip.yazilim.p2g.android.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import vip.yazilim.p2g.android.ui.main.MainViewModel
import vip.yazilim.p2g.android.util.helper.TAG

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
abstract class FragmentBase(var layout: Int) : Fragment() {
    lateinit var root: View
    lateinit var container: ViewGroup
    private var mainViewModelBase: MainViewModel = MainViewModel()


    // Inflate view with container and setupViewModel and setupUI
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        root = inflater.inflate(layout, container, false)

        if (container != null) {
            this.container = container
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
    }

    abstract fun setupUI()
    abstract fun setupViewModel()

    // Default ViewModelBase setup
    fun setupMainViewModel(): MainViewModel {
        mainViewModelBase.isViewLoading.observe(this, isViewLoadingObserver)
        mainViewModelBase.onMessageError.observe(this, onMessageErrorObserver)
        mainViewModelBase.isEmptyList.observe(this, emptyListObserver)

        return mainViewModelBase
    }

    // Default Observers
    val isViewLoadingObserver = Observer<Boolean> {
        Log.v(TAG, "isViewLoading $it")
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar?.visibility = visibility
    }

    val onMessageErrorObserver = Observer<Any> {
        Log.v(TAG, "onMessageError $it")
        layoutError?.visibility = View.VISIBLE
        layoutEmpty?.visibility = View.GONE
        textViewError.text = it?.toString()
    }

    val emptyListObserver = Observer<Boolean> {
        Log.v(TAG, "emptyListObserver $it")
        layoutEmpty?.visibility = View.VISIBLE
        layoutError?.visibility = View.GONE
    }

}