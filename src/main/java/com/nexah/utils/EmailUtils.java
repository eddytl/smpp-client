package com.nexah.utils;

public class EmailUtils {

    public static final String IP = "3.136.152.137";
    public static final String PORT = "2776";
    public static final String SMS_ERROR_SUBJECT = "[NEXAH SMS SMPP] SMS Error Alert";
    public static final String SERVICE_UNBIND_SUBJECT = "[NEXAH SMS SMPP] SMPP service Unbind Alert";
    public static final String SERVICE_BIND_SUBJECT = "[NEXAH SMS SMPP] SMPP service Bind Alert";
    public static final String RECHARGE_SUBJECT_EN = "[NEXAH SMS SMPP] Account recharge alert";
    public static final String LOGIN_FAIL_SUBJECT = "[NEXAH SMS SMPP] Echec de connexion";
    public static final String LOGIN_SUCCESS_SUBJECT = "[NEXAH SMS SMPP] Nouvelle connexion";
    public static final String LOGOUT_SUBJECT = "[NEXAH SMS SMPP] Déconnexion";
    //public static final String ACCOUNT_CREATE_SUBJECT = "[NEXAH SMS SMPP] Création de compte";
    public static final String ACCOUNT_CREATE_SUBJECT_EN = "[NEXAH SMS SMPP] Account creation alert";
    //public static final String ACCOUNT_DIABLED_SUBJECT = "[NEXAH SMS SMPP] Compte désactivé";
    public static final String ACCOUNT_STATUS_SUBJECT_EN = "[NEXAH SMS SMPP] Account status alert";

    //public static final String RECHARGE_BALANCE_MAIL = "Cher Client,\n\nVotre compte $TYPE a été $OPERATION de $CREDIT SMS. Votre nouveau solde est de $SOLDE SMS.\n\nMerci pour votre confiance !\n\nCordialement,\nNEXAH Team";
    public static final String RECHARGE_BALANCE_MAIL_EN = "Dear Customer,\n\nYour account $USER has been $OPERATION of $CREDIT SMS for $TYPE traffic. Your new balance is $SOLDE SMS.\n\nThank you for your trust !\n\nBest regards,\nNEXAH Team";
    //public static final String CREATE_ACCOUNT_MAIL = "Bonjour M/Mme $NAME,\n\nMerci de trouver ci-dessous les paramètres de votre compte SMPP :\n\nIP: $IP\nPort: $PORT\nUsername: $USERNAME\nPassword: $PASSWORD\n\nNous restons disponible pour toute assistance.\n\nCordialement,\nNEXAH Team\nit-support@nexah.net";
    public static final String CREATE_ACCOUNT_MAIL_EN = "Good day M/Mme $NAME,\n\nPlease find below your SMPP account credentials :\n\nIP: $IP\nPort: $PORT\nUsername: $USERNAME\nPassword: $PASSWORD\n\nWe remain available for any assistance.\n\nBest regards,\nNEXAH Team\nit-support@nexah.net";
    public static final String LOGIN_FAIL_MAIL = "Hello,\n\nL'utilisateur $USER de l'entreprise $COMPANY a essayé de se connecter sans succès en utilisant l'@IP : $IP.\nMessage de l'erreur : $ERROR\n\nCordialement,\nNEXAH Team\nit-support@nexah.net";
    public static final String SMS_ERROR_MAIL = "Hello,\n\nL'utilisateur $USER de l'entreprise $COMPANY a essayé d'envoyer un SMS sans succès en utilisant l'@IP $IP et comme senderAddress $SID.\nMessage de l'erreur : $ERROR\n\nCordialement,\nNEXAH Team\nit-support@nexah.net";
    public static final String SERVICE_UNBIND_MAIL = "Hello,\n\nLe service $SERVICE c'est déconnecté de façon inattendue.\nMerci de vérifier!\n\nCordialement,\nNEXAH Team\nit-support@nexah.net";
    public static final String SERVICE_BIND_MAIL = "Hello,\n\nLe service $SERVICE c'est reconnecté avec succès.\n\nCordialement,\nNEXAH Team\nit-support@nexah.net";
    public static final String LOGIN_SUCCESS_MAIL = "Hello,\n\nL'utilisateur $USER de l'entreprise $COMPANY vient d'établir une nouvelle connexion SMPP sur le Gateway avec l'@IP : $IP.\n\nCordialement,\nNEXAH Team\nit-support@nexah.net";
    public static final String LOGOUT_MAIL = "Hello,\n\nL'utilisateur $USER de l'entreprise $COMPANY vient de se déconnecter du Gateway avec l'@IP : $IP.\nStatistique de la session :\n\n $STATS\n\nCordialement,\nNEXAH Team\nit-support@nexah.net";
    public static final String ACCOUNT_STATUS_MAIL_EN = "Dear Customer,\n\nYour account has been $OPERATION. Please contact your Account Manager for more details.\n\nBest regards,\nNEXAH Team\\support@nexah.net\"";

    public static String recharge(String username, String credit, String solde, String operation, String type) {
        operation = operation.equals("CREDIT") ? "credited": "debited";
        return RECHARGE_BALANCE_MAIL_EN.replace("$CREDIT", credit).
                replace("$SOLDE", solde).
                replace("$USER", username).
                replace("$TYPE", type).
                replace("$OPERATION", operation);
    }

    public static String accountOperation(boolean status) {
        String operation = status ? "enabled": "disabled";
        return ACCOUNT_STATUS_MAIL_EN.replace("$OPERATION", operation);
    }

    public static String createAccount(String username, String password, String name) {
        return CREATE_ACCOUNT_MAIL_EN.replace("$NAME", name).
                replace("$IP", IP).
                replace("$PORT", PORT).
                replace("$USERNAME", username).
                replace("$PASSWORD", password);
    }


    public static String loginFail(String username, String company, String ip, String error) {
        return LOGIN_FAIL_MAIL.replace("$USER", username).
                replace("$COMPANY", company).
                replace("$IP", ip).
                replace("$ERROR", error);
    }

    public static String loginSuccess(String username, String company, String ip) {
        return LOGIN_SUCCESS_MAIL.replace("$USER", username).
                replace("$COMPANY", company).
                replace("$IP", ip);
    }

    public static String logout(String username, String company, String ip, String stats) {
        return LOGOUT_MAIL.replace("$USER", username).
                replace("$COMPANY", company).
                replace("$IP", ip).
                replace("$STATS", stats);
    }

    public static String sendSMSError(String username, String company, String ip, String sourceAddress, String error){
        return SMS_ERROR_MAIL.replace("$USER", username).
                replace("$COMPANY", company).
                replace("$SID", sourceAddress).
                replace("$IP", ip).
                replace("$ERROR", error);
    }

    public static String sendServiceUnbind(String service){
        return SERVICE_UNBIND_MAIL.replace("$SERVICE", service);
    }

    public static String sendServiceBind(String service){
        return SERVICE_BIND_MAIL.replace("$SERVICE", service);
    }
}
