package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import vip.yazilim.p2g.android.Play2GetherApplication
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.service.LogoutService
import vip.yazilim.p2g.android.service.UserWebSocketService
import vip.yazilim.p2g.android.ui.main.MainViewModel
import vip.yazilim.p2g.android.ui.main.MainViewModelFactory


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Play2GetherApplication.currentActivity = this
        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)

        startService(Intent(baseContext, LogoutService::class.java))

        intent.getParcelableExtra<User>("user")?.id?.let {
            val intent = Intent(this@MainActivity, UserWebSocketService::class.java)
            intent.putExtra("userId", it)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
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

}
