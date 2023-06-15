package com.nexah.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class StringUtils {

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public static String removeSpaces(String value){
        return value.replace("\t","").
                replace("\n","").
                replace("\r\n","").
                replace("\r","").
                replace(" ","");
    }

    public static String formatMsgId(String serviceName, String msgId){
        return serviceName.toLowerCase().replace(" ", "").concat(".").concat(msgId);
    }

}
