package top.niunaijun.blackboxa.view.xp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cbfg.rvadapter.RVAdapter
import com.afollestad.materialdialogs.MaterialDialog
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.XpModuleInfo
import top.niunaijun.blackboxa.databinding.ActivityXpBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.util.toast
import top.niunaijun.blackboxa.view.base.LoadingActivity
import top.niunaijun.blackboxa.view.list.ListActivity

/**
 *
 * @Description: xposed模块管理界面
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 20:25
 */
class XpActivity : LoadingActivity() {

    private val viewBinding: ActivityXpBinding by inflate()


    private lateinit var viewModel: XpViewModel

    private lateinit var mAdapter: RVAdapter<XpModuleInfo>

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
            if (it.isNullOrEmpty()) {
                viewBinding.stateView.showEmpty()
            } else {
                mAdapter.setItems(it)
                viewBinding.stateView.showContent()
            }
        }

        viewModel.resultLiveData.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                hideLoading()
                toast(it)
                viewModel.getInstalledModule()
            }
        }
    }

        private fun initRecyclerView() {

            mAdapter = RVAdapter<XpModuleInfo>(this, XpAdapter()).bind(viewBinding.recyclerView)
                .setItemClickListener { view, item, position ->
                    item.enable = !item.enable
                    BlackBoxCore.get().setModuleEnable(item.packageName, item.enable)
                    mAdapter.replaceAt(position, item)
                    toast(R.string.restart_module)
                }.setItemLongClickListener { _, item, _ ->
                    unInstallModule(item.packageName)
                }

            viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
            viewBinding.stateView.showEmpty()
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
    }


    private fun unInstallModule(packageName: String) {
        MaterialDialog(this).show {
            title(R.string.uninstall_module)
            message(R.string.uninstall_module_hint)
            positiveButton(R.string.done) {
                showLoading()
                viewModel.unInstallModule(packageName)
            }
            negativeButton(R.string.cancel)
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



    companion object {
        fun start(context: Context) {
            val intent = Intent(context, XpActivity::class.java)
            context.startActivity(intent)
        }
    }

}