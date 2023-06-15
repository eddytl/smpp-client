package com.nexah.smpp;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.nexah.services.SmppSMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.ArrayList;

public class ClientSmppSessionHandler extends DefaultSmppSessionHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientSmppSessionHandler.class);
    private SmppSMSService smppSMSService;
    private SmppSessionConfiguration smppSessionConfiguration;
    private ArrayList<SmppSession> sessions;


    public ClientSmppSessionHandler(SmppSessionConfiguration smppSessionConfiguration, ArrayList<SmppSession> sessions,
                                    SmppSMSService smppSMSService) {
        this.smppSMSService = smppSMSService;
        this.sessions = sessions;
        this.smppSessionConfiguration = smppSessionConfiguration;
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
        smppSMSService.unbindServiceOnDB(sessions, smppSessionConfiguration);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public PduResponse firePduRequestReceived(PduRequest request) {
        PduResponse response = null;
        try {
            if (request instanceof DeliverSm) {
                //String sourceAddress = ((DeliverSm) request).getSourceAddress().getAddress();
                String message = CharsetUtil.decode(((DeliverSm) request).getShortMessage(),
                        mapDataCodingToCharset(((DeliverSm) request).getDataCoding()));
                byte dataCoding = ((DeliverSm) request).getDataCoding();
                DeliveryReceipt dlr = DeliveryReceipt.parseShortMessage(message, ZoneOffset.UTC);
                log.info("DLR {}", dlr);
//                PostSMS.sendDLR(dlr);
            }
            response = request.createResponse();
        } catch (Throwable error) {
            log.warn("Error while handling delivery", error);
            response = request.createResponse();
            response.setResultMessage(error.getMessage());
            response.setCommandStatus(SmppConstants.STATUS_UNKNOWNERR);
        }
        return response;
    }

}
