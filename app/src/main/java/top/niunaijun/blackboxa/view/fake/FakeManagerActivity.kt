package top.niunaijun.blackboxa.view.fake

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.databinding.ActivityListBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.inflate
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
    private lateinit var mAdapter: FakeLocationAdapter

    private lateinit var viewModel: FakeLocationViewModel
//    private lateinit var viewModel: ListViewModel

    private var appList: List<FakeLocationBean> = ArrayList()

//    override fun setOnItemClick() {
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initToolbar(viewBinding.toolbarLayout.toolbar, R.string.fake_location, true)

        mAdapter = FakeLocationAdapter()
        viewBinding.recyclerView.adapter = mAdapter
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter.setOnItemClick { _, _, data ->
//            if (data.fakeLocation == null) {
//                Log.d(TAG, "null")
//            }
//            viewModel.setPattern(data.userID, data.packageName, BLocationManager.OWN_MODE)
//            val location: BLocation = BLocation(12.4, 124.1)
//            viewModel.setLocation(data.userID, data.packageName, location)
//            viewModel.set

            val intent = Intent(this, FollowMyLocationOverlay::class.java)
//            var bLocation = BLocation(12.34, 122.5)
//             if data.fakeLocation is null, activity get passed intent crash when extract object
//            intent.putExtra("notEmpty", true)
            if (data.fakeLocation == null) {
                intent.putExtra("notEmpty", false)
            } else {
                intent.putExtra("notEmpty", true)
            }
            intent.putExtra("location", data.fakeLocation)

//            intent.putExtra("name", "yes")
            startActivity(intent)
//            finishWithResult(data.packageName)
        }

        initSearchView()
        initViewModel()
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
        val userID = intent.getIntExtra("userID", 0)
        viewModel.getInstallAppList(userID)
        viewBinding.toolbarLayout.toolbar.setTitle(R.string.fake_location)

        viewModel.loadingLiveData.observe(this) {
            if (it) {
                viewBinding.stateView.showLoading()
            } else {
                viewBinding.stateView.showContent()

            }
        }

        viewModel.appsLiveData.observe(this) { its ->
            if (its != null) {
                this.appList = its
                viewBinding.searchView.setQuery("", false)
                filterApp("")
                if (its.isNotEmpty()) {
                    viewBinding.stateView.showContent()
                } else {
                    viewBinding.stateView.showEmpty()
                }
            }
        }
    }

    private fun filterApp(newText: String) {
        val newList = this.appList.filter {
            it.name.contains(newText, true) or it.packageName.contains(newText, true)
        }
        mAdapter.replaceData(newList)
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

    override fun onStop() {
        super.onStop()
        viewModel.loadingLiveData.postValue(true)
        viewModel.loadingLiveData.removeObservers(this)
        viewModel.appsLiveData.postValue(null)
        viewModel.appsLiveData.removeObservers(this)
    }


    companion object {
        fun start(context: Context, onlyShowXp: Boolean) {
            val intent = Intent(context, FollowMyLocationOverlay::class.java)
//            intent.putExtra("onlyShowXp", false)
            context.startActivity(intent)
        }
    }
}