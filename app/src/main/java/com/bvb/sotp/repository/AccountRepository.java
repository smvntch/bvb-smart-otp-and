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
import com.centagate.module.device.PinAuthentication;
import com.centagate.module.exception.CentagateException;
import com.centagate.module.log.Logger;
import com.centagate.module.otp.OtpInfo;
import com.centagate.module.otp.OtpService;

import java.util.ArrayList;
import java.util.List;

import com.bvb.sotp.Constant;
import com.bvb.sotp.helper.PreferenceHelper;
import com.bvb.sotp.util.LogUtils;

import io.realm.Realm;

/**
 * Created by MY-COM-0089 on 26/07/2018.
 */


public class AccountRepository {

    private static AccountRepository single_instance = null;
    private DeviceAuthentication authentication;
    private static AccountService accountService = null;
    private static DeviceService deviceService = null;
    private static OtpService otpService = null;
    private MutableLiveData<List<Account>> accountsData = new MutableLiveData<>();
    private MutableLiveData<DeviceInfo> deviceInfoData = new MutableLiveData<>();
    private PreferenceHelper preferenceHelper;
    private List<Account> accounts;
    private DeviceInfo deviceInfo;
    private static FileSystem fileSystem;
    private Context context;

    public CompleteEntity getCompleteEntity() {
        return completeEntity;
    }

    CompleteEntity completeEntity = null;

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

        this.context = context;
        try {
            completeEntity = fileSystem.getAccountsFromFile(Constant.FILENAME, preferenceHelper.getHid(), context);

            if (completeEntity.getAccounts() != null) {
//                accounts = getFakeAccount();
                accounts = completeEntity.getAccounts();
            } else {
                accounts = new ArrayList<Account>();
            }
            if (completeEntity.getDeviceInfo() != null) {
                deviceInfo = completeEntity.getDeviceInfo();
            } else {
                deviceInfo = new DeviceInfo();
            }

            accountsData.postValue(accounts);
            deviceInfoData.postValue(deviceInfo);

        } catch (Exception e) {
            accounts = new ArrayList<Account>();
//            accounts = getFakeAccount();

            accountsData.postValue(accounts);

            deviceInfo = new DeviceInfo();
            deviceInfoData.postValue(deviceInfo);
        }
        if (completeEntity == null) {
            this.authentication = null;
        } else {
            this.authentication = completeEntity.getDeviceAuthentication();
        }

    }

    public DeviceAuthentication getAuthentication() {
        return authentication;
    }

    public List<Account> getFakeAccount() {
        List<Account> temp = new ArrayList<>();

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


        temp.add(account1);
        temp.add(account2);
        temp.add(account3);
        temp.add(account4);
        temp.add(account5);
        return temp;
    }

    public Account findAccount(String accountId) {
        Account accountFound = null;
        for (Account account : accounts) {
            if (account.getAccountInfo().getId().equals(accountId)) {
                accountFound = account;
            }
        }
        return accountFound;
    }

    public List<Account> getOnlineAccounts() {
        List<Account> data = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            Account temp = accounts.get(i);
            if (!temp.getAccountInfo().isOffline()) {
                data.add(temp);
            }
        }
//        accounts.forEach();
        return data;
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
            if (account.getAccountInfo().getUsername().toLowerCase().equals(username.toLowerCase())) {
                accountFound = account;
            }
        }
        return accountFound;
    }

    public void deleteAllAccount(final DeviceAuthentication deviceAuthentication, CommonListener listener) {
        try {
            fileSystem.saveAccountsToFile(new ArrayList<Account>(), new DeviceInfo(), Configuration.getInstance(), deviceAuthentication, Constant.FILENAME, preferenceHelper.getHid(), context);
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

    public void deleteAccount(int index, final DeviceAuthentication deviceAuthentication) {
        accounts.remove(index);
        try {
            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), deviceAuthentication, Constant.FILENAME, preferenceHelper.getHid(), context);
            accountsData.postValue(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteAccount(String accountId, final DeviceAuthentication deviceAuthentication) {

        for (int i = 0; i < getAccounts().getValue().size(); i++) {
            if (getAccounts().getValue().get(i).getAccountInfo().getAccountId().equals(accountId)) {
                accounts.remove(i);
                i = getAccounts().getValue().size();
            }
        }
        try {
            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), deviceAuthentication, Constant.FILENAME, preferenceHelper.getHid(), context);
            accountsData.postValue(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void addAccountToList(Account newAccount) {
        int i = 0;
        Boolean isDuplicate = false;
        for (Account accountExist : accounts) {
            if (newAccount.getAccountInfo().getUsername().equals(accountExist.getAccountInfo().getUsername())) {
                accounts.set(i, newAccount);
                isDuplicate = true;
            }
            i++;
        }
        if (!isDuplicate) {
            accounts.add(newAccount);
        }
    }

    public void addAccount(Account account, DeviceAuthentication authentication, CommonListener commonListener, DeviceInfo deviceInfo) {

        this.deviceInfo = deviceInfo;
        this.deviceInfoData.postValue(deviceInfo);
        this.addAccount(account, authentication, commonListener);
    }

    public void addAccount(Account account, DeviceAuthentication authentication, CommonListener commonListener) {
        accounts.add(account);
        accountsData.postValue(accounts);
        try {
            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), authentication, Constant.FILENAME, preferenceHelper.getHid(), this.context);
            commonListener.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            commonListener.onError(ErrorDetails.ERR_UNABLE_GET_OTP_INFO);
        }
    }


    public void savePin(DeviceAuthentication authentication) {
        try {
            fileSystem.saveAccountsToFile(accounts, deviceInfo, Configuration.getInstance(), authentication, Constant.FILENAME, preferenceHelper.getHid(), this.context);
            this.authentication = authentication;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetData() {
        try {
            deleteAllAccount(authentication, new CommonListener() {
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
            this.authentication = null;
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

    public static AccountRepository getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new AccountRepository(context);
        }
        if (single_instance.context != null) {
            single_instance.context = null;
            single_instance.context = context;
        }

        return single_instance;
    }

    public MutableLiveData<List<Account>> getAccounts() {
        return accountsData;
    }

    public MutableLiveData<DeviceInfo> getDeviceInfoData() {
        return deviceInfoData;
    }

    public void setTokenId(String tokenId) {
        this.deviceInfo.setRegistrationId(tokenId);
    }

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
            this.authentication = newAuthentication;
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


}
