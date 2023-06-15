package com.nexah.http.rest;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.nexah.http.responses.SMSResponse;
import com.nexah.services.SmppSMSService;
import com.nexah.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


public class PostSMS {

    private static final Logger log = LoggerFactory.getLogger(PostSMS.class);

    public static SMSResponse sendsms(SmppSMSService smppSMSService, ArrayList<SmppSession> sessions, String traffic, String sender, String mobileno, String message) throws SmppInvalidArgumentException {
        AtomicReference<String> alert = new AtomicReference<>("");
        try {
            String msgId = null;
            if (!sessions.isEmpty()) {
                for (SmppSession session : sessions) {
                    if (session.getConfiguration().getName().equals(traffic)) {
                        byte[] textBytes = CharsetUtil.encode(message, CharsetUtil.CHARSET_ISO_8859_1);
                        msgId = smppSMSService.sendTextMessage(session, sender, textBytes, mobileno);
                        log.info("MessageID = " + msgId);
                    }
                }
                return new SMSResponse(Constant.SMS_SENT, Constant.SMS_MSG_SENT, msgId);
            } else {
                log.info(Constant.SERVER_NOT_BOUND);
                return new SMSResponse(Constant.SMS_ERROR, Constant.SERVER_NOT_BOUND, null);
            }

        } catch (Exception e) {
            log.error("Send SMS SMPP Error " + e.toString() + " Mobileno = " + mobileno);
            return new SMSResponse(Constant.SMS_ERROR, e.toString(), null);
        }
    }
}
