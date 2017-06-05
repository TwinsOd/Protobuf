package com.example.twins.testkeepsolid.adapter;


import android.text.Html;
import android.text.Spanned;

class HtmlUtils {
    private HtmlUtils() {
        //empty
    }

    static Spanned boldFirstWord(String strFirst, String strSecond) {
        if (strSecond == null) return fromHtml("");
        return fromHtml("<b>" + strFirst + "</b>" + " " + strSecond);
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
