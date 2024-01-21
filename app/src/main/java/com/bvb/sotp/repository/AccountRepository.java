package com.bvb.sotp.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.centagate.module.account.Account;
import com.centagate.module.account.AccountInfo;
import com.centagate.module.account.AccountService;
import com.centagate.module.common.CompleteEntity;
import com.centagate.module.common.Configuration;
import com.centagate.module.common.ErrorDetails;
import com.centagate.module.device.DeviceAuthentication;
import com.centagate.module.device.DeviceInfo;
import com.centagate.module.device.DeviceService;
import com.centagate.module.device.FileSystem;
import com.centagate.module.device.KeyOperation;
import com.centagate.module.exception.CentagateException;
import com.centagate.module.log.Logger;
import com.centagate.module.otp.OtpInfo;
import com.centagate.module.otp.OtpService;

import java.util.ArrayList;
import java.util.List;

import com.bvb.sotp.Constant;
import com.bvb.sotp.helper.PreferenceHelper;

import io.realm.Realm;

// TODO : VuNA: Convert this class to Kotlin

/**
 * This class is used to manage the accounts of the user.
 * It follows the Singleton Design Pattern, ensuring that only one instance of this class is created.
 * It provides methods for account management such as adding, deleting, and finding accounts.
 * It also provides methods for OTP (One-Time Password) management such as syncing OTP and getting OTP status.
 * It uses the FileSystem class to save and retrieve account data from a file.
 * It uses the PreferenceHelper class to manage shared preferences.
 * It uses the AccountService, DeviceService, and OtpService classes to perform various operations related to accounts, devices, and OTPs.
 * It uses the MutableLiveData class to hold and observe data of accounts and device info.
 *
 * @author MY-COM-0089
 * @version 2018-07-26
 */
public class AccountRepository {

    // Fields

    // Singleton instance of AccountRepository, initially null
    private static AccountRepository singleton_instance = null;
    private Context context;
    private List<Account> accounts;
    /**
     * LiveData is an observable data holder class that is lifecycle-aware,
     * meaning it respects the lifecycle of other app components,
     * such as activities, fragments, or services.
     * This awareness ensures LiveData only updates app component observers
     * that are in an active lifecycle state.
     * MutableLiveData is a subclass of LiveData that exposes
     * the setValue(T) and postValue(T) methods publicly
     * and you can use these methods to change the value stored in the LiveData object.
     * In this case, accountsData is holding a list of Account objects.
     * The Account class is a custom class defined in your application.
     * The MutableLiveData object will notify its active observers when the data has been changed.
     * The accountsData object is declared as private and final.
     * private means that this variable can only be accessed within the AccountRepository class.
     * final means that once a value is assigned to accountsData,it cannot be changed.
     * In this case, it's assigned a new instance of MutableLiveData and this instance
     * cannot be replaced with another one, although the data inside it can be changed
     * In summary, while accounts is used for direct manipulation of the account data,
     * accountsData is used to observe changes in the account data in a lifecycle-aware manner.
     * This makes accountsData particularly useful for updating the UI in response to changes
     * in the account data.
     */
    private final MutableLiveData<List<Account>> accountsData = new MutableLiveData<>();
    private static AccountService accountService = null;
    private DeviceInfo deviceInfo;
    private final MutableLiveData<DeviceInfo> deviceInfoData = new MutableLiveData<>();
    private static DeviceService deviceService = null;
    private DeviceAuthentication deviceAuthentication;
    private static FileSystem fileSystem;
    private PreferenceHelper preferenceHelper;
    private static OtpService otpService = null;

    CompleteEntity completeEntity = null;

    // Getters and Setters
    public CompleteEntity getCompleteEntity() {
        return completeEntity;
    }

    public DeviceAuthentication getDeviceAuthentication() {
        return deviceAuthentication;
    }

    public MutableLiveData<List<Account>> getAccountsData() {
        return accountsData;
    }

    public MutableLiveData<DeviceInfo> getDeviceInfoData() {
        return deviceInfoData;
    }

    public void setTokenId(String tokenId) {
        this.deviceInfo.setRegistrationId(tokenId);
    }

    // Constructors
    public AccountRepository(Context context) {
        if (accountService == null) {
            accountService = new AccountService();
        }
        if (deviceService == null) {
            deviceService = new DeviceService();
        }
        if (fileSystem == null) {
            fileSystem = new FileSystem();
        }
        if (otpService == null) {
            otpService = new OtpService();
        }
        if (preferenceHelper == null) {
            preferenceHelper = new PreferenceHelper(context);
        }

        // Todo: VuNA: inspect this for account files
        this.context = context;
        try {
            completeEntity = fileSystem.getAccountsFromFile(
                    Constant.FILENAME, preferenceHelper.getHid(), context);

            if (completeEntity.getAccounts() != null) {
//                accounts = getFakeAccount();
                accounts = completeEntity.getAccounts();
            } else {
                accounts = new ArrayList<>();
            }
            if (completeEntity.getDeviceInfo() != null) {
                deviceInfo = completeEntity.getDeviceInfo();
            } else {
                deviceInfo = new DeviceInfo();
            }

            accountsData.postValue(accounts);
            deviceInfoData.postValue(deviceInfo);

        } catch (Exception e) {
            accounts = new ArrayList<>();
//            accounts = getFakeAccount();

            accountsData.postValue(accounts);

            deviceInfo = new DeviceInfo();
            deviceInfoData.postValue(deviceInfo);
        }
        if (completeEntity == null) {
            this.deviceAuthentication = null;
        } else {
            this.deviceAuthentication = completeEntity.getDeviceAuthentication();
        }

    }

    /**
     * This method is used to get an instance of the AccountRepository class.
     * It follows the Singleton Design Pattern,
     * which ensures that only one instance of the class is created.
     *  - If an instance already exists, it returns that instance.
     *  - If not, it creates a new instance.
     *
     * @param context The context of the caller. This is used for various operations within the AccountRepository class.
     * @return An instance of the AccountRepository class.
     */
    public static AccountRepository getInstance(Context context) {
        if (singleton_instance == null) {
            singleton_instance = new AccountRepository(context);
        }
        if (singleton_instance.context != null) {
            singleton_instance.context = context;
        }

        return singleton_instance;
    }


    // TODO: VuNA: I'm pretty sure getId() and getAccountId() are the same thing
    public Account findAccount(String accountId) {
        Account accountFound = null;
        for (Account account : accounts) {
            if (account.getAccountInfo().getId().equals(accountId)) {
                accountFound = account;
            }
        }
        return accountFound;
    }

    public Account findAccountById(String accountId) {
        Account accountFound = null;
        for (Account account : accounts) {
            if (account.getAccountInfo().getAccountId().equals(accountId)) {
                accountFound = account;
            }
        }
        return accountFound;
    }

    public Account findAccountByName(String username) {
        Account accountFound = null;
        for (Account account : accounts) {
            if (account.getAccountInfo().getUsername().equalsIgnoreCase(username)) {
                accountFound = account;
            }
        }
        return accountFound;
    }

//    public List<Account> getOnlineAccounts() {
//        List<Account> data = new ArrayList<>();
//        for (int i = 0; i < accountList.size(); i++) {
//            Account temp = accountList.get(i);
//            if (!temp.getAccountInfo().isOffline()) {
//                data.add(temp);
//            }
//        }
//        return data;
//    }

    /**
     * @return A list of accounts where {@code isOffline} is {@code false}
     * @since 2024-01-21
     */
    public List<Account> getOnlineAccounts() {
        List<Account> data = new ArrayList<>();
        for (Account account : accounts) {
            if (!account.getAccountInfo().isOffline()) {
                data.add(account);
            }
        }
        return data;
    }

    /**
     * This method ensures that the accounts list does not contain duplicate accounts.
     * If a duplicate account is added, it replaces the existing account in the list with @newAccount.
     * If a new account is added, it is appended to the end of the list.
     * @param newAccount The account to be added or replaced in the list.
     */
    private void addAccountToList(Account newAccount) {
        int i = 0;
        boolean isDuplicate = false;
        for (Account existingAccount : accounts) {
            if (newAccount.getAccountInfo().getUsername().equals(existingAccount.getAccountInfo().getUsername())) {
                accounts.set(i, newAccount);
                isDuplicate = true;
            }
            i++;
        }
        if (!isDuplicate) {
            accounts.add(newAccount);
        }
    }


    // TODO: VuNA: Seems like this is a early version of addAccount, and might have problem with missing deviceInfo. Also it doesn't check for duplicate account
    public void addAccount(Account account, DeviceAuthentication authentication, CommonListener commonListener) {
        accounts.add(account);
        accountsData.postValue(accounts);
        try {
            fileSystem.saveAccountsToFile(
                    accounts,
                    deviceInfo,
                    Configuration.getInstance(),
                    authentication,
                    Constant.FILENAME,
                    preferenceHelper.getHid(),
                    this.context);
            commonListener.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            commonListener.onError(ErrorDetails.ERR_UNABLE_GET_OTP_INFO);
        }
    }

    public void addAccount(Account account, DeviceAuthentication authentication, CommonListener commonListener, DeviceInfo deviceInfo) {

        this.deviceInfo = deviceInfo;
        this.deviceInfoData.postValue(deviceInfo);
        this.addAccount(account, authentication, commonListener);
    }


    public void deleteAccount(int index, final DeviceAuthentication deviceAuthentication) {
        accounts.remove(index);
        try {
            fileSystem.saveAccountsToFile(
                    accounts,
                    deviceInfo,
                    Configuration.getInstance(),
                    deviceAuthentication,
                    Constant.FILENAME,
                    preferenceHelper.getHid(),
                    context);
            accountsData.postValue(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteAccount(String accountId, final DeviceAuthentication deviceAuthentication) {

        /*
          The confusion arises from the fact that the code is iterating over
          getAccountsData().getValue(), but deleting from accounts.
          This might seem strange, but it's important to note that
          getAccountsData().getValue() and accounts are actually the same list.
          getAccountsData().getValue() is a method that returns the accounts list.
          So, when an account is removed from accounts,
          it's also removed from the list returned by getAccountsData().getValue(), and vice versa.
         */
        accounts.removeIf(account -> account.getAccountInfo().getAccountId().equals(accountId));
        for (int i = 0; i < getAccountsData().getValue().size(); i++) {
            if (getAccountsData().getValue().get(i).getAccountInfo().getAccountId().equals(accountId)) {
                accounts.remove(i);
                i = getAccountsData().getValue().size();
            }
        }
        // TODO: VuNA: this is a replacement for the above code
        // accounts.removeIf(account -> account.getAccountInfo().getAccountId().equals(accountId));

        try {
            fileSystem.saveAccountsToFile(
                    accounts,
                    deviceInfo,
                    Configuration.getInstance(),
                    deviceAuthentication,
                    Constant.FILENAME,
                    preferenceHelper.getHid(),
                    context);
            accountsData.postValue(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteAllAccounts(final DeviceAuthentication deviceAuthentication, CommonListener listener) {
        try {
            fileSystem.saveAccountsToFile(
                    new ArrayList<>(),
                    new DeviceInfo(),
                    Configuration.getInstance(),
                    deviceAuthentication,
                    Constant.FILENAME,
                    preferenceHelper.getHid(),
                    context);
            accounts.clear();
            accountsData.setValue(accounts);
            deviceInfo = new DeviceInfo();
            deviceInfoData.setValue(deviceInfo);
            listener.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError(Constant.ERROR_DATA_NULL);
        }
    }




    public void savePin(DeviceAuthentication authentication) {
        try {
            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), authentication, Constant.FILENAME, preferenceHelper.getHid(), this.context);
            this.deviceAuthentication = authentication;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetData() {
        try {
            deleteAllAccounts(deviceAuthentication, new CommonListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Integer code) {

                }
            });
            fileSystem.deleteAccountFile(Constant.FILENAME);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });
            this.deviceAuthentication = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData(DeviceAuthentication authentication, CommonListener listener) {

        try {
            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), authentication, Constant.FILENAME, preferenceHelper.getHid(), this.context);
            listener.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError(ErrorDetails.ERR_ENCRYPTION);
        }
    }

    // TODO: VuNA: Is there any leak here?
    @SuppressLint("StaticFieldLeak")
    public void syncOtp(final String serial, final Account account, final DeviceAuthentication authentication, final CommonListener listener) {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                switch (result) {
                    case 1:
                        listener.onSuccess();
                        break;
                    default:
                        listener.onError(result);
                        break;
                }
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    Boolean result = otpService.syncOtp(serial, true, account, authentication);
                    if (result) {
                        return 1;
                    }
                } catch (CentagateException e) {
                    e.printStackTrace();
                    return e.getErrorCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 123;

            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getOtpStatus(final String serial, final Account account, final DeviceAuthentication authentication, final CommonListener listener) {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
//                switch (result) {
//                    case 1:
//                        listener.onSuccess();
//                        break;
//                    default:
                listener.onError(result);
//                        break;
//                }
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    Integer result = otpService.getOtpStatus(serial, true, account.getAccountInfo(), account.getOtpInfo(), authentication);
                    System.out.println("------result---------" + result);
                    return result;
                } catch (CentagateException e) {
                    e.printStackTrace();
                    return e.getErrorCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 123;

            }
        }.execute();
    }

//    public void activateOtp(final String serial, final Account account, final DeviceAuthentication authentication, final CommonListener listener){
//        new AsyncTask<Void, Void, Integer>() {
//
//            @Override
//            protected void onPostExecute(Integer result) {
//                super.onPostExecute(result);
//                switch (result){
//                    case 1 : listener.onSuccess(); break;
//                    case ErrorDetails.ERR_SYNC_OTP_TOKEN : listener.onError(ErrorDetails.ERR_SYNC_OTP_TOKEN); break;
//                    default: listener.onError(result); break;
//                }
//            }
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try{
//                    Boolean result =  otpService.activateOtp(serial,account.getAccountInfo(),account.getOtpInfo(),authentication);
//                    if(result){
//                        return  1;
//                    }else{
//                        return ErrorDetails.ERR_ACTIVATE_OTP_TOKEN;
//                    }
//                }catch (CentagateException e){
//                    e.printStackTrace();
//                    return e.getErrorCode();
//                }
//
//            }
//        }.execute();
//    }



    //add by Jerry
//    public void updateAuthentication(DeviceAuthentication oldAuthentication, DeviceAuthentication newAuthentication) {
//        try {
//            ArrayList<Account> newAccounts = new ArrayList<Account>();
//            KeyOperation key = new KeyOperation();
//
//            for (Account account : accounts) {
//                Account newAccount = key.updateAccountKey(oldAuthentication, newAuthentication, account);
////                newAccounts.add(newAccount);
//            }
//
//            deviceInfo = key.updateDeviceKey(oldAuthentication, newAuthentication, deviceInfo);
//            deviceInfoData.setValue(deviceInfo);
//
//            accounts = newAccounts;
//            accountsData.setValue(accounts);
//
//            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), newAuthentication, Constant.FILENAME, context);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public void updateAuthentication(DeviceAuthentication oldAuthentication, DeviceAuthentication newAuthentication) {
        try {
            ArrayList<Account> newAccounts = new ArrayList<Account>();
            KeyOperation key = new KeyOperation();

            for (Account account : accounts) {
                Account newAccount = key.updateAccountKey(preferenceHelper.getHid(), oldAuthentication, newAuthentication, account);
                newAccounts.add(newAccount);
            }

            if (deviceInfo != null && deviceInfo.hasDeviceKey()) {
                deviceInfo = key.updateDeviceKey(preferenceHelper.getHid(), oldAuthentication, newAuthentication, deviceInfo);
                deviceInfoData.setValue(deviceInfo);
            }

            accounts = newAccounts;
            accountsData.setValue(accounts);
            this.deviceAuthentication = newAuthentication;
            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), newAuthentication, Constant.FILENAME, preferenceHelper.getHid(), context);

//            LogUtils.printLog("updateAuthentication success ", "");
            Logger.log(Logger.DEBUG, this.getClass(), "updateAuthentication success");

        } catch (Exception e) {

            StringBuilder temp = new StringBuilder();
            temp.append("error :" + e.getMessage() + "\n");
//            LogUtils.printLog("updateAuthentication failed ", temp.toString());
            Logger.log(Logger.DEBUG, this.getClass(), temp.toString());

            e.printStackTrace();
        }


    }

    /**
     * This method is used to generate a list of fake accounts for testing purposes.
     * Each account is created with a unique username, display name, and account ID.
     * The method returns a list of these fake accounts.
     *
     * @return A list of fake Account objects.
     */
    public List<Account> getFakeAccount() {

        AccountInfo accountInfo1 = new AccountInfo();
        accountInfo1.setCompanyName("SM");
        accountInfo1.setUsername("user 1");
        accountInfo1.setDisplayName("user 1");
        accountInfo1.setAccountId("1");
        OtpInfo otpInfo = new OtpInfo();
        Account account1 = new Account(accountInfo1, otpInfo);

        AccountInfo accountInfo2 = new AccountInfo();
        accountInfo2.setCompanyName("SM");
        accountInfo2.setUsername("user 2");
        accountInfo2.setDisplayName("user 2");
        accountInfo2.setAccountId("2");
        OtpInfo otpInfo2 = new OtpInfo();
        Account account2 = new Account(accountInfo2, otpInfo2);

        AccountInfo accountInfo3 = new AccountInfo();
        accountInfo3.setCompanyName("SM");
        accountInfo3.setUsername("user 3");
        accountInfo3.setDisplayName("user 3");
        accountInfo3.setAccountId("3");
        OtpInfo otpInfo3 = new OtpInfo();
        Account account3 = new Account(accountInfo3, otpInfo3);

        AccountInfo accountInfo4 = new AccountInfo();
        accountInfo4.setCompanyName("SM");
        accountInfo4.setUsername("user 4");
        accountInfo4.setDisplayName("user 4");
        accountInfo4.setAccountId("4");
        OtpInfo otpInfo4 = new OtpInfo();
        Account account4 = new Account(accountInfo4, otpInfo4);

        AccountInfo accountInfo5 = new AccountInfo();
        accountInfo5.setCompanyName("SM");
        accountInfo5.setUsername("user 5");
        accountInfo5.setDisplayName("user 5");
        accountInfo5.setAccountId("5");
        OtpInfo otpInfo5 = new OtpInfo();
        Account account5 = new Account(accountInfo5, otpInfo5);

        List<Account> temp = new ArrayList<>();
        temp.add(account1);
        temp.add(account2);
        temp.add(account3);
        temp.add(account4);
        temp.add(account5);
        return temp;
    }

}
