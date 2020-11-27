package com.pratama.baseandroid.coreandroid

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import butterknife.ButterKnife
import io.reactivex.disposables.Disposable

open abstract class BaseFragment : Fragment() {
    private var progressDlg: ProgressDialog? = null
    var baseActivity: BaseActivity? = null
    var mInflater: LayoutInflater? = null
    var mContainer: ViewGroup? = null
    protected var rootView: View? = null
    protected val PARAM_BUNDLE = "PARAM_BUNDLE"
    private var savedState: Bundle? = null
    protected var fragmentViewParent: ViewGroup? = null
    var disposable: Disposable? = null
    var initialProgressBar: View? = null

    var initialNetworkError: View? = null

    var initialEmptyList: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getLayoutID(), container, false)
        Log.e("Lifecycle ", this.javaClass.simpleName)
        return createRootView(inflater, container!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = baseActivity
        if (context is BaseActivity) baseActivity = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            getArgument(arguments)
        }
        baseActivity = activity as BaseActivity?
        initView(rootView, mInflater, mContainer)
        initData()
    }

    private fun createRootView(inflater: LayoutInflater, container: ViewGroup): View? {
        mInflater = inflater
        mContainer = container
        if (isSkipGenerateBaseLayout()) {
            rootView = inflater.inflate(getLayoutID(), container, false)
            ButterKnife.bind(rootView!!)
        } else {
            rootView = inflater.inflate(R.layout.layout_base_fragment, container, false)
            fragmentViewParent = rootView!!.findViewById<View>(R.id.fragmentViewParent) as ViewGroup
            fragmentViewParent!!.addView(inflater.inflate(getLayoutID(), container, false))
            ButterKnife.bind(this, rootView!!)
            initialProgressBar =
                rootView!!.findViewById<View>(R.id.initialProgressBar) as ProgressBar
            initialNetworkError =
                rootView!!.findViewById<View>(R.id.initialNetworkError) as FrameLayout
            initialEmptyList = rootView!!.findViewById<View>(R.id.initialEmptyList) as FrameLayout
            bypassCommonNetworkLoadingIfNecessary()
        }
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveStateToArguments()
//        KeyboardUtil.hideSoftKeyboard(activity)
        if (isCancelRequestOnDestroyView()) {
//            NetworkUtils.getInstance(BaseApplication.getInstance()).cancelNormalRequest()
            isLoading = isLoadingMore()
        }
    }

    protected open fun isLoadingMore(): Boolean {
        return false
    }

    protected open fun isCancelRequestOnDestroyView(): Boolean {
        return true
    }

    open fun showProgressDialog(cancleable: Boolean) {
        if (progressDlg != null && progressDlg!!.isShowing()) {
            closeProgressDialog()
        }
        progressDlg = ProgressDialog.show(activity, "", "Đang xử lý", true, cancleable)
        progressDlg!!.setCancelable(cancleable)
        progressDlg!!.setCanceledOnTouchOutside(cancleable)
        progressDlg!!.setOnCancelListener(DialogInterface.OnCancelListener {
//            cancelAllRequest(
//                getArrayRequest()
//            )
        })
    }

    open fun closeProgressDialog() {
        if (progressDlg != null) {
            try {
                progressDlg!!.dismiss()
                progressDlg = null
            } catch (e: Exception) {
            }
        }
    }

    private fun showOrHide(subject: View, target: View) {
        subject.visibility = if (subject === target) View.VISIBLE else View.GONE
    }

    private fun showAndHideOthers(target: View) {
        showOrHide(initialProgressBar!!, target)
        showOrHide(initialNetworkError!!, target)
        showOrHide(initialEmptyList!!, target)
        showOrHide(fragmentViewParent!!, target)
    }

    protected var isLoading = false
    protected open fun initialLoadingProgress() {
        showAndHideOthers(initialProgressBar!!)
    }

    private fun bypassCommonNetworkLoadingIfNecessary() {
        if (!isStartWithLoading()) {
            initialResponseHandled()
        } else {
            initialLoadingProgress()
            isLoading = true
        }
    }

    protected open fun initialResponseHandled() {
        isLoading = false
        showAndHideOthers(fragmentViewParent!!)
    }

    protected open fun isStartWithLoading(): Boolean {
        return false
    }

    protected open fun isSkipGenerateBaseLayout(): Boolean {
        return false
    }

    protected abstract fun getArgument(bundle: Bundle?)
    fun showToast(context: Context, message: String) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showToast(context, message)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveStateToArguments()
        super.onSaveInstanceState(outState)
    }

    private fun saveStateToArguments() {
        if (view != null) savedState = saveState()
        if (savedState != null) {
            val bundle = arguments
            bundle?.putBundle(PARAM_BUNDLE, savedState)
        }
    }

    private fun saveState(): Bundle? {
        val state = Bundle()
        onSaveState(state)
        return state
    }

    abstract fun getLayoutID(): Int
    protected abstract fun initView(root: View?, inflater: LayoutInflater?, container: ViewGroup?)
    protected abstract fun initData()

    //    abstract fun onViewReady(view: View)
    protected abstract fun onRestore()

    protected abstract fun initialize()

    protected abstract fun onSaveState(bundle: Bundle?)

    protected abstract fun onRestoreState(bundle: Bundle?)
    open fun setCustomToolbar(isCustom: Boolean, activity: FragmentActivity, resID: Int) {
        customToolbar(isCustom, activity, resID)
    }

    protected open fun getIconLeft(): Drawable? {
        return null
    }

    var act: BaseActivity? = null
    protected open fun customToolbar(isCustom: Boolean, activity: FragmentActivity, resID: Int) {
        act = activity as BaseActivity?
        if (isCustom) {
            if (act!!.getSupportActionBar() != null) {
                val toolbar: Toolbar = act!!.findViewById(resID) as Toolbar
                toolbar.setNavigationIcon(getIconLeft())
                toolbar.setNavigationOnClickListener(View.OnClickListener {
                    if (act!!.supportFragmentManager.backStackEntryCount > 0) {
                        processCustomToolbar()
                    } else {
                        loadMenuLeft()
                    }
                })
            }
        } else {
            if (act!!.getSupportActionBar() != null) {
                val toolbar: Toolbar = act!!.findViewById(resID) as Toolbar
                toolbar.setNavigationIcon(null)
            }
        }
    }

    private fun restoreState() {
        if (savedState != null) {
            onRestoreState(savedState)
        }
    }

    private fun restoreStateFromArguments(): Boolean {
        val bundle = arguments ?: return false
        savedState = bundle.getBundle(PARAM_BUNDLE)
        if (savedState == null) {
            return false
        }
        restoreState()
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!restoreStateFromArguments()) {
            initialize()
        } else {
            onRestore()
        }
    }

    protected open fun loadMenuLeft() {
        if (act!!.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            act!!.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            act!!.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    protected open fun processCustomToolbar() {}
    fun showSnackBar(message: String) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showSnackBar(message)
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    fun isNetworkConnected(): Boolean {
        if (activity is BaseActivity) {
            return (activity as BaseActivity).isNetworkState
        }
        return false
    }
}