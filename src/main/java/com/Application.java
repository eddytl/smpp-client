package com;

import com.cloudhopper.commons.util.HexString;
import com.cloudhopper.commons.util.HexUtil;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
        SpringApplication.run(Application.class, args);
//        ArrayList<SmppSession> sessions = (ArrayList<SmppSession>) ctx.getBean("sessions");
//        List<Service> services = (ArrayList<Service>) ctx.getBean("services");
        /*String text = "Test #8: ISO/IEC-10646, e.mail@address 24579 % £ $ ¥ € ? ! λ ت ї Ӝ ױ";
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_16BE);
        byte[] textInHex = HexUtil.toHexString(textBytes).getBytes();
//[70, 69, 70, 70, 48, 48, 53, 52, 48, 48, 54, 53, 48, 48, 55, 51, 48, 48, 55, 52, 48, 48, 50, 48, 48, 48, 50, 51, 48, 48, 51, 56, 48, 48, 51, 65, 48, 48, 50, 48, 48, 48, 52, 57, 48, 48, 53, 51, 48, 48, 52, 70, 48, 48, 50, 70, 48, 48, 52, 57, 48, 48, 52, 53, 48, 48, 52, 51, 48, 48, 50, 68, 48, 48, 51, 49, 48, 48, 51, 48, 48, 48, 51, 54, 48, 48, 51, 52, 48, 48, 51, 54, 48, 48, 50, 67, 48, 48, 50, 48, 48, 48, 54, 53, 48, 48, 50, 69, 48, 48, 54, 68, 48, 48, 54, 49, 48, 48, 54, 57, 48, 48, 54, 67, 48, 48, 52, 48, 48, 48, 54, 49, 48, 48, 54, 52, 48, 48, 54, 52, 48, 48, 55, 50, 48, 48, 54, 53, 48, 48, 55, 51, 48, 48, 55, 51, 48, 48, 50, 48, 48, 48, 51, 50, 48, 48, 51, 52, 48, 48, 51, 53, 48, 48, 51, 55, 48, 48, 51, 57, 48, 48, 50, 48, 48, 48, 50, 53, 48, 48, 50, 48, 48, 48, 65, 51, 48, 48, 50, 48, 48, 48, 50, 52, 48, 48, 50, 48, 48, 48, 65, 53, 48, 48, 50, 48, 50, 48, 65, 67, 48, 48, 50, 48, 48, 48, 51, 70, 48, 48, 50, 48, 48, 48, 50, 49, 48, 48, 50, 48, 48, 51, 66, 66, 48, 48, 50, 48, 48, 54, 50, 65, 48, 48, 50, 48, 48, 52, 53, 55, 48, 48, 50, 48, 48, 52, 68, 67, 48, 48, 50, 48, 48, 53, 70, 49]
        System.out.println(HexUtil.toHexString(textBytes));
        System.out.println(Arrays.toString(textBytes));*/
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
