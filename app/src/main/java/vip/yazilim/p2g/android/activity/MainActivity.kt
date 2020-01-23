package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.data.p2g.User
import vip.yazilim.p2g.android.util.helper.DBHelper
import vip.yazilim.p2g.android.util.helper.UIHelper

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainActivity : AppCompatActivity() {

    val db by lazy { DBHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db.readData().ifEmpty {
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        val user = intent.getSerializableExtra("user") as? User
        UIHelper.showToastLong(applicationContext, "Logged in as ${user?.name}")
        UIHelper.showToastLong(applicationContext, "Creation date ${user?.creationDate}")

        val navView: BottomNavigationView = nav_view
        val navController = nav_host_fragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

}
