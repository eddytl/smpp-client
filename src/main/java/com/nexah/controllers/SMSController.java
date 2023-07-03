package com.nexah.controllers;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.nexah.http.requests.SMSRequest;
import com.nexah.http.responses.SMSResponse;
import com.nexah.http.rest.PostSMS;
import com.nexah.services.SmppSMSService;
import com.nexah.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin
public class SMSController {

    @Autowired
    ArrayList<SmppSession> sessions;
    @Autowired
    SmppSMSService smppSMSService;
    @Value("${api.key}")
    String localApiKey;

    @GetMapping(value = "/sendsms")
    public @ResponseBody
    SMSResponse sendsms(@RequestParam(name = "apiKey") String apiKey, @RequestParam(name = "traffic") String traffic, @RequestParam(name = "mobileno") String mobileno, @RequestParam(name = "sender") String sender,
                        @RequestParam(name = "message") String message) throws SmppInvalidArgumentException {
        if (apiKey.equals(localApiKey)){
            return PostSMS.sendsms(smppSMSService, sessions, traffic, sender, mobileno, message);
        }else{
            return new SMSResponse(Constant.SMS_ERROR, "Invalid ApiKey", null);
        }
    }

    @PostMapping(value = "/sendsms")
    public @ResponseBody
    SMSResponse sendsms(@RequestBody SMSRequest smsRequest) throws SmppInvalidArgumentException {
        String apiKey = smsRequest.getApiKey();
        if (apiKey.equals(localApiKey)){
            String sender = smsRequest.getSender();
            String message = smsRequest.getMessage();
            String mobileno = smsRequest.getMobileno();
            String traffic = smsRequest.getTraffic();
            return PostSMS.sendsms(smppSMSService, sessions, traffic, sender, mobileno, message);
        }else{
            return new SMSResponse(Constant.SMS_ERROR, "Invalid ApiKey", null);
        }

    }

}
