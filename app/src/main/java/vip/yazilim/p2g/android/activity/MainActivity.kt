package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_create_room.view.dialog_cancel_button
import kotlinx.android.synthetic.main.dialog_create_room.view.dialog_room_password
import kotlinx.android.synthetic.main.dialog_room_password.view.*
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.withCallback
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.service.LogoutService
import vip.yazilim.p2g.android.service.UserWebSocketService
import vip.yazilim.p2g.android.ui.main.MainViewModel
import vip.yazilim.p2g.android.ui.main.MainViewModelFactory
import vip.yazilim.p2g.android.ui.main.home.HomeAdapter
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.closeKeyboard
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarError


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainActivity : BaseActivity(),
    HomeAdapter.OnItemClickListener {

    private lateinit var viewModel: MainViewModel
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Play2GetherApplication.currentActivity = this
        viewModel =
            ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)

        user = intent.getParcelableExtra("user")
    }

    override fun onStart() {
        super.onStart()

        startService(Intent(baseContext, LogoutService::class.java))

        user?.id?.let {
            val intent = Intent(this@MainActivity, UserWebSocketService::class.java)
            intent.putExtra("userId", it)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopService(Intent(this@MainActivity, UserWebSocketService::class.java))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Bind views
        val navView: BottomNavigationView = nav_view
        val navController = nav_host_fragment.findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_invites,
                R.id.navigation_friends,
                R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onItemClicked(room: Room) {
        if (room.password == null) {
            joinRoomEvent(room)
        } else {
            joinPrivateRoomEvent(room)
        }

    }

    private fun joinRoomEvent(room: Room) =
        Api.client?.joinRoom(room.id, GeneralConstants.UNDEFINED)?.withCallback(
            object : Callback<RoomUserModel> {
                override fun onError(msg: String) {
                    viewModel.onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: RoomUserModel) {
                    val roomIntent = Intent(this@MainActivity, RoomActivity::class.java)
                    roomIntent.putExtra("room", obj.room)
                    roomIntent.putExtra("user", obj.user)
                    roomIntent.putExtra("roomUser", obj.roomUser)
                    startActivity(roomIntent)
                }
            })


    private fun joinPrivateRoomEvent(room: Room) {
        val mDialogView = View.inflate(this, R.layout.dialog_room_password, null)
        val mBuilder = MaterialAlertDialogBuilder(this).setView(mDialogView)
        val joinButton = mDialogView.dialog_join_room_button
        val roomPasswordEditText = mDialogView.dialog_room_password
        val mAlertDialog: AlertDialog?
        mAlertDialog = mBuilder.show()
        mAlertDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        roomPasswordEditText.requestFocus()

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
            Api.client?.joinRoom(room.id, roomPasswordEditText.text.toString())?.withCallback(
                object : Callback<RoomUserModel> {
                    override fun onError(msg: String) {
                        mDialogView.showSnackBarError(msg)
                    }

                    override fun onSuccess(obj: RoomUserModel) {
                        mAlertDialog?.dismiss()

                        val roomIntent = Intent(this@MainActivity, RoomActivity::class.java)
                        roomIntent.putExtra("room", obj.room)
                        roomIntent.putExtra("user", obj.user)
                        roomIntent.putExtra("roomUser", obj.roomUser)
                        startActivity(roomIntent)
                    }
                })
        }


        // Click cancel
        mDialogView.dialog_cancel_button.setOnClickListener {
            mAlertDialog?.cancel()
            roomPasswordEditText.clearFocus()
            applicationContext?.closeKeyboard()
        }
    }
}
