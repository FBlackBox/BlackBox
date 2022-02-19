package de.robv.android.xposed;

import android.util.Log;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

import top.canyie.pine.Pine;
import top.canyie.pine.callback.MethodHook;
import top.canyie.pine.xposed.PineXposed;

/**
 * This class contains most of Xposed's central logic, such as initialization and callbacks used by
 * the native side. It also includes methods to add new hooks.
 */
public final class XposedBridge {
	// Pine removed: Useless apis

	// Pine added: New API for querying supported features
	private static String[] sSupportedFeatures = new String[0];

	/**
	 * The system class loader which can be used to locate Android framework classes.
	 * Application classes cannot be retrieved from it.
	 *
	 * @see ClassLoader#getSystemClassLoader
	 */
	public static final ClassLoader BOOTCLASSLOADER = ClassLoader.getSystemClassLoader();

	/** @hide */
	public static final String TAG = PineXposed.TAG;

	/** @deprecated Use {@link #getXposedVersion()} instead. */
	@Deprecated
	public static int XPOSED_BRIDGE_VERSION = 90;

	// built-in handlers
	private static final Map<Member, CopyOnWriteSortedSet<XC_MethodHook>> sHookedMethodCallbacks = new HashMap<>();

	// Pine changed: Move sLoadedPackageCallbacks to PineXposed.
	// /*package*/ static final CopyOnWriteSortedSet<XC_LoadPackage> sLoadedPackageCallbacks = new CopyOnWriteSortedSet<>();

	private XposedBridge() {}

	/**
	 * Returns the currently installed version of the Xposed framework.
	 */
	public static int getXposedVersion() {
		return XPOSED_BRIDGE_VERSION;
	}

	// Pine added
	public static void setXposedVersion(int version) {
		XPOSED_BRIDGE_VERSION = version;
	}

	// Pine added: New API for querying supported features
	public static boolean isFeatureSupported(String featureName) {
		for (String f : sSupportedFeatures) {
			if (f.equalsIgnoreCase(featureName)) return true;
		}
		return false;
	}

	public static String[] getSupportedFeatures() {
		return sSupportedFeatures;
	}

	public static void setSupportedFeatures(String[] features) {
		sSupportedFeatures = features;
	}

	/**
	 * Writes a message to the Xposed error log.
	 *
	 * <p class="warning"><b>DON'T FLOOD THE LOG!!!</b> This is only meant for error logging.
	 * If you want to write information/debug messages, use logcat.
	 *
	 * @param text The log message.
	 */
	public static synchronized void log(String text) {
		Log.i(TAG, text);
	}

	/**
	 * Logs a stack trace to the Xposed error log.
	 *
	 * <p class="warning"><b>DON'T FLOOD THE LOG!!!</b> This is only meant for error logging.
	 * If you want to write information/debug messages, use logcat.
	 *
	 * @param t The Throwable object for the stack trace.
	 */
	public static synchronized void log(Throwable t) {
		Log.e(TAG, Log.getStackTraceString(t));
	}

	/**
	 * Hook any method (or constructor) with the specified callback. See below for some wrappers
	 * that make it easier to find a method/constructor in one step.
	 *
	 * @param hookMethod The method to be hooked.
	 * @param callback The callback to be executed when the hooked method is called.
	 * @return An object that can be used to remove the hook.
	 *
	 * @see XposedHelpers#findAndHookMethod(String, ClassLoader, String, Object...)
	 * @see XposedHelpers#findAndHookMethod(Class, String, Object...)
	 * @see #hookAllMethods
	 * @see XposedHelpers#findAndHookConstructor(String, ClassLoader, Object...)
	 * @see XposedHelpers#findAndHookConstructor(Class, Object...)
	 * @see #hookAllConstructors
	 */
	public static XC_MethodHook.Unhook hookMethod(Member hookMethod, XC_MethodHook callback) {
		if (!(hookMethod instanceof Method) && !(hookMethod instanceof Constructor<?>)) {
			throw new IllegalArgumentException("Only methods and constructors can be hooked: " + hookMethod.toString());
		}
		// Pine changed: We can hook interfaces's non-abstract methods
		/*else if (hookMethod.getDeclaringClass().isInterface()) {
			throw new IllegalArgumentException("Cannot hook interfaces: " + hookMethod.toString());
		}*/ else if (Modifier.isAbstract(hookMethod.getModifiers())) {
			throw new IllegalArgumentException("Cannot hook abstract methods: " + hookMethod.toString());
		}

		boolean newMethod = false;
		CopyOnWriteSortedSet<XC_MethodHook> callbacks;
		synchronized (sHookedMethodCallbacks) {
			callbacks = sHookedMethodCallbacks.get(hookMethod);
			if (callbacks == null) {
				callbacks = new CopyOnWriteSortedSet<>();
				sHookedMethodCallbacks.put(hookMethod, callbacks);
				newMethod = true;
			}
		}
		callbacks.add(callback);

		if (newMethod) {
			Handler handler = new Handler(callbacks);
			Pine.hook(hookMethod, handler);
		}

		return callback.new Unhook(hookMethod);
	}

	/**
	 * Removes the callback for a hooked method/constructor.
	 *
	 * @deprecated Use {@link XC_MethodHook.Unhook#unhook} instead. An instance of the {@code Unhook}
	 * class is returned when you hook the method.
	 *
	 * @param hookMethod The method for which the callback should be removed.
	 * @param callback The reference to the callback as specified in {@link #hookMethod}.
	 */
	@Deprecated
	public static void unhookMethod(Member hookMethod, XC_MethodHook callback) {
		CopyOnWriteSortedSet<XC_MethodHook> callbacks;
		synchronized (sHookedMethodCallbacks) {
			callbacks = sHookedMethodCallbacks.get(hookMethod);
			if (callbacks == null)
				return;
		}
		callbacks.remove(callback);
	}

	/**
	 * Hooks all methods with a certain name that were declared in the specified class. Inherited
	 * methods and constructors are not considered. For constructors, use
	 * {@link #hookAllConstructors} instead.
	 *
	 * @param hookClass The class to check for declared methods.
	 * @param methodName The name of the method(s) to hook.
	 * @param callback The callback to be executed when the hooked methods are called.
	 * @return A set containing one object for each found method which can be used to unhook it.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
		Set<XC_MethodHook.Unhook> unhooks = new HashSet<>();
		for (Member method : hookClass.getDeclaredMethods())
			if (method.getName().equals(methodName))
				unhooks.add(hookMethod(method, callback));
		return unhooks;
	}

	/**
	 * Hook all constructors of the specified class.
	 *
	 * @param hookClass The class to check for constructors.
	 * @param callback The callback to be executed when the hooked constructors are called.
	 * @return A set containing one object for each found constructor which can be used to unhook it.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
		Set<XC_MethodHook.Unhook> unhooks = new HashSet<>();
		for (Member constructor : hookClass.getDeclaredConstructors())
			unhooks.add(hookMethod(constructor, callback));
		return unhooks;
	}

	// Pine changed: removed handleHookedMethod(), it be implemented in Handler.class
	// Pine changed: removed hookXxx(), it be implemented in PineXposed.class

	/**
	 * Basically the same as {@link Method#invoke}, but calls the original method
	 * as it was before the interception by Xposed. Also, access permissions are not checked.
	 *
	 * <p class="caution">There are very few cases where this method is needed. A common mistake is
	 * to replace a method and then invoke the original one based on dynamic conditions. This
	 * creates overhead and skips further hooks by other modules. Instead, just hook (don't replace)
	 * the method and call {@code param.setResult(null)} in {@link XC_MethodHook#beforeHookedMethod}
	 * if the original method should be skipped.
	 *
	 * @param method The method to be called.
	 * @param thisObject For non-static calls, the "this" pointer, otherwise {@code null}.
	 * @param args Arguments for the method call as Object[] array.
	 * @return The result returned from the invoked method.
	 * @throws NullPointerException
	 *             if {@code receiver == null} for a non-static method
	 * @throws IllegalAccessException
	 *             if this method is not accessible (see {@link AccessibleObject})
	 * @throws IllegalArgumentException
	 *             if the number of arguments doesn't match the number of parameters, the receiver
	 *             is incompatible with the declaring class, or an argument could not be unboxed
	 *             or converted by a widening conversion to the corresponding parameter type
	 * @throws InvocationTargetException
	 *             if an exception was thrown by the invoked method
	 */
	public static Object invokeOriginalMethod(Member method, Object thisObject, Object[] args)
			throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return Pine.invokeOriginalMethod(method, thisObject, args);
	}

	// Pine added: Handler class for help dispatch
	/** @hide */
	static final class Handler extends MethodHook {
		private final CopyOnWriteSortedSet<XC_MethodHook> callbacks;
		private final ThreadLocal<ExtData> extDataHolder = new ThreadLocal<>();

		Handler(CopyOnWriteSortedSet<XC_MethodHook> callbacks) {
			this.callbacks = callbacks;
		}

		@Override public void beforeCall(Pine.CallFrame callFrame) {
			// Pine changed: Member disableHooks in PineXposed and can modify by user
			if (PineXposed.disableHooks) return;

			Object[] callbacksSnapshot = callbacks.getSnapshot();
			final int callbacksLength = callbacksSnapshot.length;
			if (callbacksLength == 0) return;

			ExtData extData = extDataHolder.get();
			if (extData == null) {
				extData = new ExtData();
				extDataHolder.set(extData);
			}

			MethodHookParam param = new MethodHookParam();
			param.method = callFrame.method;
			param.thisObject = callFrame.thisObject;
			param.args = callFrame.args;

			// call "before method" callbacks
			int beforeIdx = 0;
			do {
				try {
					((XC_MethodHook) callbacksSnapshot[beforeIdx]).beforeHookedMethod(param);
				} catch (Throwable t) {
					XposedBridge.log(t);

					// reset result (ignoring what the unexpectedly exiting callback did)
					param.setResult(null);
					param.returnEarly = false;
					continue;
				}

				if (param.returnEarly) {
					// skip remaining "before" callbacks and corresponding "after" callbacks
					beforeIdx++;
					break;
				}
			} while (++beforeIdx < callbacksLength);

			// Pine added: Flush MethodHookParam changes to CallFrame
			callFrame.thisObject = param.thisObject;
			callFrame.args = param.args;
			if (param.returnEarly) {
				if (param.hasThrowable())
					callFrame.setThrowable(param.getThrowable());
				else
					callFrame.setResult(param.getResult());
			}

			extData.callbacks = callbacksSnapshot;
			extData.param = param;
			extData.afterIdx = beforeIdx - 1;
		}

		@Override public void afterCall(Pine.CallFrame callFrame) {
			ExtData extData = extDataHolder.get();
			if (extData == null) return;

			Object[] callbacksSnapshot = extData.callbacks;
			if (callbacksSnapshot == null) return;
			MethodHookParam param = extData.param;
			int afterIdx = extData.afterIdx;

			// Flush CallFrame changes to MethodHookParam
			param.thisObject = callFrame.thisObject;
			param.args = callFrame.args;
			if (callFrame.hasThrowable())
				param.setThrowable(callFrame.getThrowable());
			else
				param.setResult(callFrame.getResult());

			// call "after method" callbacks
			do {
				Object lastResult = param.getResult();
				Throwable lastThrowable = param.getThrowable();

				try {
					((XC_MethodHook) callbacksSnapshot[afterIdx]).afterHookedMethod(param);
				} catch (Throwable t) {
					XposedBridge.log(t);

					// reset to last result (ignoring what the unexpectedly exiting callback did)
					if (lastThrowable == null)
						param.setResult(lastResult);
					else
						param.setThrowable(lastThrowable);
				}
			} while (--afterIdx >= 0);

			// Pine added: Flush MethodHookParam changes to CallFrame
			callFrame.thisObject = param.thisObject;
			callFrame.args = param.args;

			if (param.hasThrowable())
				callFrame.setThrowable(param.getThrowable());
			else
				callFrame.setResult(param.getResult());

			// Clean up.
			extData.callbacks = null;
			extData.param = null;
			extData.afterIdx = 0;
		}

		static final class ExtData {
			Object[] callbacks;
			MethodHookParam param;
			int afterIdx;

			ExtData() {}
		}
	}

	/** @hide */
	public static final class CopyOnWriteSortedSet<E> {
		// Pine changed: Use Pine.EMPTY_OBJECT_ARRAY
		private transient volatile Object[] elements = Pine.EMPTY_OBJECT_ARRAY;

		@SuppressWarnings("UnusedReturnValue")
		public synchronized boolean add(E e) {
			int index = indexOf(e);
			if (index >= 0)
				return false;

			Object[] newElements = new Object[elements.length + 1];
			System.arraycopy(elements, 0, newElements, 0, elements.length);
			newElements[elements.length] = e;
			Arrays.sort(newElements);
			elements = newElements;
			return true;
		}

		@SuppressWarnings("UnusedReturnValue")
		public synchronized boolean remove(E e) {
			int index = indexOf(e);
			if (index == -1)
				return false;

			Object[] newElements = new Object[elements.length - 1];
			System.arraycopy(elements, 0, newElements, 0, index);
			System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
			elements = newElements;
			return true;
		}

		private int indexOf(Object o) {
			for (int i = 0; i < elements.length; i++) {
				if (o.equals(elements[i]))
					return i;
			}
			return -1;
		}

		public Object[] getSnapshot() {
			return elements;
		}
	}
}
