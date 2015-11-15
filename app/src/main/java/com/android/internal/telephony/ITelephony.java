package com.android.internal.telephony;

/**
 * Created by moo on 14/11/15.
 */
public interface ITelephony {

    boolean endCall();
    void answerRingingCall();
    void silenceRinger();

}
