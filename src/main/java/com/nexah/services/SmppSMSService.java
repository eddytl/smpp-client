package com.nexah.services;

import com.cloudhopper.commons.charset.GSMCharset;
import com.cloudhopper.smpp.*;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.*;
import com.cloudhopper.smpp.util.SmppUtil;
import com.nexah.smpp.Async;
import com.nexah.smpp.ClientSmppSessionHandler;
import com.nexah.smpp.GsmUtil;
import com.nexah.smpp.Service;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class SmppSMSService {

    private static final Logger log = LoggerFactory.getLogger(SmppSMSService.class);

    public String sendTextMessage(SmppSession session, String sourceAddress, byte[] textBytes, String destinationAddress, byte dataEncoding) {
        if (session.isBound()) {
            String messageId = null;
            try {

                // create a reference number for the message (any value from 0 to 255)
                /*byte referenceNumber = 0x01;

                byte[][] shortMessages = GsmUtil.createConcatenatedBinaryShortMessages(textBytes, referenceNumber);
                for(byte[] shortMessage:shortMessages){
                    //Send concatenated SMS
                    SubmitSm submit = new SubmitSm();
                    submit.setDataCoding(SmppConstants.DATA_CODING_LATIN1);  //Encoded text in Latin 1 alphabet - Prend en compte les accents et les caractères spéciaux

                    // set the esmClass to enable UDHI
                    submit.setEsmClass((byte) 0x40);
                    submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);

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
                    submit.setShortMessage(shortMessage);
                    DefaultChannelFuture.setUseDeadLockChecker(false);
                    SubmitSmResp submitResponse = session.submit(submit, 100000);

                    if (submitResponse.getCommandStatus() == SmppConstants.STATUS_OK) {
                        messageId = submitResponse.getMessageId();
                        log.info("SMS submitted, message id {}", submitResponse.getMessageId());
                        return messageId;
                    } else {
                        log.info(submitResponse.getResultMessage());
                        throw new IllegalStateException(submitResponse.getResultMessage());
                    }
                }*/

                // end send concatenated sms
                log.info("Text Bytes length " + textBytes.length);
                if (textBytes.length > 254) {
                    //int maximumMultipartMessageSegmentSize = 160;
                    byte referenceNumber = 0x01;
                    //byte[][] shortMessages = splitUnicodeMessage(textBytes, maximumMultipartMessageSegmentSize);
                    byte[][] shortMessages = GsmUtil.createConcatenatedBinaryShortMessages(textBytes, referenceNumber);

                    // submit all messages
                    for (byte[] bytes : shortMessages) {
                        SubmitSm submit = new SubmitSm();
                        submit.setDataCoding(dataEncoding);
                        submit.setEsmClass(SmppConstants.ESM_CLASS_UDHI_MASK);
                        submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
                        submit.setShortMessage(bytes);
                        messageId = this.sendSingleSMS(sourceAddress, session, destinationAddress, submit);
                    }
                    return messageId;
                } else {
                    SubmitSm submit = new SubmitSm();
                    submit.setDataCoding(dataEncoding);
                    submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
                    submit.setShortMessage(textBytes);
                    return this.sendSingleSMS(sourceAddress, session, destinationAddress, submit);
                }
            } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException
                     | InterruptedException e) {
                log.info(e.getMessage());
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("SMPP session is not connected");
    }

    public String sendTextMessage2(SmppSession session, String sourceAddress, byte[] textBytes, String destinationAddress, byte dataEncoding) {
        if (session.isBound()) {
            String messageId;
            try {

                // create a reference number for the message (any value from 0 to 255)
                /*byte referenceNumber = 0x01;

                byte[][] shortMessages = GsmUtil.createConcatenatedBinaryShortMessages(textBytes, referenceNumber);
                for(byte[] shortMessage:shortMessages){
                    //Send concatenated SMS
                    SubmitSm submit = new SubmitSm();
                    submit.setDataCoding(SmppConstants.DATA_CODING_LATIN1);  //Encoded text in Latin 1 alphabet - Prend en compte les accents et les caractères spéciaux

                    // set the esmClass to enable UDHI
                    submit.setEsmClass((byte) 0x40);
                    submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);

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
                    submit.setShortMessage(shortMessage);
                    DefaultChannelFuture.setUseDeadLockChecker(false);
                    SubmitSmResp submitResponse = session.submit(submit, 100000);

                    if (submitResponse.getCommandStatus() == SmppConstants.STATUS_OK) {
                        messageId = submitResponse.getMessageId();
                        log.info("SMS submitted, message id {}", submitResponse.getMessageId());
                        return messageId;
                    } else {
                        log.info(submitResponse.getResultMessage());
                        throw new IllegalStateException(submitResponse.getResultMessage());
                    }
                }*/


                // end send concatenated sms


                SubmitSm submit = new SubmitSm();
                //submit.setDataCoding(SmppConstants.DATA_CODING_LATIN1);  //Encoded text in Latin 1 alphabet - Prend en compte les accents et les caractères spéciaux
                //submit.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);  //Encoded text in GSM 7-bit - Accents et caractères spéciaux non pris en charge;
                //submit.setDataCoding(SmppConstants.DATA_CODING_UCS2);   //Encoded text in UCS2 alphabet - Tous les textes apparaissent en chinois
                submit.setDataCoding(dataEncoding);

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
                    messageId = submitResponse.getMessageId();
                    log.info("SMS submitted, message id {}", submitResponse.getMessageId());
                    return messageId;
                } else {
                    log.info(submitResponse.getResultMessage());
                    throw new IllegalStateException(submitResponse.getResultMessage());
                }
            } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException
                     | InterruptedException e) {
                log.info(e.getMessage());
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("SMPP session is not connected");
    }

    private static byte[][] splitUnicodeMessage(byte[] aMessage, Integer maximumMultipartMessageSegmentSize) {
        final byte UDHIE_HEADER_LENGTH = 0x05;
        final byte UDHIE_IDENTIFIER_SAR = 0x00;
        final byte UDHIE_SAR_LENGTH = 0x03;

        // determine how many messages have to be sent
        int numberOfSegments = aMessage.length / maximumMultipartMessageSegmentSize;
        int messageLength = aMessage.length;
        if (numberOfSegments > 255) {
            numberOfSegments = 255;
            messageLength = numberOfSegments * maximumMultipartMessageSegmentSize;
        }
        if ((messageLength % maximumMultipartMessageSegmentSize) > 0) {
            numberOfSegments++;
        }

        // prepare array for all of the msg segments
        byte[][] segments = new byte[numberOfSegments][];

        int lengthOfData;

        // generate new reference number
        byte[] referenceNumber = new byte[1];
        new Random().nextBytes(referenceNumber);

        // split the message adding required headers
        for (int i = 0; i < numberOfSegments; i++) {
            if (numberOfSegments - i == 1) {
                lengthOfData = messageLength - i * maximumMultipartMessageSegmentSize;
            } else {
                lengthOfData = maximumMultipartMessageSegmentSize;
            }
            // new array to store the header
            segments[i] = new byte[6 + lengthOfData];

            // UDH header
            // doesn't include itself, its header length
            segments[i][0] = UDHIE_HEADER_LENGTH;
            // SAR identifier
            segments[i][1] = UDHIE_IDENTIFIER_SAR;
            // SAR length
            segments[i][2] = UDHIE_SAR_LENGTH;
            // reference number (same for all messages)
            segments[i][3] = referenceNumber[0];
            // total number of segments
            segments[i][4] = (byte) numberOfSegments;
            // segment number
            segments[i][5] = (byte) (i + 1);
            // copy the data into the array
            System.arraycopy(aMessage, (i * maximumMultipartMessageSegmentSize), segments[i], 6, lengthOfData);
        }
        return segments;
    }

    public String sendSingleSMS(String sourceAddress, SmppSession session, String destinationAddress, SubmitSm submit) throws SmppTimeoutException, RecoverablePduException, UnrecoverablePduException, SmppChannelException, InterruptedException {

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
            String messageId = submitResponse.getMessageId();
            log.info("SMS submitted, message id {}", submitResponse.getMessageId());
            return messageId;
        } else {
            log.error(submitResponse.getResultMessage());
            throw new IllegalStateException(submitResponse.getResultMessage());
        }
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
