package top.niunaijun.blackboxa.view.fake

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cbfg.rvadapter.RVAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.ferfalk.simplesearchview.SimpleSearchView
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackbox.fake.frameworks.BLocationManager
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.databinding.ActivityListBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.util.toast
import top.niunaijun.blackboxa.view.base.BaseActivity

/**
 *
 * @Author: BlackBoxing
 * @CreateDate: 2022/3/14
 */
class FakeManagerActivity : BaseActivity() {
    val TAG: String = "FakeManagerActivity"

    private val viewBinding: ActivityListBinding by inflate()

    //    private lateinit var mAdapter: ListAdapter
    private lateinit var mAdapter: RVAdapter<FakeLocationBean>

    private lateinit var viewModel: FakeLocationViewModel

    private var appList: List<FakeLocationBean> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initToolbar(viewBinding.toolbarLayout.toolbar, R.string.fake_location, true)

        mAdapter = RVAdapter<FakeLocationBean>(this,FakeLocationAdapter()).bind(viewBinding.recyclerView)
            .setItemClickListener { _, data, _ ->

                val intent = Intent(this, FollowMyLocationOverlay::class.java)
                intent.putExtra("location", data.fakeLocation)
                intent.putExtra("pkg", data.packageName)

                locationResult.launch(intent)
            }.setItemLongClickListener { _, item, position ->
                disableFakeLocation(item,position)
            }

        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)


        initSearchView()
        initViewModel()
    }

    private fun disableFakeLocation(item: FakeLocationBean,position:Int) {
        MaterialDialog(this).show {
            title(R.string.close_fake_location)
            message(text = getString(R.string.close_app_fake_location,item.name))
            negativeButton(R.string.cancel)
            positiveButton(R.string.done){
                BLocationManager.disableFakeLocation(currentUserID(),item.packageName)
                toast(getString(R.string.close_fake_location_success,item.name))
                item.fakeLocationPattern = BLocationManager.CLOSE_MODE
                mAdapter.replaceAt(position,item)
            }
        }
    }

    private fun initSearchView() {
        viewBinding.searchView.setOnQueryTextListener(object :
            SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                filterApp(newText)
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, InjectionUtil.getFakeLocationFactory()).get(
            FakeLocationViewModel::class.java
        )
        loadAppList()
        viewBinding.toolbarLayout.toolbar.setTitle(R.string.fake_location)

        viewModel.appsLiveData.observe(this) {
            if (it != null) {
                this.appList = it
                viewBinding.searchView.setQuery("", false)
                filterApp("")
                if (it.isNotEmpty()) {
                    viewBinding.stateView.showContent()
                } else {
                    viewBinding.stateView.showEmpty()
                }
            }
        }
    }

    private fun loadAppList() {
        viewBinding.stateView.showLoading()
        viewModel.getInstallAppList(currentUserID())
    }

    private val locationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {

                it.data?.let { data ->
                    val latitude = data.getDoubleExtra("latitude", 0.0)
                    val longitude = data.getDoubleExtra("longitude", 0.0)
                    val pkg = data.getStringExtra("pkg")

                    viewModel.setPattern(currentUserID(), pkg, BLocationManager.OWN_MODE)
                    viewModel.setLocation(currentUserID(), pkg, BLocation(latitude, longitude))

                    toast(getString(R.string.set_location,latitude.toString(), longitude.toString()))

                    loadAppList()
                }

            }
        }


    private fun filterApp(newText: String) {
        val newList = this.appList.filter {
            it.name.contains(newText, true) or it.packageName.contains(newText, true)
        }
        mAdapter.setItems(newList)
    }

    private fun finishWithResult(source: String) {
        intent.putExtra("source", source)
        setResult(Activity.RESULT_OK, intent)
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.peekDecorView()?.run {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        finish()
    }


    override fun onBackPressed() {
        if (viewBinding.searchView.isSearchOpen) {
            viewBinding.searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val item = menu!!.findItem(R.id.list_search)
        viewBinding.searchView.setMenuItem(item)
        return true
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FakeManagerActivity::class.java)
            context.startActivity(intent)
        }
    }
}