package com.nexah.http.rest;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.nexah.http.requests.DLRreq;
import com.nexah.http.responses.DLRresp;
import com.nexah.http.responses.SMSResponse;
import com.nexah.services.SmppSMSService;
import com.nexah.smpp.DeliveryReceipt;
import com.nexah.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class PostSMS {

    private static final Logger log = LoggerFactory.getLogger(PostSMS.class);
    protected static RestTemplate restTemplate = new RestTemplate();

    public static SMSResponse sendsms(SmppSMSService smppSMSService, ArrayList<SmppSession> sessions, String traffic, String sender, String mobileno, String message) throws SmppInvalidArgumentException {
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

    public static void sendDLR(String msisdn, DeliveryReceipt deliveryReceipt) {
        DLRreq dlRreq = new DLRreq();
        dlRreq.setRequestId(deliveryReceipt.getMessageId());
        dlRreq.setDeliveryStatus(DeliveryReceipt.toStateText(deliveryReceipt.getState()));
        dlRreq.setMobileno(msisdn);
        dlRreq.setProvider("OCM");
        dlRreq.setIsSmpp(1);
        dlRreq.setSubmitDate(deliveryReceipt.getSubmitDate());
        dlRreq.setDeliverytime(deliveryReceipt.getDoneDate());
        log.info("DLR send dlRreq {}", dlRreq);

        DLRresp dlRresp = restTemplate.postForObject("https://sms-broker.nexah.net/api/v1/nxh/dr", dlRreq, DLRresp.class);
        log.info("DLR send response {}", dlRresp);
    }
}
