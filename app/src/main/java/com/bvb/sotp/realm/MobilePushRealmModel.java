package com.bvb.sotp.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MobilePushRealmModel extends RealmObject {

    @PrimaryKey
    public long id;
    public String detail;
    public String tittle;
    public String content;
    public long date;
    public String type;//1:mobile push,2:transaction,3:mobile push invalid,4: invalid active code,5:invalid qr
    public String status;//1:success,2:denied,3:failed
}
