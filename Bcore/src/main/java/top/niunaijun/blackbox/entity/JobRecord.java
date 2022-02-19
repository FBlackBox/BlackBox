package top.niunaijun.blackbox.entity;

import android.app.job.JobInfo;
import android.app.job.JobService;
import android.content.pm.ServiceInfo;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class JobRecord implements Parcelable {

    public JobInfo mJobInfo;
    public ServiceInfo mServiceInfo;

    public JobService mJobService;

    public JobRecord() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mJobInfo, flags);
        dest.writeParcelable(this.mServiceInfo, flags);
    }

    protected JobRecord(Parcel in) {
        this.mJobInfo = in.readParcelable(JobInfo.class.getClassLoader());
        this.mServiceInfo = in.readParcelable(ServiceInfo.class.getClassLoader());
    }

    public static final Creator<JobRecord> CREATOR = new Creator<JobRecord>() {
        @Override
        public JobRecord createFromParcel(Parcel source) {
            return new JobRecord(source);
        }

        @Override
        public JobRecord[] newArray(int size) {
            return new JobRecord[size];
        }
    };
}
