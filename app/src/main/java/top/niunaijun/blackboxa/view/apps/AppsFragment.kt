package top.niunaijun.blackboxa.view.apps

import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import cbfg.rvadapter.RVAdapter
import com.afollestad.materialdialogs.MaterialDialog
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.FragmentAppsBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.ShortcutUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.util.toast
import top.niunaijun.blackboxa.view.base.LoadingActivity
import top.niunaijun.blackboxa.view.main.MainActivity
import java.util.*
import kotlin.math.abs


/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:21
 */
class AppsFragment : Fragment() {

    var userID: Int = 0

    private lateinit var viewModel: AppsViewModel

    private lateinit var mAdapter: RVAdapter<AppInfo>

    private val viewBinding: FragmentAppsBinding by inflate()

    private var popupMenu: PopupMenu? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, InjectionUtil.getAppsFactory()).get(AppsViewModel::class.java)
        userID = requireArguments().getInt("userID", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewBinding.stateView.showEmpty()

        mAdapter =
            RVAdapter<AppInfo>(requireContext(), AppsAdapter()).bind(viewBinding.recyclerView)

        viewBinding.recyclerView.adapter = mAdapter
        viewBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        val touchCallBack = AppsTouchCallBack { from, to ->
            onItemMove(from, to)
            viewModel.updateSortLiveData.postValue(true)
        }

        val itemTouchHelper = ItemTouchHelper(touchCallBack)
        itemTouchHelper.attachToRecyclerView(viewBinding.recyclerView)

        mAdapter.setItemClickListener { _, data, _ ->
            showLoading()
            viewModel.launchApk(data.packageName, userID)
        }


        interceptTouch()
        setOnLongClick()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getInstalledApps(userID)
    }

    /**
     * 拖拽优化
     */
    private fun interceptTouch() {
        val point = Point()
        viewBinding.recyclerView.setOnTouchListener { v, e ->
            when (e.action) {
                MotionEvent.ACTION_UP -> {
                    if (!isMove(point, e)) {
                        popupMenu?.show()
                    }
                    popupMenu = null
                    point.set(0, 0)
                }

                MotionEvent.ACTION_MOVE -> {
                    if (point.x == 0 && point.y == 0) {
                        point.x = e.rawX.toInt()
                        point.y = e.rawY.toInt()
                    }
                    isDownAndUp(point, e)

                    if (isMove(point, e)) {
                        popupMenu?.dismiss()
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun isMove(point: Point, e: MotionEvent): Boolean {
        val max = 40

        val x = point.x
        val y = point.y

        val xU = abs(x - e.rawX)
        val yU = abs(y - e.rawY)
        return xU > max || yU > max
    }

    private fun isDownAndUp(point: Point, e: MotionEvent) {
        val min = 10
        val y = point.y
        val yU = y - e.rawY

        if (abs(yU) > min) {
            (requireActivity() as MainActivity).showFloatButton(yU < 0)
        }
    }

    private fun onItemMove(fromPosition:Int, toPosition:Int){
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mAdapter.getItems(), i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mAdapter.getItems(), i, i - 1)
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition)
    }

    private fun setOnLongClick() {
        mAdapter.setItemLongClickListener { view, data, _ ->
            popupMenu = PopupMenu(requireContext(),view).also {
                it.inflate(R.menu.app_menu)
                it.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.app_remove -> {
                            if (data.isXpModule) {
                                toast(R.string.uninstall_module_toast)
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
                            ShortcutUtil.createShortcut(requireContext(), userID, data)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                it.show()
            }
        }
    }
    private fun initData() {
        viewBinding.stateView.showLoading()
        viewModel.getInstalledApps(userID)
        viewModel.appsLiveData.observe(viewLifecycleOwner) {

            if (it != null) {
                mAdapter.setItems(it)
                if (it.isEmpty()) {
                    viewBinding.stateView.showEmpty()
                } else {
                    viewBinding.stateView.showContent()
                }
            }
        }

        viewModel.resultLiveData.observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                hideLoading()
                requireContext().toast(it)
                viewModel.getInstalledApps(userID)
                scanUser()
            }

        }

        viewModel.launchLiveData.observe(viewLifecycleOwner) {
            it?.run {
                hideLoading()
                if (!it) {
                    toast(R.string.start_fail)
                }
            }
        }

        viewModel.updateSortLiveData.observe(viewLifecycleOwner) {
            if (this::mAdapter.isInitialized) {
                viewModel.updateApkOrder(userID, mAdapter.getItems())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.resultLiveData.value = null
        viewModel.launchLiveData.value = null
    }

    private fun unInstallApk(info: AppInfo) {
        MaterialDialog(requireContext()).show {
            title(R.string.uninstall_app)
            message(text = getString(R.string.uninstall_app_hint, info.name))
            positiveButton(R.string.done) {
                showLoading()
                viewModel.unInstall(info.packageName, userID)
            }
            negativeButton(R.string.cancel)
        }
    }

    /**
     * 强行停止软件
     * @param info AppInfo
     */
    private fun stopApk(info: AppInfo) {
        MaterialDialog(requireContext()).show {
            title(R.string.app_stop)
            message(text = getString(R.string.app_stop_hint,info.name))
            positiveButton(R.string.done) {
                BlackBoxCore.get().stopPackage(info.packageName, userID)
                toast(getString(R.string.is_stop,info.name))
            }
            negativeButton(R.string.cancel)
        }
    }

    /**
     * 清除软件数据
     * @param info AppInfo
     */
    private fun clearApk(info: AppInfo) {
        MaterialDialog(requireContext()).show {
            title(R.string.app_clear)
            message(text = getString(R.string.app_clear_hint,info.name))
            positiveButton(R.string.done) {
                showLoading()
                viewModel.clearApkData(info.packageName, userID)
            }
            negativeButton(R.string.cancel)
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
        if(requireActivity() is LoadingActivity){
            (requireActivity() as LoadingActivity).showLoading()
        }
    }


    private fun hideLoading() {
        if(requireActivity() is LoadingActivity){
            (requireActivity() as LoadingActivity).hideLoading()
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
