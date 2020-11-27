package com.pratama.baseandroid.ui.homepage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.customnavigationdrawerexample.*
import com.pratama.baseandroid.R
import com.pratama.baseandroid.coreandroid.BaseActivity
import com.pratama.baseandroid.model.Event
import com.pratama.baseandroid.utils.EventBus
import com.pratama.baseandroid.utils.FragmentUtil.addFragment
import com.pratama.baseandroid.utils.FragmentUtil.replaceFragment
import com.pratama.baseandroid.utils.Logg
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.app_bar_main.*


@AndroidEntryPoint
class HomePageActivity : BaseActivity() {
    private lateinit var adapter: NavigationRVAdapter

    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_home, "Home"),
        NavigationItemModel(R.drawable.ic_music, "Music"),
        NavigationItemModel(R.drawable.ic_movie, "Movies"),
        NavigationItemModel(R.drawable.ic_book, "Books"),
        NavigationItemModel(R.drawable.ic_profile, "Profile"),
        NavigationItemModel(R.drawable.ic_settings, "Settings"),
        NavigationItemModel(R.drawable.ic_social, "Like us on facebook")
    )

    override fun getLayoutID(): Int {
        return R.layout.activity_home_page
    }

    override fun onCreateActivity() {
        this.getPreferences(Context.MODE_PRIVATE).edit { putInt("test", 5) }
        var test = this.getPreferences(Context.MODE_PRIVATE).getInt("test", 1)
        drawerLayout = findViewById(R.id.drawer_layout)
        setSupportActionBar(activity_main_toolbar)

        // Setup Recyclerview's Layout
        navigation_rv.layoutManager = LinearLayoutManager(this)
        navigation_rv.setHasFixedSize(true)

        // Add Item Touch Listener
        navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        // # Home Fragment
                        val bundle = Bundle()
                        bundle.putString("fragmentName", "Home Fragment")
                        val homeFragment = DemoFragment()
                        homeFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, homeFragment).commit()
                    }
                    1 -> {
                        // # Music Fragment
                        val bundle = Bundle()
                        bundle.putString("fragmentName", "Music Fragment")
                        val musicFragment = DemoFragment()
                        musicFragment.arguments = bundle
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.activity_main_content_id, musicFragment).commit()

                        addFragment(musicFragment, R.id.container)
                    }
                    2 -> {
                        // # Movies Fragment
                        val bundle = Bundle()
                        bundle.putString("fragmentName", "Movies Fragment")
                        val moviesFragment = DemoFragment()
                        moviesFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, moviesFragment).commit()
                    }
                    3 -> {
                        val bundle = Bundle()
                        bundle.putString("fragmentName", "Books Fragment")
                        val booksFragment = DemoFragment()
                        booksFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, booksFragment).commit()
                    }
                    4 -> {
                        val intent = Intent(this@HomePageActivity, DemoActivity::class.java)
                        intent.putExtra("activityName", "Profile Activity")
                        startActivity(intent)
                    }
                    5 -> {
                        val bundle = Bundle()
                        bundle.putString("fragmentName", "Settings Fragment")
                        val settingsFragment = DemoFragment()
                        settingsFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, settingsFragment).commit()
                    }
                    6 -> {
                        // # Open URL in browser
                        val uri: Uri = Uri.parse("https://johnc.co/fb")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                }
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
                if (position != 6 && position != 4) {
                    updateAdapter(position)
                }
                Handler().postDelayed({
                    drawerLayout.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

        // Update Adapter with item data and highlight the default menu item ('Home' Fragment)
        updateAdapter(0)

        // Set 'Home' as the default fragment when the app starts
        val homeFragment = DemoFragment()
        replaceFragment(homeFragment.newInstance("hey"), R.id.container)

        // Close the soft keyboard when you open or close the Drawer
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            activity_main_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()


        // Set Header Image
//        navigation_header_img.setImageResource(R.drawable.logo)

        // Set background of Drawer
        navigation_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_left)// set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        disposable =
            EventBus.subscribe<Event.SomeEvent>()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logg.e("event received: $it")
                    activity_main_toolbar_title.text = "${it.text}"
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_add -> {
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show()
                true
            }
//            R.id.action_settings -> {
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main_drawer, menu)
        return true
    }

    private fun updateAdapter(highlightItemPos: Int) {
        adapter = NavigationRVAdapter(items, highlightItemPos)
        navigation_rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                // Exit the app
                super.onBackPressed()
            }
        }
    }
}