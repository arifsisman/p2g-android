package vip.yazilim.p2g.android.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.user.UserViewModel
import vip.yazilim.p2g.android.ui.user.UserViewModelFactory


class UserActivity : BaseActivity() {

    private lateinit var userViewModel: UserViewModel
    var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userModel = intent.getParcelableExtra("userModel")
        setContentView(R.layout.activity_user)
        userViewModel = ViewModelProvider(this, UserViewModelFactory())
            .get(UserViewModel::class.java)
        val actionBar = supportActionBar
        actionBar?.title = ""
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
