package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.*;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.model.SearchCriteriaModel;
import com.shameless.bookingtech.domain.service.HotelApplicationService;
import com.shameless.bookingtech.domain.service.ParamService;
import com.shameless.bookingtech.domain.service.ReportService;
import com.shameless.bookingtech.integration.automation.model.HotelPriceExtDto;
import com.shameless.bookingtech.integration.automation.model.PeriodicResultExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import com.shameless.bookingtech.integration.automation.service.BookingProviderImpl;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProcessService {

    private final BookingProviderImpl bookingProvider;
    private final HotelApplicationService hotelApplicationService;
    private final ParamService paramService;
    private final EmailService emailService;
    private final ReportApplicationService reportApplicationService;
    private final MockService mockService;
    private final ReportService reportService;

    public ProcessService(BookingProviderImpl bookingProvider,
                          HotelApplicationService hotelApplicationService,
                          ParamService paramService, EmailService emailService,
                          ReportApplicationService reportApplicationService, MockService mockService, ReportService reportService) {
        this.bookingProvider = bookingProvider;
        this.hotelApplicationService = hotelApplicationService;
        this.paramService = paramService;
        this.emailService = emailService;
        this.reportApplicationService = reportApplicationService;
        this.mockService = mockService;
        this.reportService = reportService;
    }

    @Scheduled(cron = "0 0 10,11,13,14,16,17,19,20,22 * * ?")
    //@Scheduled(fixedRate = 5 * 60 * 1000)
    public void hourlyJob() {
        log.info("Hourly Job: it is starting...");
        try{
            log.info("Hourly Job: Params are fetching");
            Map<Param, String> params = getAllParamsMap();
            log.info("Hourly Job: Params fetched: {}", params);

            log.info("Hourly Job: Booking data is fetching...");
            SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, false, LocalDate.now());
            log.info("Hourly Job: Booking data fetched");

            log.info("Hourly Job: Booking data is saving...");
            hotelApplicationService.save(toDto(searchResultExtDto));
            log.info("Hourly Job: Booking data saved");

            log.info("Hourly Job: Report is generating...");
            PriceEmailModel hourlyReport = reportApplicationService.getHourlyReport();
            log.info("Hourly Job: report generated");

            log.info("Hourly Job: Mail is sending...");
            emailService.sendMail(hourlyReport, "emailTemplate");
            log.info("Hourly Job: Mail sent");
        } catch (Exception e) {
            log.error("Hourly Job: An error occurred: ", e);
        } finally {
            log.info("Hourly Job: It is finished");
        }

    }

    @Scheduled(cron = "0 0 9,12,15,18,21 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void periodicJob() {
        log.info("Periodic Job: it is starting...");
        try {
            log.info("Periodic Job: Params are fetching");
            Map<Param, String> params = getAllParamsMap();
            log.info("Periodic Job: Params fetched: {}", params);

            log.info("Periodic Job: Report Info and Date is fetching...");
            Optional<ReportDto> reportOpt = reportService.getReportByTypeAndDate(StoreTypeDto.PERIODIC, LocalDate.now());
            LocalDate date = reportOpt.map(reportDto -> reportDto.getLastPriceDay().plusDays(1)).orElseGet(LocalDate::now);
            log.info("Periodic Job: Report Info and Date fetched");

            log.info("Periodic Job: Booking data is fetching...");
            SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, true, date);
            log.info("Periodic Job: Booking data fetched");

            log.info("Periodic Job: Booking data is saving...");
            hotelApplicationService.save(toDto(searchResultExtDto));
            log.info("Periodic Job: Booking data saved");

            log.info("Periodic Job: Report is generating...");
            PeriodicMailReport periodicReport = reportApplicationService.getPeriodicReport();
            log.info("Periodic Job: report generated");

            log.info("Periodic Job: Mail is sending...");
            emailService.sendMail(periodicReport, "periodicEmailTemplate");
            log.info("Periodic Job: Mail sent");
        } catch (Exception e) {
            log.error("Periodic Job: An error occurred: ", e);
        } finally {
            log.info("Periodic Job: It is finished");
        }

    }

    @Scheduled(cron = "0 0 23,0-8 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void dontSleepJob() {
        log.info("Dummy Job: it is starting...");
        try{
            log.info("Dummy Job: Params are fetching");
            Map<Param, String> params = getAllParamsMap();
            log.info("Dummy Job: Params fetched: {}", params);

            log.info("Dummy Job: browser is opening...");
            bookingProvider.dummyBrowser();
            log.info("Dummy Job: browser opened and closed");
        }catch (Exception e) {
            log.error("Dummy Job: An error occurred: ", e);
        } finally {
            log.info("Dummy Job: It is finished");
        }

    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void testHourly() throws MessagingException, IOException {
        SearchResultExtDto searchResultExtDto = mockService.createSearchResultExtDtoMock();
        hotelApplicationService.save(toDto(searchResultExtDto));
        PriceEmailModel hourlyReport = reportApplicationService.getHourlyReport();
        emailService.sendMail(hourlyReport, "emailTemplate");
    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void testPeriodic() throws IOException, MessagingException, InterruptedException {
        PeriodicMailReport periodicReport = reportApplicationService.getPeriodicReport();
        emailService.sendMail(periodicReport, "periodicEmailTemplate");
    }

    private Map<Param, String> getAllParamsMap(){
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        return params;
    }

    private BookingResultDto toDto(SearchResultExtDto searchResultExtDto) {
        return BookingResultDto.builder()
                .searchCriteria(mapSearchCriteria(searchResultExtDto.getSearchCriteria()))
                .periodicHotelPriceList(mapPeriodicHotelPriceList(searchResultExtDto.getPeriodicResultList()))
                .build();
    }

    private List<PeriodicHotelPriceModel> mapPeriodicHotelPriceList(List<PeriodicResultExtDto> periodicResult) {
        return periodicResult.stream().map(perRes ->
                PeriodicHotelPriceModel.builder()
                        .hotelPriceList(mapHotelPriceList(perRes.getHotelPriceList()))
                        .dateRange(perRes.getDateRange())
                        .build()).collect(Collectors.toList());
    }

    private List<HotelPriceModel> mapHotelPriceList(List<HotelPriceExtDto> hotelPriceList) {
        return hotelPriceList.stream().map(hotelPriceExtDto ->
                HotelPriceModel.builder()
                        .hotelName(hotelPriceExtDto.getHotelName())
                        .price(hotelPriceExtDto.getPrice())
                        .location(hotelPriceExtDto.getLocation())
                        .rating(hotelPriceExtDto.getRating())
                        .build()).collect(Collectors.toList());
    }

    private SearchCriteriaModel mapSearchCriteria(SearchCriteriaExtDto searchCriteria) {
        return SearchCriteriaModel.builder()
                .adult(searchCriteria.getAdult())
                .child(searchCriteria.getChild())
                .room(searchCriteria.getRoom())
                .location(searchCriteria.getLocation())
                .currency(searchCriteria.getCurrency())
                .dayRange(searchCriteria.getDayRange())
                .build();
    }
}
