package com.nexah.utils;

public class StatusUtils {

    public static String statusConvertToSMPP(String status){
        switch (status) {
            case Constant.DELIV_TO_TERMINAL:
                return Constant.DELIVRD;
            case Constant.DELIV_TO_NETWORK:
                return Constant.UNDELIV;
            case Constant.DELIV_UNCERTAIN:
                return Constant.UNDELIV;
            case Constant.DELIV_IMPOSSIBLE:
                return Constant.UNDELIV;
            default:
                return Constant.UNDELIV;
        }
    }

}
