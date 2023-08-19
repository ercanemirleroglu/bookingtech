package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.domain.dto.TriggerTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReportJobExecutorTask {

    private final ReportApplicationService reportApplicationService;

    public ReportJobExecutorTask(ReportApplicationService reportApplicationService) {
        this.reportApplicationService = reportApplicationService;
    }

    @Scheduled(cron = "0 0 10,11,13,14,16,17,19,20,22 * * ?")
    //@Scheduled(fixedRate = 5 * 60 * 1000)
    public void hourlyJob() {
        reportApplicationService.triggerHourlyJob(TriggerTypeDto.SYSTEM);
    }

    @Scheduled(cron = "0 0 9,12,15,18,21 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void periodicJob() {
        reportApplicationService.triggerPeriodicJob(TriggerTypeDto.SYSTEM);
    }

    @Scheduled(cron = "0 0 23,0-8 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void dontSleepJob() {
        reportApplicationService.triggerDummyJob();
    }

/*    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void testHourly() throws MessagingException, IOException {
        SearchResultExtDto searchResultExtDto = mockService.createSearchResultExtDtoMock();
        hotelApplicationService.save(toDto(searchResultExtDto));
        PriceEmailModel hourlyReport = reportApplicationService.getHourlyReport();
        emailService.sendMail(hourlyReport, "emailTemplate");
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void testPeriodic() throws IOException, MessagingException, InterruptedException {
        PeriodicMailReport periodicReport = reportApplicationService.getPeriodicReport();
        emailService.sendMail(periodicReport, "periodicEmailTemplate");
    }*/
}
