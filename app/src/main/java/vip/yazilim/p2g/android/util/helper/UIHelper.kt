package vip.yazilim.p2g.android.util.helper

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UIHelper {
    companion object {
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

        fun showErrorDialog(context: Context, message: String) {
            val dialogBuilder = AlertDialog.Builder(context)

            dialogBuilder.setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Error")
            alert.show()
        }


        fun showSnackBarLong(view: View?, message: String) {
            val snack: Snackbar? = view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG) }
            val snackView = snack?.view
            val params = snackView?.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params
            snack.show()
        }

        fun showSnackBarShort(view: View?, message: String) {
            val snack: Snackbar? = view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT) }
            val snackView = snack?.view
            val params = snackView?.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params
            snack.show()
        }

    }
}

