package vip.yazilim.p2g.android.util.helper

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.androidadvance.topsnackbar.TSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vip.yazilim.p2g.android.constant.ColorCodes.ACCENT_BLUE
import vip.yazilim.p2g.android.constant.ColorCodes.ERROR


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UIHelper {
    companion object {
        fun showToastShort(context: Context?, message: String) {
            val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)

            toast.setGravity(Gravity.BOTTOM, 0, 175)
            toast.show()
        }

        fun showToastLong(context: Context?, message: String) {
            val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_LONG)

            toast.setGravity(Gravity.BOTTOM, 0, 175)
            toast.show()
        }

        fun showErrorDialog(context: Context, message: String): AlertDialog? {
            val dialogBuilder = MaterialAlertDialogBuilder(context)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Error")
            alert.show()

            return alert
        }

        fun showSnackBarShortTop(view: View?, message: String) {
            val snack: TSnackbar? =
                view?.let { TSnackbar.make(it, message, TSnackbar.LENGTH_SHORT) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun showSnackBarPlayer(view: View?, message: String) {
            val snack: TSnackbar? =
                view?.let { TSnackbar.make(it, message, TSnackbar.LENGTH_SHORT) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ERROR))
            snack?.show()
        }

        fun showSnackBarError(view: View?, message: String): TSnackbar? {
            val snack: TSnackbar? =
                view?.let { TSnackbar.make(it, message, TSnackbar.LENGTH_SHORT) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ERROR))
            snack?.show()

            return snack
        }

        fun showSnackBarErrorIndefinite(view: View?, message: String) {
            val snack: TSnackbar? =
                view?.let { TSnackbar.make(it, message, TSnackbar.LENGTH_INDEFINITE) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ERROR))
            snack?.show()
        }

        fun dpFromPx(context: Context, px: Float): Float {
            return px / context.resources.displayMetrics.density
        }

        fun pxFromDp(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

        fun Context.closeKeyboard() {
            val inputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

    }
}

