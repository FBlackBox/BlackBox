package top.niunaijun.blackbox.core.system.accounts;

import android.accounts.Account;

import java.util.Objects;

/**
 * Created by BlackBox on 2022/3/3.
 */
public class TokenCache {
    public int userId;
    public Account account;
    public long expiryEpochMillis;
    public String authToken;
    public String authTokenType;
    public String packageName;

    public TokenCache(int userId,Account account,
                      String callerPkg,
                      String tokenType,
                      String token,
                      long expiryMillis) {
        this.userId = userId;
        this.account = account;
        this.expiryEpochMillis = expiryMillis;
        this.authToken = token;
        this.authTokenType = tokenType;
        this.packageName = callerPkg;
    }

    public TokenCache(int userId, Account account, String authTokenType, String packageName) {
        this.userId = userId;
        this.account = account;
        this.authToken = authToken;
        this.authTokenType = authTokenType;
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenCache)) return false;
        TokenCache that = (TokenCache) o;
        return userId == that.userId &&
                expiryEpochMillis == that.expiryEpochMillis &&
                Objects.equals(account, that.account) &&
                Objects.equals(authToken, that.authToken) &&
                Objects.equals(authTokenType, that.authTokenType) &&
                Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, account, expiryEpochMillis, authToken, authTokenType, packageName);
    }
}
