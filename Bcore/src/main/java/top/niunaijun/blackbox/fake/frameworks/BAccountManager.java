package top.niunaijun.blackbox.fake.frameworks;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.accounts.IAccountManagerResponse;
import android.os.Bundle;
import android.os.RemoteException;

import java.util.Map;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.accounts.IBAccountManagerService;

/**
 * Created by BlackBox on 2022/3/3.
 */
public class BAccountManager extends BlackManager<IBAccountManagerService> {
    private static final BAccountManager sBAccountManager = new BAccountManager();

    public static BAccountManager get() {
        return sBAccountManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.ACCOUNT_MANAGER;
    }

    public String getPassword(Account account) {
        try {
            return getService().getPassword(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getUserData(Account account, String key) {
        try {
            return getService().getUserData(account, key, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AuthenticatorDescription[] getAuthenticatorTypes() {
        try {
            return getService().getAuthenticatorTypes(BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account[] getAccountsForPackage(String packageName, int uid) {
        try {
            return getService().getAccountsForPackage(packageName, uid, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account[] getAccountsByTypeForPackage(String type, String packageName) {
        try {
            return getService().getAccountsByTypeForPackage(type, packageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account[] getAccountsAsUser(String type) {
        try {
            return getService().getAccountsAsUser(type, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getAccountByTypeAndFeatures(IAccountManagerResponse response, String accountType,
                                            String[] features) {
        try {
            getService().getAccountByTypeAndFeatures(response, accountType, features, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    public void getAccountsByFeatures(IAccountManagerResponse response, String accountType,
                               String[] features) {
        try {
            getService().getAccountsByFeatures(response, accountType, features, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean addAccountExplicitly(Account account, String password, Bundle extras) {
        try {
            return getService().addAccountExplicitly(account, password, extras, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeAccountAsUser(IAccountManagerResponse response, Account account,
                             boolean expectActivityLaunch) {
        try {
            getService().removeAccountAsUser(response, account, expectActivityLaunch, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean removeAccountExplicitly(Account account) {
        try {
            return getService().removeAccountExplicitly(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void copyAccountToUser(IAccountManagerResponse response, Account account,
                           int userFrom, int userTo) {
        try {
            getService().copyAccountToUser(response, account, userFrom, userTo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void invalidateAuthToken(String accountType, String authToken) {
        try {
            getService().invalidateAuthToken(accountType, authToken, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String peekAuthToken(Account account, String authTokenType) {
        try {
            return getService().peekAuthToken(account, authTokenType, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAuthToken(Account account, String authTokenType, String authToken) {
        try {
            getService().setAuthToken(account, authTokenType, authToken, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void setPassword(Account account, String password) {
        try {
            getService().setPassword(account, password, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void clearPassword(Account account) {
        try {
            getService().clearPassword(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUserData(Account account, String key, String value) {
        try {
            getService().setUserData(account, key, value, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) {
        try {
            getService().updateAppPermission(account, authTokenType, uid, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAuthToken(IAccountManagerResponse response, Account account,
                      String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch,
                      Bundle options) {
        try {
            getService().getAuthToken(response, account, authTokenType, notifyOnAuthFailure, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(IAccountManagerResponse response, String accountType,
                    String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch,
                    Bundle options) {
        try {
            getService().addAccount(response, accountType, authTokenType, requiredFeatures, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addAccountAsUser(IAccountManagerResponse response, String accountType,
                          String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch,
                          Bundle options) {
        try {
            getService().addAccountAsUser(response, accountType, authTokenType, requiredFeatures, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateCredentials(IAccountManagerResponse response, Account account,
                           String authTokenType, boolean expectActivityLaunch, Bundle options) {
        try {
            getService().updateCredentials(response, account, authTokenType, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void editProperties(IAccountManagerResponse response, String accountType,
                        boolean expectActivityLaunch) {
        try {
            getService().editProperties(response, accountType, expectActivityLaunch, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void confirmCredentialsAsUser(IAccountManagerResponse response, Account account,
                                  Bundle options, boolean expectActivityLaunch) {
        try {
            getService().confirmCredentialsAsUser(response, account, options, expectActivityLaunch, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean accountAuthenticated(Account account) {
        try {
            return getService().accountAuthenticated(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getAuthTokenLabel(IAccountManagerResponse response, String accountType,
                           String authTokenType) {
        try {
            getService().getAuthTokenLabel(response, accountType, authTokenType, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* Returns Map<String, Integer> from package name to visibility with all values stored for given account */
    public Map getPackagesAndVisibilityForAccount(Account account) {
        try {
            return getService().getPackagesAndVisibilityForAccount(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addAccountExplicitlyWithVisibility(Account account, String password, Bundle extras,
                                               Map visibility) {
        try {
            return getService().addAccountExplicitlyWithVisibility(account, password, extras, visibility, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setAccountVisibility(Account account, String packageName, int newVisibility) {
        try {
            return getService().setAccountVisibility(account, packageName, newVisibility, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAccountVisibility(Account account, String packageName) {
        try {
            return getService().getAccountVisibility(account, packageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // AccountManager.VISIBILITY_NOT_VISIBLE
        return 3;
    }

    /* Type may be null returns Map <Account, Integer>*/
    public Map getAccountsAndVisibilityForPackage(String packageName, String accountType) {
        try {
            return getService().getAccountsAndVisibilityForPackage(packageName, accountType, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerAccountListener(String[] accountTypes, String opPackageName) {
        try {
            getService().registerAccountListener(accountTypes, opPackageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterAccountListener(String[] accountTypes, String opPackageName) {
        try {
            getService().unregisterAccountListener(accountTypes, opPackageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
