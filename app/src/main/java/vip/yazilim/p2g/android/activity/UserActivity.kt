package vip.yazilim.p2g.android.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.model.p2g.UserModel


class UserActivity : AppCompatActivity() {

    var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userModel = intent.getParcelableExtra("userModel")
        setContentView(R.layout.activity_user)
    }

//    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//        val userFragment = UserFragment()
//        userFragment.arguments = savedInstance
//        setContentView(R.layout.activity_user)
////
////        supportFragmentManager
////            .beginTransaction()
////            .replace(container.id, userFragment)
////            .addToBackStack("FriendsFragment")
////            .commit()
//
//        return super.onCreateView(name, context, attrs)
//    }
}
