package vip.yazilim.p2g.android.activity

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val prefences = getSharedPreferences(SharedPreferencesConstants.SPOTIFY_INFO, Context.MODE_PRIVATE)
        val displayName = prefences.getString("display_name", "UNKNOWN")

        val toast: Toast = Toast . makeText (applicationContext, "Logged in as $displayName", Toast.LENGTH_LONG)

        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()

    }
}
