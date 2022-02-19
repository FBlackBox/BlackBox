package top.niunaijun.blackboxa.view.xp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.roger.catloadinglibrary.CatLoadingView
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.utils.compat.BuildCompat
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivityXpBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.LoadingUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.util.toast
import top.niunaijun.blackboxa.view.base.BaseActivity
import top.niunaijun.blackboxa.view.list.ListActivity

/**
 *
 * @Description: xposed模块管理界面
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 20:25
 */
class XpActivity : BaseActivity() {

    private val viewBinding: ActivityXpBinding by inflate()

    private lateinit var loadingView: CatLoadingView

    private lateinit var viewModel: XpViewModel

    private lateinit var mAdapter: XpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initToolbar(viewBinding.toolbarLayout.toolbar, R.string.xp_setting, true)

        viewModel = ViewModelProvider(this, InjectionUtil.getXpFactory()).get(XpViewModel::class.java)

        initRecyclerView()
        initFab()
    }


    private fun observeLiveData() {

        viewBinding.stateView.showLoading()
        viewModel.getInstalledModule()
        viewModel.appsLiveData.observe(this) {
            if (it != null) {
                mAdapter.replaceData(it)
                if (it.isEmpty()) {
                    viewBinding.stateView.showEmpty()
                } else {
                    viewBinding.stateView.showContent()
                }
            } else {
                viewBinding.stateView.showEmpty()
            }
        }

        viewModel.resultLiveData.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                hideLoading()
                toast(it)
                viewModel.getInstalledModule()
            }
        }

        viewModel.launchLiveData.observe(this) {
            it?.run {
                hideLoading()
                if (!it) {
                    toast("启动失败")
                }
            }
        }
    }

    private fun initRecyclerView() {

        mAdapter = XpAdapter()
        viewBinding.recyclerView.adapter = mAdapter
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewBinding.stateView.showEmpty()

        mAdapter.setOnItemClick { _, _, _ ->
            toast("请在外部启动模块")
        }

        mAdapter.setOnItemLongClick { _, _, data ->
            unInstallModule(data.packageName)
        }

        mAdapter.setOnCheckChangeListener { data, isChecked ->

            BlackBoxCore.get().setModuleEnable(data.packageName, isChecked)
            toast("重新启动软件修改才能生效")

        }
    }

    private fun initFab() {
        viewBinding.fab.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("onlyShowXp", true)
            apkPathResult.launch(intent)
        }

    }


    override fun onStart() {
        super.onStart()
        observeLiveData()
    }

    override fun onStop() {
        super.onStop()
        viewModel.appsLiveData.value = null
        viewModel.appsLiveData.removeObservers(this)
        viewModel.resultLiveData.value = null
        viewModel.resultLiveData.removeObservers(this)
        viewModel.launchLiveData.value = null
        viewModel.launchLiveData.removeObservers(this)
    }


    private fun unInstallModule(packageName: String) {
        MaterialDialog(this).show {
            title(text = "卸载模块")
            message(text = "是否卸载该模块，卸载后将无法再发挥作用？")
            positiveButton(text = "确定") {
                showLoading()
                viewModel.unInstallModule(packageName)
            }
            negativeButton(text = "取消")
        }
    }


    private fun installModule(source: String) {
        showLoading()
        viewModel.installModule(source)
    }


    private val apkPathResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            it.data?.let { data ->
                val source = data.getStringExtra("source")
                if (source != null) {
                    installModule(source)
                }
            }

        }
    }

    private fun showLoading() {
        if (!this::loadingView.isInitialized) {
            loadingView = CatLoadingView()
        }

        LoadingUtil.showLoading(loadingView, supportFragmentManager)
    }


    private fun hideLoading() {
        if (this::loadingView.isInitialized) {
            loadingView.dismiss()
        }
    }


}