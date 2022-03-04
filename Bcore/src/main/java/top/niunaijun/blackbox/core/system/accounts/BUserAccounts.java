package top.niunaijun.blackbox.core.system.accounts;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BlackBox on 2022/3/3.
 */
public class BUserAccounts implements Parcelable {
    public final Object lock = new Object();

    public int userId;
    public List<BAccount> accounts = new ArrayList<>();

    public Account[] toAccounts() {
        List<Account> local = new ArrayList<>();
        for (BAccount account : accounts) {
            local.add(account.account);
        }
        return local.toArray(new Account[]{});
    }

    public BAccount addAccount(Account account) {
        BAccount bAccount = new BAccount();
        bAccount.account = account;
        accounts.add(bAccount);
        return bAccount;
    }

    public BAccount getAccount(Account account) {
        for (BAccount bAccount : accounts) {
            if (bAccount.isMatch(account))
                return bAccount;
        }
        return null;
    }

    public boolean delAccount(Account account) {
        BAccount bAccount = getAccount(account);
        return accounts.remove(bAccount);
    }


    public Map<String, Integer> getVisibility(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount == null)
            return new HashMap<>();
        return bAccount.visibility;
    }

    public Map<String, String> getAccountUserData(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount == null)
            return new HashMap<>();
        return bAccount.accountUserData;
    }

    public Map<String, String> getAuthToken(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount == null)
            return new HashMap<>();
        return bAccount.authTokens;
    }

    public Account[] getAccountsByType(String type) {
        List<Account> local = new ArrayList<>();
        for (BAccount account : accounts) {
            if (account.account.type.equals(type)) {
                local.add(account.account);
            }
        }
        return local.toArray(new Account[]{});
    }

    public void updateLastAuthenticatedTime(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount != null) {
            bAccount.updateLastAuthenticatedTime = System.currentTimeMillis();
        }
    }

    public long findAccountLastAuthenticatedTime(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount != null) {
            return bAccount.updateLastAuthenticatedTime;
        }
        return -1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeTypedList(this.accounts);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readInt();
        this.accounts = source.createTypedArrayList(BAccount.CREATOR);
    }

    public BUserAccounts() {
    }

    protected BUserAccounts(Parcel in) {
        this.userId = in.readInt();
        this.accounts = in.createTypedArrayList(BAccount.CREATOR);
    }

    public static final Creator<BUserAccounts> CREATOR = new Creator<BUserAccounts>() {
        @Override
        public BUserAccounts createFromParcel(Parcel source) {
            return new BUserAccounts(source);
        }

        @Override
        public BUserAccounts[] newArray(int size) {
            return new BUserAccounts[size];
        }
    };
}
