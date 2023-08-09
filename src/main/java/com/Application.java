package com;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.*;
import com.cloudhopper.smpp.type.*;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.nexah.services.SmppSMSService;
import com.nexah.smpp.Service;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EntityScan
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ArrayList<SmppSession> sessions = new ArrayList<>();
    @Autowired
    private SmppSMSService smppSMSService;
    @Autowired
    private ArrayList<Service> services = new ArrayList<>();

    static public void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
//        ArrayList<SmppSession> sessions = (ArrayList<SmppSession>) ctx.getBean("sessions");
//        List<Service> services = (ArrayList<Service>) ctx.getBean("services");

        //
        // setup 3 things required for any session we plan on creating
        //

        // for monitoring thread use, it's preferable to create your own instance
        // of an executor with Executors.newCachedThreadPool() and cast it to ThreadPoolExecutor
        // this permits exposing thinks like executor.getActiveCount() via JMX possible
        // no point renaming the threads in a factory since underlying Netty
        // framework does not easily allow you to customize your thread names
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        // to enable automatic expiration of requests, a second scheduled executor
        // is required which is what a monitor task will be executed with - this
        // is probably a thread pool that can be shared with between all client bootstraps
        ScheduledThreadPoolExecutor monitorExecutor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1, new ThreadFactory() {
            private AtomicInteger sequence = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("SmppClientSessionWindowMonitorPool-" + sequence.getAndIncrement());
                return t;
            }
        });

        // a single instance of a client bootstrap can technically be shared
        // between any sessions that are created (a session can go to any different
        // number of SMSCs) - each session created under
        // a client bootstrap will use the executor and monitorExecutor set
        // in its constructor - just be *very* careful with the "expectedSessions"
        // value to make sure it matches the actual number of total concurrent
        // open sessions you plan on handling - the underlying netty library
        // used for NIO sockets essentially uses this value as the max number of
        // threads it will ever use, despite the "max pool size", etc. set on
        // the executor passed in here
        DefaultSmppClient clientBootstrap = new DefaultSmppClient(Executors.newCachedThreadPool(), 1, monitorExecutor);

        //
        // setup configuration for a client session
        //
        DefaultSmppSessionHandler sessionHandler = new ClientSmppSessionHandler();

        SmppSessionConfiguration config0 = new SmppSessionConfiguration();
        config0.setWindowSize(1);
        config0.setName("Tester.Session.Com");
        config0.setType(SmppBindType.TRANSCEIVER);
        config0.setHost("80.12.36.131");
        config0.setPort(2775);
        config0.setWindowSize(500);
        config0.setConnectTimeout(10000);
        config0.setSystemId("A2Pnexah2");
        config0.setPassword("A2P75int");
        config0.getLoggingOptions().setLogBytes(false);
        // to enable monitoring (request expiration)
        config0.setRequestExpiryTimeout(30000);
        config0.setWindowMonitorInterval(15000);
        config0.setCountersEnabled(true);

        //
        // create session, enquire link, submit an sms, close session
        //
        SmppSession session0 = null;

        try {
            // create session a session by having the bootstrap connect a
            // socket, send the bind request, and wait for a bind response
            session0 = clientBootstrap.bind(config0, sessionHandler);

            System.out.println("Press any key to send enquireLink #1");
            System.in.read();

            // demo of a "synchronous" enquireLink call - send it and wait for a response
            EnquireLinkResp enquireLinkResp1 = session0.enquireLink(new EnquireLink(), 10000);
            logger.info("enquire_link_resp #1: commandStatus [" + enquireLinkResp1.getCommandStatus() + "=" + enquireLinkResp1.getResultMessage() + "]");

            System.out.println("Press any key to send enquireLink #2");
            System.in.read();

            // demo of an "asynchronous" enquireLink call - send it, get a future,
            // and then optionally choose to pick when we wait for it
            WindowFuture<Integer,PduRequest,PduResponse> future0 = session0.sendRequestPdu(new EnquireLink(), 10000, true);
            if (!future0.await()) {
                logger.error("Failed to receive enquire_link_resp within specified time");
            } else if (future0.isSuccess()) {
                EnquireLinkResp enquireLinkResp2 = (EnquireLinkResp)future0.getResponse();
                logger.info("enquire_link_resp #2: commandStatus [" + enquireLinkResp2.getCommandStatus() + "=" + enquireLinkResp2.getResultMessage() + "]");
            } else {
                logger.error("Failed to properly receive enquire_link_resp: " + future0.getCause());
            }

            System.out.println("Press any key to send submit #1");
            System.in.read();

            String text160 = "Bonjour SMPP";
            final byte[] textBytes = CharsetUtil.encode(text160, CharsetUtil.CHARSET_ISO_8859_1);

            //for(int i=0; i < ; i++) {
            try {
                final SmppSession finalSession = session0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SubmitSm submit0 = new SubmitSm();

                        // add delivery receipt

                        submit0.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);

                        submit0.setSourceAddress(new Address((byte) 0x03, (byte) 0x00, "NEXAH"));
                        submit0.setDestAddress(new Address((byte) 0x01, (byte) 0x01, "237678018812"));
                        try {
                            submit0.setShortMessage(textBytes);
                            submit0.setDataCoding(SmppConstants.DATA_CODING_LATIN1);
                        } catch (SmppInvalidArgumentException e) {
                            e.printStackTrace();
                        }

                        try {
                            SubmitSmResp submitResp = finalSession.submit(submit0, 30000);
                            logger.info("result Message {}", submitResp.getResultMessage());
                        } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException | InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
            }catch (Exception e){
                logger.error(e.getMessage());
            }
            //}

            logger.info("sendWindow.size: {}", session0.getSendWindow().getSize());

            System.out.println("Press any key to unbind and close sessions");
            System.in.read();

            session0.unbind(5000);
        } catch (Exception e) {
            logger.error("", e);
        }

        /*if (session0 != null) {
            logger.info("Cleaning up session... (final counters)");
            if (session0.hasCounters()) {
                logger.info("tx-enquireLink: {}", session0.getCounters().getTxEnquireLink());
                logger.info("tx-submitSM: {}", session0.getCounters().getTxSubmitSM());
                logger.info("tx-deliverSM: {}", session0.getCounters().getTxDeliverSM());
                logger.info("tx-dataSM: {}", session0.getCounters().getTxDataSM());
                logger.info("rx-enquireLink: {}", session0.getCounters().getRxEnquireLink());
                logger.info("rx-submitSM: {}", session0.getCounters().getRxSubmitSM());
                logger.info("rx-deliverSM: {}", session0.getCounters().getRxDeliverSM());
                logger.info("rx-dataSM: {}", session0.getCounters().getRxDataSM());
            }

            session0.destroy();
            // alternatively, could call close(), get outstanding requests from
            // the sendWindow (if we wanted to retry them later), then call shutdown()
        }

        // this is required to not causing server to hang from non-daemon threads
        // this also makes sure all open Channels are closed to I *think*
        logger.info("Shutting down client bootstrap and executors...");
        clientBootstrap.destroy();
        executor.shutdownNow();
        monitorExecutor.shutdownNow();

        logger.info("Done. Exiting");*/
    }

    @Bean(name = "services")
    public ArrayList<Service> services() {

        Service service01 = new Service();
        service01.setName("Transactional");
        service01.setHost("80.12.36.131");
        service01.setPort(2775);
        service01.setUsername("A2Pnexah1");
        service01.setPassword("A2P75int");
        service01.setBound(false);
        services.add(service01);

        Service service02 = new Service();
        service02.setName("Commercial");
        service02.setHost("80.12.36.131");
        service02.setPort(2775);
        service02.setUsername("A2Pnexah2");
        service02.setPassword("A2P75int");
        service02.setBound(false);
        services.add(service02);

        return services;
    }

    @Bean(name = "sessions")
    public ArrayList<SmppSession> sessions(List<Service> services, SmppSMSService smppSMSService) {
        for (Service service : services) {
            Thread newThread = new Thread(() -> {
                if (!service.getBound()) {
                    smppSMSService.bindSession(sessions, service);
                }
            });
            newThread.start();
        }
        return sessions;
    }


    @Scheduled(initialDelayString = "${sms.async.initial-delay}", fixedDelayString = "${sms.async.initial-delay}")
    void enquireLinkJob() {
        try {
            for (SmppSession session : sessions) {
                if (session.isBound()) {
                    try {
                        session.enquireLink(new EnquireLink(), 60000);
                    } catch (SmppTimeoutException | SmppChannelException e) {
                        log.info(session.getConfiguration().getName() + " Enquire link failed, executing reconnect; " + e);
                        smppSMSService.unbindServiceOnDB(sessions, session);
                    } catch (InterruptedException e) {
                        log.info(session.getConfiguration().getName() + " Enquire link interrupted, probably killed by reconnecting");
                        smppSMSService.unbindServiceOnDB(sessions, session);
                    } catch (Exception e) {
                        log.error(session.getConfiguration().getName() + " Enquire link failed, executing reconnect", e);
                        smppSMSService.unbindServiceOnDB(sessions, session);
                    }
                } else {
                    log.error(session.getConfiguration().getName() + " enquire link running while session is not connected");
                }
            }
        } catch (Exception $e) {
            log.error($e.getMessage());
        }

    }

    /**
     * Could either implement SmppSessionHandler or only override select methods
     * by extending a DefaultSmppSessionHandler.
     */
    public static class ClientSmppSessionHandler extends DefaultSmppSessionHandler {

        public ClientSmppSessionHandler() {
            super(logger);
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
        public void firePduRequestExpired(PduRequest pduRequest) {
            logger.warn("PDU request expired: {}", pduRequest);
        }

        @Override
        public PduResponse firePduRequestReceived(PduRequest pduRequest) {
            PduResponse response = pduRequest.createResponse();

            try{
                if (pduRequest instanceof DeliverSm) {
                    String sourceAddress = ((DeliverSm) pduRequest).getSourceAddress().getAddress();
                    String message = CharsetUtil.decode(((DeliverSm) pduRequest).getShortMessage(),
                            mapDataCodingToCharset(((DeliverSm) pduRequest).getDataCoding()));
                    byte dataCoding = ((DeliverSm) pduRequest).getDataCoding();
                    logger.debug("SMS Message Received: {}, Source Address: {}", message.trim(), sourceAddress);
                    logger.info("DLR: {}", message);
                    DeliveryReceipt dlr = DeliveryReceipt.parseShortMessage(message, DateTimeZone.UTC);
                    logger.info("Received delivery from {} at {} with message-id {} and status {}", sourceAddress,
                            dlr.getDoneDate(), dlr.getMessageId(), DeliveryReceipt.toStateText(dlr.getState()));

                }
                response = pduRequest.createResponse();
            } catch (Throwable error) {
                logger.warn("Error while handling delivery", error);
                response = pduRequest.createResponse();
                response.setResultMessage(error.getMessage());
                response.setCommandStatus(SmppConstants.STATUS_UNKNOWNERR);
            }

            // do any logic here

            return response;
        }

    }
}
