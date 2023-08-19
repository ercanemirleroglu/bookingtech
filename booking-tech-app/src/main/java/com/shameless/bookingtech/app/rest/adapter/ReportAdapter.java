package com.shameless.bookingtech.app.rest.adapter;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.app.rest.RestResponse;
import com.shameless.bookingtech.app.service.EmailService;
import com.shameless.bookingtech.app.service.ReportApplicationService;
import com.shameless.bookingtech.common.exception.JobNotStartedException;
import com.shameless.bookingtech.domain.dto.TriggerTypeDto;
import com.shameless.bookingtech.domain.service.JobService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class ReportAdapter {

    private final ReportApplicationService reportApplicationService;
    private final JobService jobService;
    private final EmailService emailService;

    public synchronized RestResponse triggerHourlyJob() {
        try {
            jobService.start("Hourly Job", TriggerTypeDto.MANUAL);
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.submit(() -> reportApplicationService.triggerHourlyJob(TriggerTypeDto.MANUAL));
            executorService.shutdown();
            return RestResponse.builder()
                    .code(200)
                    .message("Job has been triggered successfully")
                    .build();
        } catch (JobNotStartedException e) {
            return RestResponse.builder()
                    .code(406)
                    .message("Job has been already triggered before! Please wait the moment when will be finished!")
                    .build();
        }
    }

    public RestResponse triggerPeriodicJob() {
        try {
            jobService.start("Periodic Job", TriggerTypeDto.MANUAL);
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.submit(() -> reportApplicationService.triggerPeriodicJob(TriggerTypeDto.MANUAL));
            executorService.shutdown();
            return RestResponse.builder()
                    .code(200)
                    .message("Job has been triggered successfully")
                    .build();
        } catch (JobNotStartedException e) {
            return RestResponse.builder()
                    .code(406)
                    .message("Job has been already triggered before! Please wait the moment when will be finished!")
                    .build();
        }
    }

    public void sendHourlyMail() throws MessagingException {
        PriceEmailModel hourlyReport = reportApplicationService.getHourlyReport();
        emailService.sendMail(hourlyReport, "emailTemplate");
    }

    public void sendPeriodicMail() throws MessagingException {
        PeriodicMailReport periodicReport = reportApplicationService.getPeriodicReport();
        emailService.sendMail(periodicReport, "periodicEmailTemplate");
    }

    public PriceEmailModel getHourlyReport() {
        return reportApplicationService.getHourlyReport();
    }

    public PeriodicMailReport getPeriodicReport() {
        return reportApplicationService.getPeriodicReport();
    }
}
