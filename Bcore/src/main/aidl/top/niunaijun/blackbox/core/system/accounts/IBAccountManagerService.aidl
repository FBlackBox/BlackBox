package top.niunaijun.blackbox.core.system.accounts;

import android.accounts.IAccountManagerResponse;
import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.UserHandle;

import java.util.Map;


interface IBAccountManagerService {
    String getPassword(in Account account, int userId);
    String getUserData(in Account account, String key, int userId);
    AuthenticatorDescription[] getAuthenticatorTypes(int userId);
    Account[] getAccountsForPackage(String packageName, int uid, int userId);
    Account[] getAccountsByTypeForPackage(String type, String packageName, int userId);
    Account[] getAccountsAsUser(String accountType, int userId);
    void getAccountByTypeAndFeatures(in IAccountManagerResponse response, String accountType,
            in String[] features, int userId);
    void getAccountsByFeatures(in IAccountManagerResponse response, String accountType,
        in String[] features, int userId);
    boolean addAccountExplicitly(in Account account, String password, in Bundle extras, int userId);
    void removeAccountAsUser(in IAccountManagerResponse response, in Account account,
        boolean expectActivityLaunch, int userId);
    boolean removeAccountExplicitly(in Account account, int userId);
    void copyAccountToUser(in IAccountManagerResponse response, in Account account,
        int userFrom, int userTo);
    void invalidateAuthToken(String accountType, String authToken, int userId);
    String peekAuthToken(in Account account, String authTokenType, int userId);
    void setAuthToken(in Account account, String authTokenType, String authToken, int userId);
    void setPassword(in Account account, String password, int userId);
    void clearPassword(in Account account, int userId);
    void setUserData(in Account account, String key, String value, int userId);
    void updateAppPermission(in Account account, String authTokenType, int uid, boolean value);

    void getAuthToken(in IAccountManagerResponse response, in Account account,
        String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch,
        in Bundle options, int userId);
    void addAccount(in IAccountManagerResponse response, String accountType,
            String authTokenType, in String[] requiredFeatures, boolean expectActivityLaunch,
            in Bundle options, int userId);
    void addAccountAsUser(in IAccountManagerResponse response, String accountType,
        String authTokenType, in String[] requiredFeatures, boolean expectActivityLaunch,
        in Bundle options, int userId);
    void updateCredentials(in IAccountManagerResponse response, in Account account,
        String authTokenType, boolean expectActivityLaunch, in Bundle options, int userId);
    void editProperties(in IAccountManagerResponse response, String accountType,
        boolean expectActivityLaunch, int userId);
    void confirmCredentialsAsUser(in IAccountManagerResponse response, in Account account,
        in Bundle options, boolean expectActivityLaunch, int userId);
    boolean accountAuthenticated(in Account account, int userId);
    void getAuthTokenLabel(in IAccountManagerResponse response, String accountType,
        String authTokenType, int userId);

    /* Returns Map<String, Integer> from package name to visibility with all values stored for given account */
    Map getPackagesAndVisibilityForAccount(in Account account, int userId);
    boolean addAccountExplicitlyWithVisibility(in Account account, String password, in Bundle extras,
            in Map visibility, int userId);
    boolean setAccountVisibility(in Account a, in String packageName, int newVisibility, int userId);
    int getAccountVisibility(in Account a, in String packageName, int userId);
    /* Type may be null returns Map <Account, Integer>*/
    Map getAccountsAndVisibilityForPackage(in String packageName, in String accountType, int userId);

    void registerAccountListener(in String[] accountTypes, String opPackageName, int userId);
    void unregisterAccountListener(in String[] accountTypes, String opPackageName, int userId);

    /* Check if the package in a user can access an account */
//    boolean hasAccountAccess(in Account account, String packageName, in UserHandle userHandle);
    /* Crate an intent to request account access for package and a given user id */
//    IntentSender createRequestAccountAccessIntentSenderAsUser(in Account account,
//        String packageName, in UserHandle userHandle);

//    void onAccountAccessed(String token);
}
