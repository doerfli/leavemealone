package li.doerf.leavemealone.telephony;

/**
 * Created by moo on 22/11/15.
 */
public class CallHangupFactory {
//    private final String LOGTAG = getClass().getSimpleName();

    /**
     * Instantiate an {@link ICallHangup}.
     *
     * @return
     */
    public ICallHangup get() {
        return new TelephonyServiceCallHangup();
    }
}
