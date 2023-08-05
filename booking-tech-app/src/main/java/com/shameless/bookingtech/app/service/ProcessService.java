package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.*;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.model.SearchCriteriaModel;
import com.shameless.bookingtech.domain.service.HotelApplicationService;
import com.shameless.bookingtech.domain.service.ParamService;
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
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProcessService {

    private final BookingProviderImpl bookingProvider;
    private final HotelApplicationService hotelApplicationService;
    private final ParamService paramService;
    private final EmailService emailService;
    private final ReportService reportService;
    private final MockService mockService;

    public ProcessService(BookingProviderImpl bookingProvider,
                          HotelApplicationService hotelApplicationService,
                          ParamService paramService, EmailService emailService,
                          ReportService reportService, MockService mockService) {
        this.bookingProvider = bookingProvider;
        this.hotelApplicationService = hotelApplicationService;
        this.paramService = paramService;
        this.emailService = emailService;
        this.reportService = reportService;
        this.mockService = mockService;
    }

    @Scheduled(cron = "0 0 10-15,17-22 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void hourlyJob() throws MessagingException, InterruptedException, IOException {
        Map<Param, String> params = getAllParamsMap();
        SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, false);
        hotelApplicationService.save(toDto(searchResultExtDto));
        PriceEmailModel hourlyReport = reportService.getHourlyReport();
        emailService.sendMail(hourlyReport, "emailTemplate");
    }

    @Scheduled(cron = "0 0 9,16 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void periodicJob() throws MessagingException, InterruptedException, IOException {
        Map<Param, String> params = getAllParamsMap();
        SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, true);
        hotelApplicationService.save(toDto(searchResultExtDto));
        PeriodicMailReport periodicReport = reportService.getPeriodicReport();
        emailService.sendMail(periodicReport, "periodicEmailTemplate");
    }

    @Scheduled(cron = "0 0 23,0-8 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void dontSleepJob() throws MalformedURLException, InterruptedException {
        Map<Param, String> params = getAllParamsMap();
        bookingProvider.dummyBrowser();
        log.info(params.toString());
    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void testHourly() throws MessagingException, IOException {
        SearchResultExtDto searchResultExtDto = mockService.createSearchResultExtDtoMock();
        hotelApplicationService.save(toDto(searchResultExtDto));
        PriceEmailModel hourlyReport = reportService.getHourlyReport();
        emailService.sendMail(hourlyReport, "emailTemplate");
    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void testPeriodic() throws IOException, MessagingException, InterruptedException {
        PeriodicMailReport periodicReport = reportService.getPeriodicReport();
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
