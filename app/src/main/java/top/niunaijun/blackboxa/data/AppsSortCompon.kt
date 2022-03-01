package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo

/**
 *
 * @Description: app sort
 * @Author: BlackBox
 * @CreateDate: 2022/2/27 23:21
 */
class AppsSortComparator(private val sortedList: List<String>) : Comparator<ApplicationInfo> {
    override fun compare(o1: ApplicationInfo?, o2: ApplicationInfo?): Int {
        if (o1 == null || o2 == null) {
            return 0
        }

        val first = sortedList.indexOf(o1.packageName)
        val second = sortedList.indexOf(o2.packageName)
        return first - second

    }
}