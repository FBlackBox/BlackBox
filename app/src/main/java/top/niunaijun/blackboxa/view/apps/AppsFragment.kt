package top.niunaijun.blackboxa.view.apps

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.roger.catloadinglibrary.CatLoadingView
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.FragmentAppsBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.LoadingUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.util.toast
import top.niunaijun.blackboxa.view.main.MainActivity
import top.niunaijun.blackboxa.view.main.ShortcutActivity


/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:21
 */
class AppsFragment : Fragment() {

    var userID: Int = 0

    private lateinit var viewModel: AppsViewModel

    private lateinit var mAdapter: AppsAdapter

    private val viewBinding: FragmentAppsBinding by inflate()

    private lateinit var loadingView: CatLoadingView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, InjectionUtil.getAppsFactory()).get(AppsViewModel::class.java)
        userID = requireArguments().getInt("userID",0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mAdapter = AppsAdapter()
        viewBinding.recyclerView.adapter = mAdapter
        viewBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        viewBinding.stateView.showEmpty()

        mAdapter.setOnItemClick { _, _, data ->
            showLoading()
            viewModel.launchApk(data.packageName, userID)
        }

        setAdapterLongClick()
        return viewBinding.root
    }

    private fun setAdapterLongClick() {
        mAdapter.setOnItemLongClick { _, binding, data ->
            PopupMenu(requireContext(), binding.root).also {
                it.inflate(R.menu.app_menu)
                it.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.app_remove -> {
                            if (data.isXpModule) {
                                toast("Xposed模块请在管理界面卸载")
                            } else {
                                unInstallApk(data)
                            }
                        }

                        R.id.app_clear -> {
                            clearApk(data)
                        }

                        R.id.app_stop -> {
                            stopApk(data)
                        }

                        R.id.app_shortcut -> {
                            createShortcut(data)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }
    }

    override fun onStart() {
        super.onStart()
        viewBinding.stateView.showLoading()
        viewModel.getInstalledApps(userID)
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
                requireContext().toast(it)
                viewModel.getInstalledApps(userID)
                scanUser()
            }

        }

        viewModel.launchLiveData.observe(this) {
            it?.run {
                hideLoading()
                if (!it) {
                    Toast.makeText(requireContext(), "启动失败", Toast.LENGTH_LONG).show()
                }
            }
        }
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


    private fun unInstallApk(info: AppInfo) {
        MaterialDialog(requireContext()).show {
            title(text = "卸载软件")
            message(text = "是否卸载\"${info.name}\"，卸载后相关数据将被清除？")
            positiveButton(text = "确定") {
                showLoading()
                viewModel.unInstall(info.packageName, userID)
            }
            negativeButton(text = "取消")
        }
    }

    /**
     * 强行停止软件
     * @param info AppInfo
     */
    private fun stopApk(info: AppInfo) {
        MaterialDialog(requireContext()).show {
            title(text = "停止运行")
            message(text = "是否强行停止运行\"${info.name}\"？")
            positiveButton(text = "确定") {
                BlackBoxCore.get().stopPackage(info.packageName, userID)
                toast("\"${info.name}\"已停止运行")
            }
            negativeButton(text = "取消")
        }
    }

    /**
     * 清除软件数据
     * @param info AppInfo
     */
    private fun clearApk(info: AppInfo) {
        MaterialDialog(requireContext()).show {
            title(text = "清除数据")
            message(text = "是否清除\"${info.name}\"的数据？")
            positiveButton(text = "确定") {
                showLoading()
                viewModel.clearApkData(info.packageName, userID)
            }
            negativeButton(text = "取消")
        }
    }

    /**
     * 创建桌面快捷方式
     * @param info AppInfo
     */
    private fun createShortcut(info: AppInfo) {

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(requireContext())) {
            val labelName = info.name + userID
            val intent = Intent(context, ShortcutActivity::class.java)
                .setAction(Intent.ACTION_MAIN)
                .putExtra("pkg", info.packageName)
                .putExtra("userId", userID)
            MaterialDialog(requireContext()).show {
                title(res = R.string.app_shortcut)
                input(
                    hintRes = R.string.shortcut_name,
                    prefill = labelName
                ) { _, input ->

                    val shortcutInfo: ShortcutInfoCompat =
                        ShortcutInfoCompat.Builder(requireContext(), info.packageName + userID)
                            .setIntent(intent)
                            .setShortLabel(input)
                            .setLongLabel(input)
                            .setIcon(IconCompat.createWithBitmap(info.icon.toBitmap()))
                            .build()
                    ShortcutManagerCompat.requestPinShortcut(requireContext(), shortcutInfo, null)

                }
                positiveButton(res = R.string.done)
                negativeButton(res = R.string.cancel)
            }

        } else {
            toast("该桌面不支持创建快捷方式")
        }
    }


    fun installApk(source: String) {
        showLoading()
        viewModel.install(source, userID)
    }


    private fun scanUser() {
        (requireActivity() as MainActivity).scanUser()
    }

    private fun showLoading() {
        if (!this::loadingView.isInitialized) {
            loadingView = CatLoadingView()
        }

        LoadingUtil.showLoading(loadingView, childFragmentManager)
    }


    private fun hideLoading() {
        if (this::loadingView.isInitialized) {
            loadingView.dismiss()
        }
    }


    companion object{
        fun newInstance(userID:Int): AppsFragment {
            val fragment = AppsFragment()
            val bundle = bundleOf("userID" to userID)
            fragment.arguments = bundle
            return fragment
        }
    }



}
