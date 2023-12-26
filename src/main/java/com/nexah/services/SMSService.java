package com.nexah.services;

import com.cloudhopper.smpp.SmppSession;
import com.nexah.http.rest.PostSMS;
import com.nexah.models.Message;
import com.nexah.models.Setting;
import com.nexah.repositories.MessageRepository;
import com.nexah.utils.Constant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;



@Service
public class SMSService {

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    SmppSession session;
    @Autowired
    SmppSMSService smppSMSService;
    @Async
    public void sendsms(MultipartFile file, String sender, String message, String traffic, String campaignId, String dlrUrl, Setting setting) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            // Now let's iterate over the columns of the current row
            int cellIdx = 0;
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String mobileno = Double.toString(cell.getNumericCellValue());
                BigDecimal bigDecimalValue = new BigDecimal(mobileno);
                String msisdn = bigDecimalValue.toPlainString();
                if (cellIdx == 0) {
                    if (msisdn.length() == Constant.MSISDN_LENGTH) {
                        Message msg = new Message();
                        msg.setMsisdn(msisdn);
                        msg.setSender(sender);
                        msg.setCampaignId(campaignId);
                        msg.setMessage(message);
                        msg.setTraffic(traffic);
                        msg.setStatus(Constant.SMS_CREATED);
                        msg.setRetry(0);
                        msg.setDlrUrl(dlrUrl);
                        msg.setDlrIsSent(false);
                        msg.setCreatedAt(new Date());
                        msg.setUpdatedAt(new Date());
                        messageRepository.save(msg);

                        if (session.isBound()) {
                            if (session.getConfiguration().getName().equals(traffic)) {
                                PostSMS.sendsms(smppSMSService, session, msg, setting);
                            }
                        }
                    }
                }
                cellIdx++;
            }
        }
        workbook.close();
    }
}
