package com.pratama.baseandroid.coreandroid

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pratama.baseandroid.coreandroid.network.NetworkChecker
import io.reactivex.disposables.Disposable


abstract class BaseActivity : AppCompatActivity() {
    var mProgressDialog: ProgressDialog? = null
    var isNetworkState: Boolean = false
    lateinit var drawerLayout: DrawerLayout
    var disposable: Disposable? = null
    abstract fun getLayoutID(): Int
    abstract fun onCreateActivity()

    //Listener lắng nghe sự thay đổi trạng thái connect internet
    private var onNetworkConnectedListener: NetworkChecker? = null
    fun setOnNetworkConnectedListener(onNetworkConnectedListener: NetworkChecker) {
        this.onNetworkConnectedListener = onNetworkConnectedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNetworkState = isNetworkConnected()
        registerBroadcastReciver()
        setContentView(getLayoutID())
        onCreateActivity()
        supportFragmentManager.addOnBackStackChangedListener {
            val fragments: List<Fragment> = supportFragmentManager!!.fragments
            if (fragments.isNotEmpty() && fragments[fragments.size - 1].javaClass.simpleName.equals(
                    "DemoFragment",
                    true
                )
            ) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_left);
                supportActionBar?.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mProgressDialog = null
        disposable?.dispose()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun showProgressLoadding() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setIndeterminate(true)
        mProgressDialog!!.setMessage(resources.getString(R.string.loadding))
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.show()
    }

    fun updateMessageProgressDialog(message: String) {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.setMessage(message)
        }
    }

    fun dismisProgressLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.dismiss()
        }
    }

    //Đăng ký broadcast lắng nghe sự kiện thay đổi network
    private fun registerBroadcastReciver() {
        val filter = IntentFilter()
        filter.addAction(Const.ACTION_NETWORK_CHANGE)
        registerReceiver(receiver, filter)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                when (action) {
                    Const.ACTION_NETWORK_CHANGE -> {
                        isNetworkState = isNetworkConnected()
                        if (isNetworkState) {
                            onNetworkConnectedListener?.let {
                                it.isNetworkConnected()
                            }
                        } else {
                            onNetworkConnectedListener?.let {
                                it.onNetworkDisconnect()
                            }
                        }
                    }
                }
            }
        }
    }

    //Kiểm tra trạng thái internet
    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfor = connectivityManager.getActiveNetworkInfo()
        isNetworkState = activeNetInfor != null && activeNetInfor.isConnected
        return isNetworkState
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}

fun AppCompatActivity.showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showSnackBar(message: String) {
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
}