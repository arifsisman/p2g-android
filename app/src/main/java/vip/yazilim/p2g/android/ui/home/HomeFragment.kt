package vip.yazilim.p2g.android.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_create_room.view.*
import kotlinx.android.synthetic.main.dialog_create_room.view.dialog_cancel_button
import kotlinx.android.synthetic.main.dialog_create_room.view.dialog_room_password
import kotlinx.android.synthetic.main.dialog_room_password.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.constant.GeneralConstants.UNDEFINED
import vip.yazilim.p2g.android.model.p2g.Room
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.RoomUser
import vip.yazilim.p2g.android.util.helper.UIHelper


class HomeFragment : Fragment(), HomeAdapter.OnItemClickListener {

    //    private val db by lazy { activity?.let { DBHelper(it) } }
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var root: View
    private lateinit var container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        root = inflater.inflate(R.layout.fragment_home, container, false)

        if (container != null) {
            this.container = container
        }

        setupViewModel()
        setupUI()

        return root
    }

    //viewmodel
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)

        viewModel.roomModels.observe(this, renderRoomModels)
        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
        viewModel.isEmptyList.observe(this, emptyListObserver)
    }

    private fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = HomeAdapter(viewModel.roomModels.value ?: emptyList(), this)
        recyclerView.adapter = adapter

        val createRoomButton: Button = root.findViewById(R.id.button_create_room)
        createRoomButton.setOnClickListener {
            createRoomButtonEvent()
        }
    }


    //observers
    private val renderRoomModels = Observer<List<RoomModel>> {
        Log.v(LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)

        if (it.isNotEmpty()) {
            adapter.roomModelsFull = it as MutableList<RoomModel>

        }
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        Log.v(LOG_TAG, "isViewLoading $it")
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    @SuppressLint("SetTextI18n")
    private val onMessageErrorObserver = Observer<Any> {
        Log.v(LOG_TAG, "onMessageError $it")
        layoutError.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        textViewError.text = "Error $it"
    }

    private val emptyListObserver = Observer<Boolean> {
        Log.v(LOG_TAG, "emptyListObserver $it")
        layoutEmpty.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Search Room or Room Owner"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("queryText", query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                Log.d("queryText", newText)
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchView.requestFocus()
                searchView.isIconified = false
                searchView.isIconifiedByDefault = false
                searchView.visibility = View.VISIBLE
                setItemsVisibility(menu, searchItem, false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                adapter.filter.filter("")
                searchView.isIconified = true
                searchView.isIconifiedByDefault = true
                searchView.visibility = View.VISIBLE
                setItemsVisibility(menu, searchItem, true)
                return true
            }
        })
    }

    override fun onItemClicked(roomModel: RoomModel) {
        Log.d(LOG_TAG, "Click " + roomModel.room.name)
        val room: Room = roomModel.room

        val mDialogView = View.inflate(context, R.layout.dialog_room_password, null)
        val mBuilder = AlertDialog.Builder(activity).setView(mDialogView)

        val joinButton = mDialogView.dialog_join_room_button
        val roomPasswordEditText = mDialogView.dialog_room_password

        val mAlertDialog: AlertDialog

        if (room.password.isNotEmpty()) {
            mAlertDialog = mBuilder.show()

            roomPasswordEditText.requestFocus()
            showKeyboard()

            // For disable create button if password is empty
            roomPasswordEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    joinButton.isEnabled = s.isNotEmpty()
                }
            })

            // Click join
            joinButton.setOnClickListener {
                val roomPassword = roomPasswordEditText.text.toString()

                P2GRequest.build(
                    ApiClient.build().joinRoom(room.id, roomPassword),
                    object : Callback<RoomUser> {
                        override fun onError(msg: String) {
                            Log.d(LOG_TAG, msg)
                            UIHelper.showToastLong(context, msg)
                        }

                        override fun onSuccess(obj: RoomUser) {
                            //TODO: Start room activity from here
                            Log.d(LOG_TAG, "Joined room with roomUser ID: " + obj.id)
                            mAlertDialog.dismiss()
                        }
                    })
            }

            // Click cancel
            mDialogView.dialog_cancel_button.setOnClickListener {
                mAlertDialog.cancel()
                roomPasswordEditText.clearFocus()
                closeKeyboard()
            }
        } else {
            P2GRequest.build(
                ApiClient.build().joinRoom(room.id, UNDEFINED),
                object : Callback<RoomUser> {
                    override fun onError(msg: String) {
                        Log.d(LOG_TAG, msg)
                        UIHelper.showToastLong(context, msg)
                    }

                    override fun onSuccess(obj: RoomUser) {
                        //TODO: Start room activity from here
                        Log.d(LOG_TAG, "Joined room with roomUser ID: " + obj.id)
                    }
                })
        }

    }

    private fun createRoomButtonEvent() {
        val mDialogView = View.inflate(context, R.layout.dialog_create_room, null)
        val mBuilder = AlertDialog.Builder(activity).setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        val roomNameEditText = mDialogView.dialog_room_name
        val roomPasswordEditText = mDialogView.dialog_room_password
        val createButton = mDialogView.dialog_create_room_button

        // For request focus and open keyboard
        roomNameEditText.requestFocus()
        showKeyboard()

        // For disable create button if name is empty
        roomNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                createButton.isEnabled = s.isNotEmpty()
            }
        })

        // Click create
        createButton.setOnClickListener {
            val roomName = roomNameEditText.text.toString()
            val roomPassword = roomPasswordEditText.text.toString()

            P2GRequest.build(
                ApiClient.build().createRoom(roomName, roomPassword),
                object : Callback<Room> {
                    override fun onError(msg: String) {
                        Log.d(LOG_TAG, "Room can not created")
                        UIHelper.showToastLong(context, msg)
                    }

                    override fun onSuccess(obj: Room) {
                        //TODO: Start room activity from here
                        Log.d(LOG_TAG, "Room created with ID: " + obj.id)
                        mAlertDialog.dismiss()
                    }
                })
        }

        // Click cancel
        mDialogView.dialog_cancel_button.setOnClickListener {
            mAlertDialog.cancel()
            roomNameEditText.clearFocus()
            roomPasswordEditText.clearFocus()
            closeKeyboard()
        }
    }

    private fun setItemsVisibility(
        menu: Menu,
        exception: MenuItem,
        visible: Boolean
    ) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item !== exception) item.isVisible = visible
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRooms()
    }

    private fun showKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

}