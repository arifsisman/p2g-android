package vip.yazilim.p2g.android.util.helper

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.Toast

/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object UIHelper{

    fun showToastShort(context: Context?, message: String) {
        val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)

        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()
    }

    fun showToastLong(context: Context?, message: String) {
        val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_LONG)

        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()
    }

    fun showErrorDialog(context: Context, message: String){
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }

}

