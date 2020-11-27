package com.pratama.baseandroid.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.pratama.baseandroid.R
import com.pratama.baseandroid.coreandroid.BaseFragment
import com.pratama.baseandroid.utils.FragmentUtil.addFragment

object FragmentUtil {
    fun AppCompatActivity.addFragment(fragment: BaseFragment, frameId: Int) {
        supportFragmentManager.inTransaction({ add(frameId, fragment) }, true)
    }

    fun AppCompatActivity.replaceFragment(fragment: BaseFragment, frameId: Int) {
        supportFragmentManager.inTransaction({ replace(frameId, fragment) }, false);
    }

    fun Fragment.replaceFragment(fragment: BaseFragment, frameId: Int) {
        fragment.fragmentManager!!.inTransaction({ replace(frameId, fragment) }, false)
        val fragmentManager = this.activity!!.supportFragmentManager
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    fun Fragment.addFragment(fragment: BaseFragment, frameId: Int) {
        val fragmentManager = this.activity!!.supportFragmentManager
        val transaction = fragmentManager!!.beginTransaction()
        transaction.add(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    inline fun FragmentManager.inTransaction(
        func: FragmentTransaction.() -> FragmentTransaction,
        boolean: Boolean
    ) {

        if (boolean)
            beginTransaction().addToBackStack(null)
        beginTransaction().func().commit()
    }

    fun popBackStack(fragment: BaseFragment) {
        val fm = fragment.act!!.supportFragmentManager
        val backStackCount = fm!!.backStackEntryCount
        if (backStackCount > 0) {
            fragment.act!!.supportFragmentManager.popBackStack()
        } else {
            fragment.act!!.onBackPressed()
        }
    }

    fun popEntireFragmentBackStack(fragment: BaseFragment) {
        val backStackCount = fragment.act!!.supportFragmentManager.backStackEntryCount
        for (i in 0 until backStackCount) {
            fragment.act!!.supportFragmentManager.popBackStackImmediate()
        }
    }
}