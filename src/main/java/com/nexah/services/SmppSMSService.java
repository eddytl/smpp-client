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
import java.util.Date;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class SmppSMSService {

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    SettingRepository settingRepository;

    private static final Logger log = LoggerFactory.getLogger(SmppSMSService.class);

    public SmsStatus sendTextMessage(SmppSession session, byte[] textBytes, Message message) {
        if (session.isBound()) {
            try {

                SubmitSm submit = new SubmitSm();
                submit.setDataCoding(SmppConstants.DATA_CODING_LATIN1);  //Encoded text in Latin 1 alphabet - Prend en compte les accents et les caractères spéciaux
//                submit.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);  //Encoded text in GSM 7-bit - Accents et caractères spéciaux non pris en charge;
//                submit.setDataCoding(SmppConstants.DATA_CODING_UCS2);   //Encoded text in UCS2 alphabet

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
                SubmitSmResp submitResponse = session.submit(submit, 60000);

                if (submitResponse.getCommandStatus() == SmppConstants.STATUS_OK) {
                    message.setRequestId(submitResponse.getMessageId());
                    message.setStatus(Constant.SMS_SENT);
                    message.setSubmitedAt(new Date());
                    messageRepository.save(message);
                    return new SmsStatus(true, message.getId());
                } else {
                    message.setStatus(Constant.SMS_FAILED);
                    message.setErrorMsg(submitResponse.getResultMessage());
                    message.setSubmitedAt(new Date());
                    messageRepository.save(message);
                    return new SmsStatus(false, submitResponse.getResultMessage());
                }
            } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException
                     | InterruptedException e) {
                message.setStatus(Constant.SMS_FAILED);
                message.setErrorMsg(e.getMessage());
                message.setSubmitedAt(new Date());
                messageRepository.save(message);
                return new SmsStatus(false, e.getMessage());
            }
        }else{
            message.setStatus(Constant.SMS_FAILED);
            message.setErrorMsg("Session not bound");
            message.setSubmitedAt(new Date());
            messageRepository.save(message);
            return new SmsStatus(false, "Session not bound");
        }
    }

    public SmppSession bindSession(SmppSession session, Service service) {
        try {
            SmppSessionConfiguration config = sessionConfiguration(service);
            session = clientBootstrap().bind(config, new ClientSmppSessionHandler(config, session, this, messageRepository));
//            sessions.add(session);
            return session;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
//    public SmppSession bindSession(SmppSession session, Service service) {
//        try {
//            SmppSessionConfiguration config = sessionConfiguration(service);
//           session = clientBootstrap().bind(config, new ClientSmppSessionHandler(config, session, this, messageRepository));
////            sessions.add(session);
//            return session;
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return null;
//        }
//    }

    public boolean isBound(SmppSession session, Service service) {
//        for (SmppSession session : sessions) {
            if (service.getName().equals(session.getConfiguration().getName()) && session.isBound()) {
                return true;
            }
//        }
        return false;
    }
    public void unbindServiceOnDB(ArrayList<SmppSession> sessions, SmppSession session) {
        try {
            sessions.removeIf(smppSession -> smppSession.getConfiguration().getName().equals(session.getConfiguration().getName()));
            log.info(session.getConfiguration().getName() + " Service unbind on Database ready for reconnecting");
//            sendMailUnbind(appConfigRepository, emailService, service.getName());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public void unbindServiceOnDB(ArrayList<SmppSession> sessions, SmppSessionConfiguration smppSessionConfiguration
    ) {
        try {
            sessions.removeIf(smppSession -> smppSession.getConfiguration().getName().equals(smppSessionConfiguration.getName()));
            log.info(smppSessionConfiguration.getName() + " Provider unbind on Database ready for reconnecting");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void rebindSession(SmppSession session, Service service) {
        try {
//            sessions.removeIf(smppSession -> smppSession.getConfiguration().getName().equals(service.getName()));
            session = bindSession(session, service);
            if (session != null) {
                service.setBound(true);
                log.info("session bound");
            }
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
        Setting setting = settingRepository.findById(Constant.SETTING_ID).get();
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
