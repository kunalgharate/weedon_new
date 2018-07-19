package info.mores.weedon.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by krishna handge on 10/2/16.
 */
public class ConnectionDetector {
    private static ConnectionDetector connectionDetector = null;
    private Context context;

    private ConnectionDetector(Context context) {
        this.context = context;
    }

    public static ConnectionDetector getInstance(Context context) {
        if (connectionDetector == null) {
            connectionDetector = new ConnectionDetector(context);
        }
        return connectionDetector;
    }




    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
//            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();
//            if (networkInfos != null) {
//                for (NetworkInfo anInfo : networkInfos) {
//                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
//                        return true;
//                    }
//                }
//            }

            if (networkInfos != null && networkInfos.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

}
