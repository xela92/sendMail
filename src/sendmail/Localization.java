/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sendmail;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author xela92
 */
public class Localization {

    private static ResourceBundle localeRes;
    private static String localeName;
    private static boolean hasSetNewLocale;

    private static ResourceBundle setLocaleRes(Locale currentLocale) {
        ResourceBundle lang;
        Locale.setDefault(currentLocale);

        try {
            lang = ResourceBundle.getBundle("sendMail.lang.LangBundle", Locale.getDefault());
            lang.getString("sender_info");
        } catch (MissingResourceException mre) {
            Utils.sendError("Missing resource, falling back...");
            Locale.setDefault(getDefaultLocale());
        }
        lang = ResourceBundle.getBundle("sendMail.lang.LangBundle", Locale.getDefault());

        //String welcome = lang.getString("firstui_first_title");
        //System.out.println(welcome);
        return lang;
    }

    public static Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }

    public static String getDefaultLocaleName() {
        return "en";
    }

    //public static String getDefaultColumnName(String toBeTranslated) {
    //return "";
    //}
    public static Locale getLocale() {
        return getLocaleRes().getLocale();
    }

    public static String getLocaleDisplayName() {
        return getLocale().getDisplayLanguage();

    }

    public static String getLocaleName() {
        return localeName != null ? localeName : "en";
    }

    public static ResourceBundle getLocaleRes() {
        return localeRes == null ? setLocaleRes(getDefaultLocale()) : localeRes;
    }

    public static boolean hasSetNewLocale() {
        return hasSetNewLocale;
    }

    public static void setLocaleSaveDone() {
        hasSetNewLocale = false;
    }

    public static void setLocale(String newLocale) {
        localeName = newLocale;
        localeRes = setLocaleRes(new Locale(newLocale));
        hasSetNewLocale = true;
    }

    public static void setLocale(Locale newLocale) {
        localeName = newLocale.getCountry().toLowerCase();
        localeRes = setLocaleRes(newLocale);
    }

    static Locale getLocaleObjectFromName(String locale) {
        switch (locale) {
            case "it":
                return Locale.ITALIAN;
            case "en":
                return Locale.ENGLISH;

        }
        return getDefaultLocale();
    }
}
