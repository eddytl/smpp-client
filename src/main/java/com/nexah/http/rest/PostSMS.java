package com.nexah.http.rest;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppSession;
import com.nexah.http.requests.DLRreq;
import com.nexah.http.responses.DLRresp;
import com.nexah.http.responses.SMSResponse;
import com.nexah.models.Message;
import com.nexah.models.Setting;
import com.nexah.services.SmppSMSService;
import com.nexah.smpp.SmsStatus;
import com.nexah.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;


public class PostSMS {

    private static final Logger log = LoggerFactory.getLogger(PostSMS.class);
    protected static RestTemplate restTemplate = new RestTemplate();

    public static SMSResponse sendsms(SmppSMSService smppSMSService, SmppSession session, Message message, Setting setting) {
        try {

            byte[] textBytes = CharsetUtil.encode(message.getMessage(), CharsetUtil.CHARSET_ISO_8859_1);
            SmsStatus smsStatus = smppSMSService.sendTextMessage(session, textBytes, message, setting);
            if (smsStatus.isSent()) {
                return new SMSResponse(Constant.SMS_SENT, Constant.SMS_MSG_SENT, smsStatus.getMessageId());
            } else {
                return new SMSResponse(Constant.SMS_ERROR, smsStatus.getMessageId(), smsStatus.getMessageId());
            }

        } catch (Exception e) {
            return new SMSResponse(Constant.SMS_ERROR, e.toString(), message.getId());
        }
    }

    public static DLRresp sendDLR(Message message) {
        try {
            DLRreq dlRreq = new DLRreq();
            dlRreq.setRequestId(message.getId());
            dlRreq.setDeliveryStatus(message.getStatus());
            dlRreq.setMobileno(message.getMsisdn());
            dlRreq.setProvider("OCM Local");
            dlRreq.setIsSmpp(1);
            dlRreq.setSubmitDate(message.getSubmitedAt());
            dlRreq.setDeliverytime(message.getDeliveredAt());
            return restTemplate.postForObject(message.getDlrUrl(), dlRreq, DLRresp.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
