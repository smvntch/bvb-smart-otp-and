package com.bvb.sotp.repository;

/**
 * Created by MY-COM-0089 on 27/07/2018.
 */

public interface CommonListener {
    void onSuccess();
    void onError(final Integer code);
}
