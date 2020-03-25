package vip.yazilim.p2g.android.util.helper

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.androidadvance.topsnackbar.TSnackbar
import com.google.android.material.snackbar.Snackbar
import vip.yazilim.p2g.android.constant.ColorCodes.ACCENT_BLUE


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
            val dialogBuilder = AlertDialog.Builder(context)

            dialogBuilder.setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Error")
            alert.show()

            return alert
        }


        fun showSnackBarLongBottom(view: View?, message: String) {
            val snack: Snackbar? = view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun showSnackBarLongTop(view: View?, message: String) {
            val snack: TSnackbar? = view?.let { TSnackbar.make(it, message, TSnackbar.LENGTH_LONG) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun showSnackBarShortBottom(view: View?, message: String) {
            val snack: Snackbar? = view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun showSnackBarShortTop(view: View?, message: String) {
            val snack: TSnackbar? =
                view?.let { TSnackbar.make(it, message, TSnackbar.LENGTH_SHORT) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun showSnackBarPlayer(view: View?, message: String) {
            val snack: Snackbar? = view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT) }
            snack?.anchorView = view
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun showSnackBarShortBottomIndefinite(view: View?, message: String) {
            val snack: Snackbar? =
                view?.let { Snackbar.make(it, message, Snackbar.LENGTH_INDEFINITE) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun showSnackBarShortTopIndefinite(view: View?, message: String) {
            val snack: TSnackbar? =
                view?.let { TSnackbar.make(it, message, TSnackbar.LENGTH_INDEFINITE) }
            val snackView = snack?.view
            snackView?.setBackgroundColor(Color.parseColor(ACCENT_BLUE))
            snack?.show()
        }

        fun dpFromPx(context: Context, px: Float): Float {
            return px / context.resources.displayMetrics.density
        }

        fun pxFromDp(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

        fun setMenuItemsVisibility(menu: Menu, exception: MenuItem, visible: Boolean) {
            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                if (item !== exception) item.isVisible = visible
            }
        }

        fun Context.closeKeyboard() {
            val inputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

    }
}

