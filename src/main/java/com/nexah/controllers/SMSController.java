package com.nexah.controllers;

import com.cloudhopper.smpp.SmppSession;
import com.nexah.http.requests.BulkSMSRequest;
import com.nexah.http.requests.SMS;
import com.nexah.http.requests.SMSRequest;
import com.nexah.http.responses.BulkSMSResponse;
import com.nexah.http.responses.SMSResponse;
import com.nexah.http.rest.PostSMS;
import com.nexah.models.Message;
import com.nexah.repositories.MessageRepository;
import com.nexah.services.SmppSMSService;
import com.nexah.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class SMSController {

    @Autowired
    SmppSession session;
    @Autowired
    SmppSMSService smppSMSService;
    @Value("${api.key}")
    String localApiKey;
    @Autowired
    MessageRepository messageRepository;

    @GetMapping(value = "/sendsms")
    public @ResponseBody
    SMSResponse sendsms(@RequestParam(name = "apiKey") String apiKey, @RequestParam(name = "traffic") String traffic,
                        @RequestParam(name = "mobileno") String mobileno, @RequestParam(name = "sender") String sender,
                        @RequestParam(name = "message") String message, @RequestParam(name = "dlrUrl") String dlrUrl) {
        if (apiKey.equals(localApiKey)) {
            Message msg = new Message();
            msg.setMsisdn(mobileno);
            msg.setSender(sender);
            msg.setMessage(message);
            msg.setTraffic(traffic);
            msg.setStatus(Constant.SMS_CREATED);
            msg.setDlrUrl(dlrUrl);
            msg.setDlrIsSent(false);
            msg.setCreatedAt(new Date());
            msg.setUpdatedAt(new Date());
            messageRepository.save(msg);

            return PostSMS.sendsms(smppSMSService, session, msg);
        } else {
            return new SMSResponse(Constant.SMS_ERROR, "Invalid ApiKey", null);
        }
    }

    @PostMapping(value = "/sendsms")
    public @ResponseBody
    SMSResponse sendsms(@RequestBody SMSRequest smsRequest) {
        String apiKey = smsRequest.getApiKey();
        if (apiKey.equals(localApiKey)) {
            String sender = smsRequest.getSender();
            String message = smsRequest.getMessage();
            String mobileno = smsRequest.getMobileno();
            String traffic = smsRequest.getTraffic();
            String dlrUrl = smsRequest.getDlrUrl();

            Message msg = new Message();
            msg.setMsisdn(mobileno);
            msg.setSender(sender);
            msg.setMessage(message);
            msg.setTraffic(traffic);
            msg.setStatus(Constant.SMS_CREATED);
            msg.setDlrUrl(dlrUrl);
            msg.setDlrIsSent(false);
            msg.setCreatedAt(new Date());
            msg.setUpdatedAt(new Date());
            messageRepository.save(msg);

            return PostSMS.sendsms(smppSMSService, session, msg);
        } else {
            return new SMSResponse(Constant.SMS_ERROR, "Invalid ApiKey", null);
        }

    }

    @PostMapping(value = "/sendbulksms")
    public @ResponseBody
    BulkSMSResponse sendbulksms(@RequestBody BulkSMSRequest bulkSMSRequest) {
        String apiKey = bulkSMSRequest.getApiKey();
        if (apiKey.equals(localApiKey)) {
            String sender = bulkSMSRequest.getSender();
            List<SMS> smsList = bulkSMSRequest.getSmsList();
            String traffic = bulkSMSRequest.getTraffic();
            String dlrUrl = bulkSMSRequest.getDlrUrl();
            List<SMS> results = new ArrayList<>();

            for (SMS sms: smsList) {
                Message msg = new Message();
                msg.setMsisdn(sms.getMobileno());
                msg.setSender(sender);
                msg.setMessage(sms.getMessage());
                msg.setTraffic(traffic);
                msg.setStatus(Constant.SMS_CREATED);
                msg.setDlrUrl(dlrUrl);
                msg.setDlrIsSent(false);
                msg.setCreatedAt(new Date());
                msg.setUpdatedAt(new Date());
                messageRepository.save(msg);

                SMS smsContent = new SMS();
                smsContent.setSmsId(sms.getSmsId());
                smsContent.setMobileno(sms.getMobileno());
                SMSResponse smsResponse = PostSMS.sendsms(smppSMSService, session, msg);
                smsContent.setMsgId(smsResponse.getMsgId());
                smsContent.setStatus(smsResponse.getStatus());
                smsContent.setMessage(smsResponse.getMessage());

                results.add(smsContent);
            }
            return new BulkSMSResponse(Constant.OK, results);
        } else {
            return new BulkSMSResponse(Constant.SMS_ERROR, null);
        }

    }

}
