package li.doerf.leavemealone.telephony;

import android.content.Context;

/**
 * This interace is used to impelement the different ways to hang up a call.
 *
 * Created by moo on 22/11/15.
 */
public interface ICallHangup {

    /**
     * Hang up the current call.
     *
     * @param aContext the context
     */
    public boolean hangup( Context aContext);

}
