package com.nexah.controllers;

import com.cloudhopper.smpp.SmppSession;
import com.nexah.http.requests.BulkSMSRequest;
import com.nexah.http.requests.SMS;
import com.nexah.http.requests.SMSRequest;
import com.nexah.http.responses.BulkSMSResponse;
import com.nexah.http.responses.SMSResponse;
import com.nexah.http.rest.PostSMS;
import com.nexah.models.Message;
import com.nexah.models.Setting;
import com.nexah.repositories.MessageRepository;
import com.nexah.repositories.SettingRepository;
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
    @Autowired
    SettingRepository settingRepository;

    @GetMapping(value = "/sendsms")
    public @ResponseBody
    SMSResponse sendsms(@RequestParam(name = "apiKey") String apiKey, @RequestParam(name = "traffic") String traffic,
                        @RequestParam(name = "mobileno") String mobileno, @RequestParam(name = "sender") String sender,
                        @RequestParam(name = "message") String message, @RequestParam(name = "dlrUrl") String dlrUrl) {
        if (apiKey.equals(localApiKey)) {

            if (session.isBound()) {
                if (session.getConfiguration().getName().equals(traffic)) {
                    if (!sender.isEmpty() && sender.length() <= Constant.MAX_SID_LENGTH && mobileno.length() == Constant.MSISDN_LENGTH && !message.isEmpty() && message.length() <= Constant.MAX_MSG_LENGTH) {
                        Setting setting = settingRepository.findAll().get(0);

                        Message msg = new Message();
                        msg.setMsisdn(mobileno);
                        msg.setSender(sender);
                        msg.setMessage(message);
                        msg.setTraffic(traffic);
                        msg.setStatus(Constant.SMS_CREATED);
                        msg.setRetry(0);
                        msg.setDlrUrl(dlrUrl);
                        msg.setDlrIsSent(false);
                        msg.setCreatedAt(new Date());
                        msg.setUpdatedAt(new Date());
                        messageRepository.save(msg);

                        return PostSMS.sendsms(smppSMSService, session, msg, setting);
                    } else {
                        return new SMSResponse(Constant.SMS_ERROR, Constant.INVALID_CREDENTIALS, null);
                    }
                } else {
                    return new SMSResponse(Constant.SMS_ERROR, Constant.TRAFFIC_NOT_FOUND, null);
                }
            } else {
                return new SMSResponse(Constant.SMS_ERROR, Constant.SERVER_NOT_BOUND, null);
            }
        } else {
            return new SMSResponse(Constant.SMS_ERROR, Constant.INVALID_KEY, null);
        }

    }

    @PostMapping(value = "/sendsms")
    public @ResponseBody
    SMSResponse sendsms(@RequestBody SMSRequest smsRequest) {
        String apiKey = smsRequest.getApiKey();
        if (apiKey.equals(localApiKey)) {
            String traffic = smsRequest.getTraffic();

            if (session.isBound()) {
                if (session.getConfiguration().getName().equals(traffic)) {

                    String sender = smsRequest.getSender();
                    String message = smsRequest.getMessage();
                    String mobileno = smsRequest.getMobileno();
                    String dlrUrl = smsRequest.getDlrUrl();

                    if (!sender.isEmpty() && sender.length() <= Constant.MAX_SID_LENGTH && mobileno.length() == Constant.MSISDN_LENGTH && !message.isEmpty() && message.length() <= Constant.MAX_MSG_LENGTH) {
                        Setting setting = settingRepository.findAll().get(0);

                        Message msg = new Message();
                        msg.setMsisdn(mobileno);
                        msg.setSender(sender);
                        msg.setMessage(message);
                        msg.setTraffic(traffic);
                        msg.setRetry(0);
                        msg.setStatus(Constant.SMS_CREATED);
                        msg.setDlrUrl(dlrUrl);
                        msg.setDlrIsSent(false);
                        msg.setCreatedAt(new Date());
                        msg.setUpdatedAt(new Date());
                        messageRepository.save(msg);

                        return PostSMS.sendsms(smppSMSService, session, msg, setting);
                    } else {
                        return new SMSResponse(Constant.SMS_ERROR, Constant.INVALID_CREDENTIALS, null);
                    }
                } else {
                    return new SMSResponse(Constant.SMS_ERROR, Constant.TRAFFIC_NOT_FOUND, null);
                }
            } else {
                return new SMSResponse(Constant.SMS_ERROR, Constant.SERVER_NOT_BOUND, null);
            }
        } else {
            return new SMSResponse(Constant.SMS_ERROR, Constant.INVALID_KEY, null);
        }
    }

    @PostMapping(value = "/sendbulksms")
    public @ResponseBody
    BulkSMSResponse sendbulksms(@RequestBody BulkSMSRequest bulkSMSRequest) {
        String apiKey = bulkSMSRequest.getApiKey();
        if (apiKey.equals(localApiKey)) {
            String traffic = bulkSMSRequest.getTraffic();
            String sender = bulkSMSRequest.getSender();

            if (!sender.isEmpty() && sender.length() <= Constant.MAX_SID_LENGTH) {
                if (session.isBound()) {
                    if (session.getConfiguration().getName().equals(traffic)) {

                        List<SMS> smsList = bulkSMSRequest.getSmsList();
                        String dlrUrl = bulkSMSRequest.getDlrUrl();
                        List<SMS> results = new ArrayList<>();

                        for (SMS sms : smsList) {
                            SMS smsContent = new SMS();
                            if (sms.getMobileno().length() == Constant.MSISDN_LENGTH && !sms.getMessage().isEmpty() && sms.getMessage().length() <= Constant.MAX_MSG_LENGTH) {
                                Setting setting = settingRepository.findAll().get(0);

                                Message msg = new Message();
                                msg.setMsisdn(sms.getMobileno());
                                msg.setSender(sender);
                                msg.setMessage(sms.getMessage());
                                msg.setTraffic(traffic);
                                msg.setRetry(0);
                                msg.setStatus(Constant.SMS_CREATED);
                                msg.setDlrUrl(dlrUrl);
                                msg.setDlrIsSent(false);
                                msg.setCreatedAt(new Date());
                                msg.setUpdatedAt(new Date());
                                messageRepository.save(msg);

                                smsContent.setSmsId(sms.getSmsId());
                                smsContent.setMobileno(sms.getMobileno());
                                SMSResponse smsResponse = PostSMS.sendsms(smppSMSService, session, msg, setting);
                                smsContent.setMsgId(smsResponse.getMsgId());
                                smsContent.setStatus(smsResponse.getStatus());
                                smsContent.setMessage(smsResponse.getMessage());
                            } else {

                                smsContent.setSmsId(sms.getSmsId());
                                smsContent.setMobileno(sms.getMobileno());
                                smsContent.setMsgId(null);
                                smsContent.setStatus(Constant.SMS_ERROR);
                                smsContent.setMessage("mobileno or message length is no valid");
                            }
                            results.add(smsContent);
                        }
                        return new BulkSMSResponse(Constant.SMS_SENT, Constant.SMS_MSG_SENT, results);
                    } else {
                        return new BulkSMSResponse(Constant.SMS_ERROR, Constant.TRAFFIC_NOT_FOUND, null);
                    }
                } else {
                    return new BulkSMSResponse(Constant.SMS_ERROR, Constant.SERVER_NOT_BOUND, null);
                }
            } else {
                return new BulkSMSResponse(Constant.SMS_ERROR, Constant.INVALID_SID, null);
            }
        } else {
            return new BulkSMSResponse(Constant.SMS_ERROR, Constant.INVALID_KEY, null);
        }

    }

}
