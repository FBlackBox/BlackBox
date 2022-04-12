package top.niunaijun.blackbox.utils.compat;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import black.android.app.BRActivity;
import black.android.app.BRActivityManagerNative;
import black.android.app.BRIActivityManager;
import black.android.app.BRIActivityManagerL;
import black.android.app.BRIActivityManagerN;

public class ActivityManagerCompat {
	/** Type for IActivityManager.serviceDoneExecuting: anonymous operation */
	public static final int SERVICE_DONE_EXECUTING_ANON = 0;
	/** Type for IActivityManager.serviceDoneExecuting: done with an onStart call */
	public static final int SERVICE_DONE_EXECUTING_START = 1;
	/** Type for IActivityManager.serviceDoneExecuting: done stopping (destroying) service */
	public static final int SERVICE_DONE_EXECUTING_STOP = 2;
//
//	public static final int START_SUCCESS = ActivityManager.START_SUCCESS == null ?
//			0 : ActivityManager.START_SUCCESS.get();
//
//	/**
//	 * Result for IActivityManager.startActivity: an error where the
//	 * given Intent could not be resolved to an activity.
//	 */
//    public static final int START_INTENT_NOT_RESOLVED = ActivityManager.START_INTENT_NOT_RESOLVED == null ?
//            -1 : ActivityManager.START_INTENT_NOT_RESOLVED.get();
//
//	/**
//	 * Result for IActivityManager.startActivity: trying to start a background user
//	 * activity that shouldn't be displayed for all users.
//	 */
//	public static final int START_NOT_CURRENT_USER_ACTIVITY = ActivityManager.START_NOT_CURRENT_USER_ACTIVITY == null ?
//            -8 : ActivityManager.START_NOT_CURRENT_USER_ACTIVITY.get();
//
//	/**
//	 * Result for IActivityManaqer.startActivity: activity wasn't really started, but
//	 * a task was simply brought to the foreground.
//	 */
//	public static final int START_TASK_TO_FRONT = ActivityManager.START_TASK_TO_FRONT == null ?
//            2 : ActivityManager.START_TASK_TO_FRONT.get();

	/**
	 * Type for IActivityManaqer.getIntentSender: this PendingIntent is
	 * for a sendBroadcast operation.
	 */
	public static final int INTENT_SENDER_BROADCAST = 1;

	/**
	 * Type for IActivityManaqer.getIntentSender: this PendingIntent is
	 * for a startActivity operation.
	 */
	public static final int INTENT_SENDER_ACTIVITY = 2;

	/**
	 * Type for IActivityManaqer.getIntentSender: this PendingIntent is
	 * for an activity result operation.
	 */
	public static final int INTENT_SENDER_ACTIVITY_RESULT = 3;

	/**
	 * Type for IActivityManaqer.getIntentSender: this PendingIntent is
	 * for a startService operation.
	 */
	public static final int INTENT_SENDER_SERVICE = 4;

	/** User operation call: success! */
	public static final int USER_OP_SUCCESS = 0;

	public static final int START_FLAG_DEBUG = 1<<1;
	public static final int START_FLAG_TRACK_ALLOCATION = 1<<2;
	public static final int START_FLAG_NATIVE_DEBUGGING = 1<<3;

	public static boolean finishActivity(IBinder token, int code, Intent data) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return BRIActivityManagerN.get(BRActivityManagerNative.get().getDefault()).finishActivity(
					token, code, data, 0);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return BRIActivityManagerL.get(BRActivityManagerNative.get().getDefault()).finishActivity(
						token, code, data, false);
		}
		return false;
	}


    public static void setActivityOrientation(Activity activity, int orientation) {
        try {
            activity.setRequestedOrientation(orientation);
        } catch (Throwable e) {
            e.printStackTrace();
            //samsung is WindowManager.setRequestedOrientation
            Activity parent =  BRActivity.get(activity).mParent();
            while (true) {
				Activity tmp = BRActivity.get(parent).mParent();
				if (tmp != null) {
					parent = tmp;
				} else {
					break;
				}
			}
            IBinder token = BRActivity.get(parent).mToken();
            try {
				BRIActivityManager.get(BRActivityManagerNative.get().getDefault()).setRequestedOrientation(token, orientation);
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        }
    }
}
