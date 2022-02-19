package black.android.content;

import android.content.Intent;
import android.os.Bundle;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

@BClassName("android.content.IIntentReceiver")
public interface IIntentReceiver {
    @BMethod
    void performReceive(Intent Intent0, int int1, String String2, Bundle Bundle3, boolean boolean4, boolean boolean5);
}
