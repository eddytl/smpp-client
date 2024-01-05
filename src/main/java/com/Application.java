package com;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.nexah.models.Setting;
import com.nexah.repositories.SettingRepository;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication
@EntityScan
@EnableAsync
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    @Autowired
    private SmppSession session;
    @Autowired
    private Service service;
    @Autowired
    private SmppSMSService smppSMSService;

    static public void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        Service service = (Service) ctx.getBean("service");
        SmppSession session = (SmppSession) ctx.getBean("session");
    }

    @Bean(name = "service")
    public Service services(SettingRepository settingRepository) {
        Setting setting = settingRepository.findAll().get(0);
        service = new Service();
        service.setName(setting.getServiceName());
        service.setHost(setting.getHost());
        service.setPort(setting.getPort());
        service.setUsername(setting.getUsername());
        service.setPassword(setting.getPassword());
        service.setBound(false);
        return service;
    }

    @Bean(name = "session")
    public SmppSession session(Service service, SmppSMSService smppSMSService) {
        if (!service.getBound()) {
            session = smppSMSService.bindSession(session, service);
        }
        return session;
    }

    @Scheduled(initialDelayString = "${sms.async.initial-delay}", fixedDelayString = "${sms.async.initial-delay}")
    void enquireLinkJob() {
        try {
            if (session.isBound()) {
                try {
                    session.enquireLink(new EnquireLink(), 20000);
                } catch (SmppTimeoutException | SmppChannelException e) {
                    log.info(session.getConfiguration().getName() + " Enquire link failed, executing reconnect; " + e);
                    session.close();
                } catch (InterruptedException e) {
                    log.info(session.getConfiguration().getName() + " Enquire link interrupted, probably killed by reconnecting");
                    session.close();
                } catch (Exception e) {
                    log.error(session.getConfiguration().getName() + " Enquire link failed, executing reconnect", e);
                    session.close();
                }
            } else {
                log.error(session.getConfiguration().getName() + " enquire link running while session is not connected");
            }
        } catch (Exception $e) {
            log.error($e.getMessage());
        }

    }

    @Scheduled(initialDelayString = "${sms.async.rebind-delay}", fixedDelayString = "${sms.async.rebind-delay}")
    void rebindFailSmppJob() {
        try {
            if (!smppSMSService.isBound(session, service)) {
                smppSMSService.rebindSession(session, service);
                log.error("session rebind success !");
            }else{
                log.error("session is already in bound state !");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
