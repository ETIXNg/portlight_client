package globals;

import com.sirachlabs.portchlyt_services.app;

import models.mClient;

public class globals {


    public static String rave_flutter_wave_split_ratio = "0.15";

    //test
    //public static String rave_flutter_wave_public_key = "FLWPUBK-9d6d10c05d2fc18a035bd89738282539-X";
    //public static String rave_flutter_wave_encryption_key = "08f060ffbbecd3c6642cb789";


    //live
    public static String rave_flutter_wave_public_key="FLWPUBK-51608cec6efafcfea89de8a19ce9ae14-X";
    public static String rave_flutter_wave_encryption_key="8affddf7e942c03b8c6ba499";


    //online settings
    final static String mqtt_server = "tcp://etixng.com:1883";
    public static String base_url="http://etixng.com:1000/apiService";

    //offline settingsd
//    final static String mqtt_server = "tcp://192.168.4.1:1883";
//    public static String base_url = "http://192.168.4.1:1111/apiService";


    //get currency
    public static String formatCurrency(double amount) {
        String amount_currency = "";

        if (amount < 1000) {
            amount_currency = "₦" + String.format("%.2f", amount);
        } else {
            int exp = (int) (Math.log(amount) / Math.log(1000));
            amount_currency = String.format("%.2f %c", amount / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
        }

        return amount_currency;
    }

    //get currency
    public static String formatCurrencyPlain(double amount) {
        String amount_currency = "";
        amount_currency = "₦" + String.format("%.2f", amount);
        return amount_currency;
    }

    //chekc if this client is enabled or disabled
    public static boolean is_client_enabled() {
        mClient client = app.db.mClientDao().get_client();
        return client.enabled;

    }

    public static String numberCalculation(long number) {
        if (number < 1000)
            return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f %c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

}
