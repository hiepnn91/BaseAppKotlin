package com.pratama.baseandroid.ui.homepage

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.pratama.baseandroid.R
import com.pratama.baseandroid.coreandroid.BaseFragment
import com.pratama.baseandroid.model.Event
import com.pratama.baseandroid.utils.*
import com.pratama.baseandroid.utils.PromptUtils.negativeButton
import com.pratama.baseandroid.utils.PromptUtils.positiveButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_third.view.*

@AndroidEntryPoint
class ThirdFragment : BaseFragment() {
    var mNameFragment: String? = null
    fun newInstance(nameFragment: String?): ThirdFragment {
        val fm = ThirdFragment()
        val bundle = Bundle()
        bundle.putString("fragmentName", nameFragment)
        fm.arguments = bundle
        return fm
    }

    override fun getArgument(bundle: Bundle?) {
        mNameFragment = bundle!!.getString("fragmentName")
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_third
    }

    override fun initView(root: View?, inflater: LayoutInflater?, container: ViewGroup?) {
        Logg.e("initView")
        EventBus.post(Event.SomeEvent("ThirdFragment"))
        setCustomToolbar(true, this.activity!!, R.id.activity_main_toolbar)
        root!!.tv_name_third.text = mNameFragment
        root!!.btnBackToFirst.setOnClickListener {
            FragmentUtil.popEntireFragmentBackStack(this)
        }
        root!!.btnEventToFirst.setOnClickListener {

            showKeyboard(this@ThirdFragment)
//            MaterialDialog(this.context!!).show {
//                title(text = "Title")
//                message(text = "Content")
//                positiveButton(text = "Agree"){
//                    Logg.e("Agree")
//                    showKeyboard(this@ThirdFragment)
//                }
//                negativeButton(text = "Disagree"){
//                    Logg.e("Disagree")
//                    hideKeyboard(this@ThirdFragment)
//                }
//            }
        }
    }

    override fun initData() {
        Logg.e("initData")
    }

    override fun onRestore() {
        Logg.e("onRestore")
    }

    override fun initialize() {
        Logg.e("initialize")
    }

    override fun onSaveState(bundle: Bundle?) {
        Logg.e("onSaveState")
    }

    override fun onRestoreState(bundle: Bundle?) {
        Logg.e("onRestoreState")
    }

    override fun getIconLeft(): Drawable? {
        return this.resources.getDrawable(R.drawable.ic_back)
    }

    override fun processCustomToolbar() {
        FragmentUtil.popBackStack(this)
    }
}