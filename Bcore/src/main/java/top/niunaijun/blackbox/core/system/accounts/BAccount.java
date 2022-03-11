package top.niunaijun.blackbox.core.system.accounts;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by BlackBox on 2022/3/3.
 */
public class BAccount implements Parcelable {
    public Account account;
    public String password;
    public HashMap<String, String> accountUserData = new LinkedHashMap<>();
    public HashMap<String, Integer> visibility = new LinkedHashMap<>();
    public HashMap<String, String> authTokens = new LinkedHashMap<>();
    public long updateLastAuthenticatedTime;

    public boolean isMatch(Account account) {
        if (account == null) return false;
        return account.equals(this.account);
    }

    public void insertExtra(String key, String value) {
        this.accountUserData.put(key, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.account, flags);
        dest.writeString(this.password);
        dest.writeSerializable(this.accountUserData);
        dest.writeSerializable(this.visibility);
        dest.writeSerializable(this.authTokens);
        dest.writeLong(this.updateLastAuthenticatedTime);
    }

    public void readFromParcel(Parcel source) {
        this.account = source.readParcelable(Account.class.getClassLoader());
        this.password = source.readString();
        this.accountUserData = (HashMap<String, String>) source.readSerializable();
        this.visibility = (HashMap<String, Integer>) source.readSerializable();
        this.authTokens = (HashMap<String, String>) source.readSerializable();
        this.updateLastAuthenticatedTime = source.readLong();
    }

    public BAccount() {
    }

    protected BAccount(Parcel in) {
        this.account = in.readParcelable(Account.class.getClassLoader());
        this.password = in.readString();
        this.accountUserData = (HashMap<String, String>) in.readSerializable();
        this.visibility = (HashMap<String, Integer>) in.readSerializable();
        this.authTokens = (HashMap<String, String>) in.readSerializable();
        this.updateLastAuthenticatedTime = in.readLong();
    }

    public static final Creator<BAccount> CREATOR = new Creator<BAccount>() {
        @Override
        public BAccount createFromParcel(Parcel source) {
            return new BAccount(source);
        }

        @Override
        public BAccount[] newArray(int size) {
            return new BAccount[size];
        }
    };
}
