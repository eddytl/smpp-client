package com.nexah.services;

import com.cloudhopper.smpp.*;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.*;
import com.nexah.smpp.Async;
import com.nexah.smpp.ClientSmppSessionHandler;
import com.nexah.smpp.Service;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class SmppSMSService {

    private static final Logger log = LoggerFactory.getLogger(SmppSMSService.class);

    public String sendTextMessage(SmppSession session, String sourceAddress, byte[] textBytes, String destinationAddress) {
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

                if (sourceAddress.matches("\\d+")) {
                    if (sourceAddress.length() < 8) {
                        submit.setSourceAddress(new Address((byte) 0x06, (byte) 0x00, sourceAddress));  //"Short code" TON=06 NPI=00
                    } else {
                        submit.setSourceAddress(new Address((byte) 0x01, (byte) 0x01, sourceAddress)); //source address Numeric TON=01 NPI=01
                    }
                } else {
                    submit.setSourceAddress(new Address((byte) 0x05, (byte) 0x00, sourceAddress));  //source address "Alphanumeric" TON=05 NPI=00
                }
                submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, destinationAddress));
                DefaultChannelFuture.setUseDeadLockChecker(false);
                SubmitSmResp submitResponse = session.submit(submit, 100000);

                if (submitResponse.getCommandStatus() == SmppConstants.STATUS_OK) {
                    return submitResponse.getMessageId();
                } else {
                    throw new IllegalStateException(submitResponse.getResultMessage());
                }
            } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException
                     | InterruptedException e) {
                log.error(e.getMessage());
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("SMPP session is not connected");
    }

    public SmppSession bindSession(ArrayList<SmppSession> sessions, Service service) {
        try {
            SmppSessionConfiguration config = sessionConfiguration(service);
            SmppSession session = clientBootstrap().bind(config, new ClientSmppSessionHandler(config, sessions, this));
            sessions.add(session);
            return session;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
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

    public void rebindSession(ArrayList<SmppSession> sessions, Service service) {
        try {
            sessions.removeIf(smppSession -> smppSession.getConfiguration().getName().equals(service.getName()));
            SmppSession session = bindSession(sessions, service);
            if (session != null) {
                log.info("session bound");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private SmppClient clientBootstrap() {
        Async async = new Async();
        return new DefaultSmppClient(Executors.newCachedThreadPool(), async.getSmppSessionSize());
    }

    private SmppSessionConfiguration sessionConfiguration(Service service) {
        SmppSessionConfiguration sessionConfig = new SmppSessionConfiguration();
        sessionConfig.setName(service.getName());
        sessionConfig.setInterfaceVersion(SmppConstants.VERSION_3_4);
        sessionConfig.setType(SmppBindType.TRANSCEIVER);
        sessionConfig.setHost(service.getHost());
        sessionConfig.setPort(service.getPort());
        sessionConfig.setSystemId(service.getUsername());
        sessionConfig.setPassword(service.getPassword());
        sessionConfig.setSystemType(null);
        sessionConfig.setWindowSize(20);
        sessionConfig.getLoggingOptions().setLogBytes(false);
        sessionConfig.getLoggingOptions().setLogPdu(true);
        return sessionConfig;
    }
}
