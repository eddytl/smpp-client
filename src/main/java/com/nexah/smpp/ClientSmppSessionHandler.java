package com.nexah.smpp;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.nexah.http.responses.DLRresp;
import com.nexah.http.rest.PostSMS;
import com.nexah.models.Message;
import com.nexah.repositories.MessageRepository;
import com.nexah.services.SmppSMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

public class ClientSmppSessionHandler extends DefaultSmppSessionHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientSmppSessionHandler.class);
    private SmppSMSService smppSMSService;
    private SmppSessionConfiguration smppSessionConfiguration;
//    private ArrayList<SmppSession> sessions;
    private SmppSession session;
    private MessageRepository messageRepository;


//    public ClientSmppSessionHandler(SmppSessionConfiguration smppSessionConfiguration, ArrayList<SmppSession> sessions,
//                                    SmppSMSService smppSMSService, MessageRepository messageRepository) {
//        this.smppSMSService = smppSMSService;
//        this.sessions = sessions;
//        this.smppSessionConfiguration = smppSessionConfiguration;
//        this.messageRepository = messageRepository;
//    }
    public ClientSmppSessionHandler(SmppSessionConfiguration smppSessionConfiguration, SmppSession session,
                                    SmppSMSService smppSMSService, MessageRepository messageRepository) {
        this.smppSMSService = smppSMSService;
        this.session = session;
        this.smppSessionConfiguration = smppSessionConfiguration;
        this.messageRepository = messageRepository;
    }

    private String mapDataCodingToCharset(byte dataCoding) {
        switch (dataCoding) {
            case SmppConstants.DATA_CODING_LATIN1:
                return CharsetUtil.NAME_ISO_8859_1;
            case SmppConstants.DATA_CODING_UCS2:
                return CharsetUtil.NAME_UCS_2;
            default:
                return CharsetUtil.NAME_GSM;
        }
    }


    @Override
    public void fireChannelUnexpectedlyClosed() {
        super.fireChannelUnexpectedlyClosed();
//        smppSMSService.unbindServiceOnDB(session, smppSessionConfiguration);
        session.close();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public PduResponse firePduRequestReceived(PduRequest request) {
        PduResponse response = null;
        try {
            if (request instanceof DeliverSm) {
                //String msisdn = ((DeliverSm) request).getSourceAddress().getAddress();
                String message = CharsetUtil.decode(((DeliverSm) request).getShortMessage(),
                        mapDataCodingToCharset(((DeliverSm) request).getDataCoding()));
                DeliveryReceipt dlr = DeliveryReceipt.parseShortMessage(message, ZoneOffset.UTC);
                Message msg = messageRepository.findByRequestId(dlr.getMessageId().replaceAll("^00+", ""));
                LocalDateTime deliveryDate = dlr.getDoneDate().toLocalDateTime();
                Instant instant = deliveryDate.atZone(ZoneId.systemDefault()).toInstant();
                Date donedate = Date.from(instant);
                if (msg != null){
                    msg.setStatus(dlr.toStateText(dlr.getState()));
                    msg.setDeliveredAt(donedate);
                    messageRepository.save(msg);
                    DLRresp resp = PostSMS.sendDLR(msg);
                    if (resp.getStatus() == 1){
                        msg.setDlrIsSent(true);
                        messageRepository.save(msg);
                    }
                }
            }
            response = request.createResponse();
        } catch (Throwable error) {
            log.error("Error while handling delivery", error);
            response = request.createResponse();
            response.setResultMessage(error.getMessage());
            response.setCommandStatus(SmppConstants.STATUS_UNKNOWNERR);
        }
        return response;
    }

}
