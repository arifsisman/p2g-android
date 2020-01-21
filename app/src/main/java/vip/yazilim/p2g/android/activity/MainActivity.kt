package vip.yazilim.p2g.android.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import retrofit2.Call
import retrofit2.Response
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.SharedPreferencesConstants
import vip.yazilim.p2g.android.dto.Hero
import vip.yazilim.p2g.android.util.network.HeroesService
import vip.yazilim.p2g.android.util.network.RetrofitClient

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private lateinit var prefences: SharedPreferences

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

        prefences =
            getSharedPreferences(SharedPreferencesConstants.SPOTIFY_INFO, Context.MODE_PRIVATE)
        val displayName = prefences.getString("display_name", null)

        val toast: Toast =
            Toast.makeText(applicationContext, "Logged in as $displayName", Toast.LENGTH_LONG)

        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()

        getHeros()
    }

    private fun getHeros() {
        RetrofitClient.getClient().create(HeroesService::class.java).getHeroes()
            .enqueue(object : retrofit2.Callback<List<Hero>> {

                override fun onResponse(call: Call<List<Hero>>, response: Response<List<Hero>>) {
                    val herolist = ArrayList(response.body()!!)

                    Toast.makeText(this@MainActivity, "Succes", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<Hero>>?, t: Throwable?) {
                    Toast.makeText(this@MainActivity, "Failure", Toast.LENGTH_SHORT).show()
                }
            }

            )
    }
}
