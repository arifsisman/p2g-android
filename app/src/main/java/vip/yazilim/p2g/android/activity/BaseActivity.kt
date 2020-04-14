package vip.yazilim.p2g.android.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import vip.yazilim.p2g.android.util.event.UnauthorizedEvent


/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
open class BaseActivity : AppCompatActivity() {

//    public override fun onStart() {
//        super.onStart()
//        EventBus.getDefault().register(this)
//    }

//    public override fun onStop() {
//        super.onStop()
//        EventBus.getDefault().unregister(this)
//    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onUnauthorizedEvent(e: UnauthorizedEvent?) {
        handleUnauthorizedEvent()
    }

    protected open fun handleUnauthorizedEvent() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}