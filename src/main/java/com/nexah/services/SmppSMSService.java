package com.nexah.services;

import com.cloudhopper.smpp.*;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.*;
import com.nexah.models.Message;
import com.nexah.models.Setting;
import com.nexah.repositories.MessageRepository;
import com.nexah.repositories.SettingRepository;
import com.nexah.smpp.Async;
import com.nexah.smpp.ClientSmppSessionHandler;
import com.nexah.smpp.Service;
import com.nexah.smpp.SmsStatus;
import com.nexah.utils.Constant;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@org.springframework.stereotype.Service
public class SmppSMSService {

    @Autowired
    SettingRepository settingRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    private Service service;

    private static final Logger log = LoggerFactory.getLogger(SmppSMSService.class);

    public SmsStatus sendTextMessage(SmppSession session, byte[] textBytes, Message message, Setting setting) {

        int retry = 0;
        String error = null;
        List<String> errors = Arrays.asList(setting.getSmppErrors().split(","));
        while (retry <= setting.getMaxRetry() && !errors.contains(error)) {

            try {

                SubmitSm submit = new SubmitSm();
//                submit.setDataCoding(SmppConstants.DATA_CODING_LATIN1);  //Encoded text in Latin 1 alphabet - Prend en compte les accents et les caractères spéciaux
//                submit.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);  //Encoded text in GSM 7-bit - Accents et caractères spéciaux non pris en charge;
                submit.setDataCoding(SmppConstants.DATA_CODING_UCS2);   //Encoded text in UCS2 alphabet

                submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);

                if (textBytes != null && textBytes.length > 254) {
                    submit.addOptionalParameter(
                            new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, textBytes, "message_payload"));
                } else {
                    submit.setShortMessage(textBytes);
                }

                if (message.getSender().matches("\\d+")) {
                    if (message.getSender().length() < 8) {
                        submit.setSourceAddress(new Address((byte) 0x06, (byte) 0x00, message.getSender()));  //"Short code" TON=06 NPI=00
                    } else {
                        submit.setSourceAddress(new Address((byte) 0x01, (byte) 0x01, message.getSender())); //source address Numeric TON=01 NPI=01
                    }
                } else {
                    submit.setSourceAddress(new Address((byte) 0x05, (byte) 0x00, message.getSender()));  //source address "Alphanumeric" TON=05 NPI=00
                }
                submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, message.getMsisdn()));
                DefaultChannelFuture.setUseDeadLockChecker(false);
                SubmitSmResp submitResponse = session.submit(submit, setting.getSubmitSmTimeOut());

                if (submitResponse != null && submitResponse.getMessageId() != null && !submitResponse.getMessageId().isEmpty()) {
                    try {
                        message.setRequestId(submitResponse.getMessageId());
                        message.setStatus(Constant.SMS_SENT);
                        message.setRetry(retry);
                        message.setSubmitedAt(new Date());
                        messageRepository.save(message);
                    } catch (Exception e) {
                        error = e.getLocalizedMessage();
                        message.setRequestId(submitResponse.getMessageId() + "_");
                        message.setStatus(Constant.SMS_SENT);
                        message.setRetry(retry);
                        message.setSubmitedAt(new Date());
                        message.setErrorMsg(error);
                        messageRepository.save(message);
                    }
                    return new SmsStatus(true, message.getId());
                } else {
                    error = submitResponse.getResultMessage();
                    //log.error("smpp error cmd : " + submitResponse.getCommandStatus() + " message : " + error);
                    String msg = error == null ? "NULL" : error;
                    message.setStatus(Constant.SMS_FAILED);
                    message.setErrorMsg(msg);
                    message.setRetry(retry);
                    message.setSubmitedAt(new Date());
                    messageRepository.save(message);
                }
            } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException |
                     SmppChannelException
                     | InterruptedException e) {
                error = e.getLocalizedMessage();
                //log.error("exception send sms error " + error);
                message.setStatus(Constant.SMS_FAILED);
                message.setErrorMsg(error);
                message.setSubmitedAt(new Date());
                message.setRetry(retry);
                messageRepository.save(message);
                service.setBound(false);
            }
            // Increment the retry counter
            retry++;

            if (retry <= setting.getMaxRetry() && !errors.contains(error)) {
                // Wait before retrying
                waitBeforeRetry(setting);
            }
        }
        return new SmsStatus(false, error);
    }

    private void waitBeforeRetry(Setting setting) {
        try {
            TimeUnit.MILLISECONDS.sleep(setting.getRetryDelay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public SmppSession bindSession(ArrayList<SmppSession> sessions, Service service) {
        try {
            SmppSessionConfiguration config = sessionConfiguration(service);
            SmppSession session = clientBootstrap().bind(config, new ClientSmppSessionHandler(this, sessions, messageRepository));
            sessions.add(session);
            log.error("session binded successfully !");
            return session;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void unbindService(ArrayList<SmppSession> sessions) {
        try {
            sessions.removeIf(smppSession -> smppSession.getConfiguration().getName().equals(service.getName()));
            log.error(service.getName() + " Provider unbind ready for reconnecting");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public boolean isBound(ArrayList<SmppSession> sessions) {
        for (SmppSession session : sessions) {
            if (service.getName().equals(session.getConfiguration().getName()) && session.isBound()) {
                return true;
            }
        }
        return false;
    }

    public void rebindSession(ArrayList<SmppSession> sessions) {
        try {
            sessions.removeIf(smppSession -> smppSession.getConfiguration().getName().equals(service.getName()));
            SmppSession session = bindSession(sessions, service);
            if (session != null) {
                service.setBound(true);
                log.error("session rebinded successfully !");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void unbindService(ArrayList<SmppSession> sessions, SmppSession session) {
        try {
            service.setBound(false);
            sessions.removeIf(smppSession -> smppSession.getConfiguration().getName().equals(service.getName()));
            log.error(service.getName() + " Service unbind  ready for reconnecting");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private SmppClient clientBootstrap() {
        Async async = new Async();
        return new DefaultSmppClient(Executors.newFixedThreadPool(5), async.getSmppSessionSize());
    }

    private SmppSessionConfiguration sessionConfiguration(Service service) {
        SmppSessionConfiguration sessionConfig = new SmppSessionConfiguration();
        Setting setting = settingRepository.findAll().get(0);
        sessionConfig.setName(service.getName());
        sessionConfig.setInterfaceVersion(SmppConstants.VERSION_3_4);
        sessionConfig.setType(SmppBindType.TRANSCEIVER);
        sessionConfig.setHost(service.getHost());
        sessionConfig.setPort(service.getPort());
        sessionConfig.setSystemId(service.getUsername());
        sessionConfig.setPassword(service.getPassword());
        sessionConfig.setSystemType(null);
        sessionConfig.setWindowSize(setting.getWindowsSize());
        sessionConfig.getLoggingOptions().setLogBytes(false);
        sessionConfig.getLoggingOptions().setLogPdu(true);
        return sessionConfig;
    }

}
