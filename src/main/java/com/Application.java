package com;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.nexah.services.SmppSMSService;
import com.nexah.smpp.Service;
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

@SpringBootApplication
@EntityScan
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    @Autowired
    private ArrayList<SmppSession> sessions = new ArrayList<>();
    @Autowired
    private SmppSMSService smppSMSService;
    @Autowired
    private ArrayList<Service> services = new ArrayList<>();

    static public void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        ArrayList<SmppSession> sessions = (ArrayList<SmppSession>) ctx.getBean("sessions");
        List<Service> services = (ArrayList<Service>) ctx.getBean("services");

//        startSession();
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

}
