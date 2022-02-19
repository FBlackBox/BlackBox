package top.canyie.pine.callback;

import java.lang.reflect.Member;

import top.canyie.pine.Pine;

/**
 * Like {@link MethodHook} but replaces the whole method call.
 * Use this will always prevent the original method call and remaining hooks to be invoked expect
 * you manually call them. Make sure you are sure you really want this!
 * @author canyie
 * @see MethodHook
 * @see Pine#hook(java.lang.reflect.Member, MethodHook) 
 */
public abstract class MethodReplacement extends MethodHook {
    /**
     * Replace the method implementation to empty implementation. In other words, this method call 
     * will do nothing and return null.
     */
    public static final MethodReplacement DO_NOTHING = new MethodReplacement() {
        @Override protected Object replaceCall(Pine.CallFrame callFrame) {
            return null;
        }
    };

    /**
     * Invoked when the method gets calling. You can get or modify some info about this call by the given {@code callFrame}.
     * Replace whole method implementation with this. Returned value or thrown exception will become
     * the return result or exception of the method call.
     * @param callFrame object that stores some info about info call.
     * @return The return value of this method call
     * @throws Throwable The exception thrown from this method call
     */
    protected abstract Object replaceCall(Pine.CallFrame callFrame) throws Throwable;

    @Override public final void beforeCall(Pine.CallFrame callFrame) {
        try {
            callFrame.setResult(replaceCall(callFrame));
        } catch (Throwable e) {
            callFrame.setThrowable(e);
        }
    }

    @Override public final void afterCall(Pine.CallFrame callFrame) {
    }

    /**
     * Helper function to create an replacement instance when you just want to return a constant value.
     * @param result The value you want to return
     * @return An instance that can be used for {@link Pine#hook(Member, MethodHook)}
     */
    public static MethodReplacement returnConstant(final Object result) {
        return new MethodReplacement() {
            @Override protected Object replaceCall(Pine.CallFrame callFrame) {
                return result;
            }
        };
    }
}
