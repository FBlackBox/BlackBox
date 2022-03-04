package black.com.android.internal;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

@BClassName("com.android.internal.R")
public interface R {
    @BClassName("com.android.internal.R$styleable")
    interface styleable {
        @BStaticField
        int[] AccountAuthenticator();

        @BStaticField
        int AccountAuthenticator_accountPreferences();

        @BStaticField
        int AccountAuthenticator_accountType();

        @BStaticField
        int AccountAuthenticator_customTokens();

        @BStaticField
        int AccountAuthenticator_icon();

        @BStaticField
        int AccountAuthenticator_label();

        @BStaticField
        int AccountAuthenticator_smallIcon();

        @BStaticField
        Object SyncAdapter();

        @BStaticField
        int SyncAdapter_accountType();

        @BStaticField
        int SyncAdapter_allowParallelSyncs();

        @BStaticField
        int SyncAdapter_contentAuthority();

        @BStaticField
        int SyncAdapter_isAlwaysSyncable();

        @BStaticField
        int SyncAdapter_settingsActivity();

        @BStaticField
        int SyncAdapter_supportsUploading();

        @BStaticField
        int SyncAdapter_userVisible();

        @BStaticField
        Object View();

        @BStaticField
        int View_background();

        @BStaticField
        int[] Window();

        @BStaticField
        int Window_windowBackground();

        @BStaticField
        int Window_windowDisablePreview();

        @BStaticField
        int Window_windowFullscreen();

        @BStaticField
        int Window_windowIsFloating();

        @BStaticField
        int Window_windowIsTranslucent();

        @BStaticField
        int Window_windowShowWallpaper();
    }

    @BClassName("com.android.internal.R$drawable")
    interface drawable {
        @BStaticField
        int popup_bottom_bright();

        @BStaticField
        int popup_bottom_dark();

        @BStaticField
        int popup_bottom_medium();

        @BStaticField
        int popup_center_bright();

        @BStaticField
        int popup_center_dark();

        @BStaticField
        int popup_full_bright();

        @BStaticField
        int popup_full_dark();

        @BStaticField
        int popup_top_bright();

        @BStaticField
        int popup_top_dark();
    }

    @BClassName("com.android.internal.R$layout")
    interface layout {
        @BStaticField
        int resolver_list();
    }

    @BClassName("com.android.internal.R$id")
    interface id {
        @BStaticField
        int alertTitle();

        @BStaticField
        int button1();

        @BStaticField
        int button2();

        @BStaticField
        int button3();

        @BStaticField
        int buttonPanel();

        @BStaticField
        int contentPanel();

        @BStaticField
        int custom();

        @BStaticField
        int customPanel();

        @BStaticField
        int icon();

        @BStaticField
        int leftSpacer();

        @BStaticField
        int message();

        @BStaticField
        int resolver_list();

        @BStaticField
        int rightSpacer();

        @BStaticField
        int scrollView();

        @BStaticField
        int text1();

        @BStaticField
        int text2();

        @BStaticField
        int titleDivider();

        @BStaticField
        int titleDividerTop();

        @BStaticField
        int title_template();

        @BStaticField
        int topPanel();
    }
}
