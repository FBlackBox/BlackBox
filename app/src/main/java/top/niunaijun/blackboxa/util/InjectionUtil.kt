package top.niunaijun.blackboxa.util

import top.niunaijun.blackboxa.data.AppsRepository
import top.niunaijun.blackboxa.data.FakeLocationRepository
import top.niunaijun.blackboxa.data.GmsRepository
import top.niunaijun.blackboxa.data.XpRepository
import top.niunaijun.blackboxa.view.apps.AppsFactory
import top.niunaijun.blackboxa.view.fake.FakeLocationFactory
import top.niunaijun.blackboxa.view.gms.GmsFactory
import top.niunaijun.blackboxa.view.list.ListFactory
import top.niunaijun.blackboxa.view.xp.XpFactory

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:38
 */
object InjectionUtil {

    private val appsRepository = AppsRepository()

    private val xpRepository = XpRepository()

    private val gmsRepository = GmsRepository()

    private val fakeLocationRepository = FakeLocationRepository()

    fun getAppsFactory() : AppsFactory {
        return AppsFactory(appsRepository)
    }

    fun getListFactory(): ListFactory {
        return ListFactory(appsRepository)
    }

    fun getXpFactory():XpFactory{
        return XpFactory(xpRepository)
    }

    fun getGmsFactory():GmsFactory{
        return GmsFactory(gmsRepository)
    }

    fun getFakeLocationFactory():FakeLocationFactory{
        return FakeLocationFactory(fakeLocationRepository)
    }
}