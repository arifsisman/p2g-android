package vip.yazilim.p2g.android.util.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.androidadvance.topsnackbar.TSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vip.yazilim.p2g.android.constant.ColorCodes.ACCENT_BLUE
import vip.yazilim.p2g.android.constant.ColorCodes.ERROR
import vip.yazilim.p2g.android.constant.ColorCodes.WARN
import kotlin.reflect.KFunction0


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UIHelper {
    companion object {
        fun Activity.showErrorDialog(
            message: String,
            kFunction0: KFunction0<Unit>
        ) {
            if (!this.isFinishing) {
                val dialogBuilder = MaterialAlertDialogBuilder(this)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle("Error")
                alert.show()
                alert.setOnCancelListener { kFunction0() }
            }
        }

        fun Activity.showErrorDialog(message: String) {
            if (!this.isFinishing) {
                val dialogBuilder = MaterialAlertDialogBuilder(this)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle("Error")
                alert.show()
            }
        }

        fun View.showSnackBarInfo(message: String) {
            val snack: TSnackbar? = TSnackbar.make(this, message, TSnackbar.LENGTH_SHORT)
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun View.showSnackBarError(message: String) {
            val snack: TSnackbar? = TSnackbar.make(this, message, TSnackbar.LENGTH_SHORT)
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ERROR))
            snack?.show()
        }

        fun View.showSnackBarWarning(message: String) {
            val snack: TSnackbar? = TSnackbar.make(this, message, TSnackbar.LENGTH_SHORT)
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(WARN))
            snack?.show()
        }

        fun View.showSnackBarPlayerError(message: String) {
            val snack: TSnackbar? = TSnackbar.make(this, message, TSnackbar.LENGTH_SHORT)
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ERROR))
            snack?.show()
        }

        fun View.showSnackBarErrorIndefinite(message: String) {
            val snack: TSnackbar? = TSnackbar.make(this, message, TSnackbar.LENGTH_INDEFINITE)
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ERROR))
            snack?.show()
        }

        fun Context.showToastShort(message: String) {
            val toast: Toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)

            toast.setGravity(Gravity.BOTTOM, 0, 175)
            toast.show()
        }

        fun Context.showToastLong(message: String) {
            val toast: Toast = Toast.makeText(this, message, Toast.LENGTH_LONG)

            toast.setGravity(Gravity.BOTTOM, 0, 175)
            toast.show()
        }

        fun Context.closeKeyboard() {
            val inputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        fun Context.closeKeyboardSoft(viewBinder: IBinder) {
            val inputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(viewBinder, 0)
        }

        fun Context.dpFromPx(px: Float): Float {
            return px / this.resources.displayMetrics.density
        }

        fun Context.pxFromDp(dp: Float): Float {
            return dp * this.resources.displayMetrics.density
        }
    }
}

