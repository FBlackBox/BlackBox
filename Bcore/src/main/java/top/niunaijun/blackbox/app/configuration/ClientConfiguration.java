package top.niunaijun.blackbox.app.configuration;

/**
 * Created by Milk on 5/4/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public abstract class ClientConfiguration {

    public boolean isHideRoot() {
        return false;
    }

    public boolean isHideXposed() {
        return false;
    }

    public abstract String getHostPackageName();
}
