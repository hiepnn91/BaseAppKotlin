package com.pratama.baseandroid.ui.homepage

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pratama.baseandroid.R
import com.pratama.baseandroid.coreandroid.BaseFragment
import com.pratama.baseandroid.model.Event
import com.pratama.baseandroid.utils.EventBus
import com.pratama.baseandroid.utils.FragmentUtil
import com.pratama.baseandroid.utils.FragmentUtil.addFragment
import com.pratama.baseandroid.utils.Logg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_second.view.*

@AndroidEntryPoint
class SecondFragment : BaseFragment() {
    var mNameFragment: String? = null
    fun newInstance(nameFragment: String?): SecondFragment? {
        val fm = SecondFragment()
        val bundle = Bundle()
        bundle.putString("fragmentName", nameFragment)
        fm.arguments = bundle
        return fm
    }

    override fun getArgument(bundle: Bundle?) {
        mNameFragment = bundle!!.getString("fragmentName")
    }

    override fun initView(root: View?, inflater: LayoutInflater?, container: ViewGroup?) {
        Logg.e("initView")
        EventBus.post(Event.SomeEvent("SecondFragment"))
        root!!.fragment_name_second.text = "secconde"
        root!!.btnBackToFirst.setOnClickListener {
            val thirdFragment = ThirdFragment().newInstance("Third")
            addFragment(thirdFragment, R.id.container)
        }
        setCustomToolbar(true, this.activity!!, R.id.activity_main_toolbar)
    }

    override fun initData() {
        Logg.e("initData")
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_second
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getIconLeft(): Drawable? {
        return this.resources.getDrawable(R.drawable.ic_back)
    }

    override fun processCustomToolbar() {
        FragmentUtil.popBackStack(this)
    }
}