package top.niunaijun.blackboxa.view.fake

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackbox.fake.frameworks.BLocationManager
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.databinding.ActivityListBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.view.base.BaseActivity
import kotlin.properties.Delegates

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
    private var localUserId by Delegates.notNull<Int>()
    private lateinit var localPackageName: String
    private var appList: List<FakeLocationBean> = ArrayList()

    private val locationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { data ->
                    val latitude: Double = data.extras.get("latitude") as Double
                    val longitude: Double = data.extras.get("longitude") as Double
                    viewModel.setPattern(localUserId, localPackageName, BLocationManager.OWN_MODE)
                    viewModel.setLocation(
                        localUserId,
                        localPackageName,
                        BLocation(latitude, longitude)
                    )
                    Toast.makeText(
                        baseContext,
                        getString(R.string.set_location) + ": " + latitude.toString() + " - " + longitude.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    refreshViewModel()
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initToolbar(viewBinding.toolbarLayout.toolbar, R.string.fake_location, true)

        mAdapter = FakeLocationAdapter()
        viewBinding.recyclerView.adapter = mAdapter
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter.setOnItemClick { _, _, data ->
            localUserId = data.userID
            localPackageName = data.packageName

            val intent = Intent(this, FollowMyLocationOverlay::class.java)
            if (data.fakeLocation == null) {
                intent.putExtra("notEmpty", false)
            } else {
                intent.putExtra("notEmpty", true)
            }
            intent.putExtra("location", data.fakeLocation)
            locationResult.launch(intent)
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

    private fun refreshViewModel() {
        val userID = intent.getIntExtra("userID", 0)
        viewModel.getInstallAppList(userID)
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