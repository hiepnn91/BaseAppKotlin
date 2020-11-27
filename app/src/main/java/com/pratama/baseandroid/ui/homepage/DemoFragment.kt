package com.example.customnavigationdrawerexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import com.github.ajalt.timberkt.e
import com.pratama.baseandroid.R
import com.pratama.baseandroid.coreandroid.BaseFragment
import com.pratama.baseandroid.model.Event
import com.pratama.baseandroid.ui.homepage.HomePageState
import com.pratama.baseandroid.ui.homepage.HomePageViewModel
import com.pratama.baseandroid.ui.homepage.SecondFragment
import com.pratama.baseandroid.ui.homepage.rvitem.NewsItem
import com.pratama.baseandroid.utils.EventBus
import com.pratama.baseandroid.utils.FragmentUtil.addFragment
import com.pratama.baseandroid.utils.Logg
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_demo.view.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DemoFragment : BaseFragment() {
    var mNameFragment: String? = null

    @BindView(R.id.fragment_name)
    lateinit var tv: TextView

    @Inject
    lateinit var homeViewModel: HomePageViewModel
    private val homeAdapter = GroupAdapter<GroupieViewHolder>()
    fun newInstance(nameFragment: String?): DemoFragment {
        val fm = DemoFragment()
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
        val fragmentName = arguments?.getString("fragmentName")
        root!!.fragment_name.text = fragmentName
        EventBus.post(Event.SomeEvent("DemoFragment"))
        root!!.btnGotoSecond.setOnClickListener {
            val secondFragment = SecondFragment().newInstance("fasfas")
            addFragment(secondFragment!!, R.id.container)
        }
//        setCustomToolbar(true, this.activity!!, R.id.activity_main_toolbar)
        homeViewModel.getTopHeadlinesByCountry(
            country = "us",
            category = "business"
        )

        homeViewModel.homePageState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is HomePageState.NewsLoadedState -> {
                    for (i in 0..1) {
                        homeAdapter.add(NewsItem(state.news.get(i)))
                    }
//                    state.news.map {
//                        homeAdapter.add(NewsItem(it))
//                    }
                }

                is HomePageState.ErrorState -> {
                    e { "error ${state.message}" }
                }
            }

        })
        root!!.rvHomePage.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        root!!.rvHomePage.adapter = homeAdapter
        root!!.rvHomePage.addOnItemTouchListener(
            RecyclerTouchListener(
                this.requireActivity(),
                object : ClickListener {
                    override fun onClick(view: View, position: Int) {
                        when (position) {
                            0 -> {
                                Toast.makeText(this@DemoFragment.context, "1", Toast.LENGTH_SHORT)
                                    .show()
                                Timber.e("e")
                            }
                        }
                    }
                })
        )

    }
    override fun initData() {
        Logg.e("initData")
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_demo
    }

    override fun onRestore() {
        Logg.e("onRestore")
    }

    override fun initialize() {
        Logg.e("initialize")
    }

    override fun onSaveState(bundle: Bundle?) {
        Logg.e("onSaveState")
        bundle!!.putString("fragmentName", "rename")
    }

    override fun onRestoreState(bundle: Bundle?) {
        Logg.e("onRestoreState")
        mNameFragment = bundle!!.getString("fragmentName")
        Logg.e(mNameFragment)
    }


//    override fun processCustomToolbar() {
//        loadMenuLeft()
//    }
//
//    override fun getIconLeft(): Drawable? {
//        return this.resources.getDrawable(R.drawable.ic_menu_left)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
