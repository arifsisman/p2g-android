package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.service.UserWebSocketService
import vip.yazilim.p2g.android.util.refrofit.Singleton


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainActivity : AppCompatActivity() {

//    private val db by lazy { DBHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        intent.getParcelableExtra<User>("user")?.id?.let {
            val intent = Intent(this@MainActivity, UserWebSocketService::class.java)
            intent.putExtra("userId", it)
            startService(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Logout -> {
                request(Singleton.apiClient().logout(), null)

//                db.deleteAllData()
                val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
        return true
    }

//    @SuppressLint("CheckResult")
//    private fun connectRoomWebSocket(roomId: Long) {
//        roomWSClient = getRoomWebSocketClient(roomId)
//
//        roomWSClient.connect()
//
//        roomWSClient.lifecycle()
//            .subscribe({
//                when (it.type) {
//                    LifecycleEvent.Type.OPENED -> {
//                        Log.i(TAG, it.toString())
//                    }
//                    LifecycleEvent.Type.CLOSED -> {
//                        Log.i(TAG, it.toString())
//                    }
//                    LifecycleEvent.Type.ERROR -> {
//                        Log.i(TAG, it.toString())
//                    }
//                    else -> Log.i(TAG, it.toString())
//                }
//            }, { t: Throwable? ->
//                Log.d(TAG, t?.message.toString())
//            })
//
//        roomWSClient.topic("/p2g/room/$roomId/messages")
//            .subscribe({
//                Log.d(TAG, it.payload)
//            }, { t: Throwable? -> Log.d(TAG, t?.message.toString()) })
//
//        roomWSClient.topic("/p2g/room/$roomId/songs")
//            .subscribe({
//                Log.d(TAG, it.payload)
//            }, { t: Throwable? -> Log.d(TAG, t?.message.toString()) })
//
//        roomWSClient.topic("/p2g/room/$roomId/status")
//            .subscribe({
//                Log.d(TAG, it.payload)
//            }, { t: Throwable? -> Log.d(TAG, t?.message.toString()) })
//
//        val gsonBuilder = GsonBuilder()
//        val gson = registerLocalDateTime(gsonBuilder).create()
//
//        val chatMessage = ChatMessage("TEST", "TEST", 1, "TEST", LocalDateTime.now())
//        val chatMessageJson = gson.toJson(chatMessage)
//
//        roomWSClient.send("/p2g/room/$roomId", chatMessageJson).subscribe()
//    }

}
