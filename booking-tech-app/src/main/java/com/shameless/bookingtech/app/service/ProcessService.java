package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.*;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.model.SearchCriteriaModel;
import com.shameless.bookingtech.domain.service.HotelApplicationService;
import com.shameless.bookingtech.domain.service.ParamService;
import com.shameless.bookingtech.domain.service.PriceService;
import com.shameless.bookingtech.integration.automation.model.HotelPriceExtDto;
import com.shameless.bookingtech.integration.automation.model.PeriodicResultExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import com.shameless.bookingtech.integration.automation.service.BookingProviderImpl;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProcessService {

    private final BookingProviderImpl bookingProvider;
    private final HotelApplicationService hotelApplicationService;
    private final ParamService paramService;
    private final EmailService emailService;
    private final ReportService reportService;
    private final PriceService priceService;

    public ProcessService(BookingProviderImpl bookingProvider, HotelApplicationService hotelApplicationService, ParamService paramService, EmailService emailService, ReportService reportService, PriceService priceService) {
        this.bookingProvider = bookingProvider;
        this.hotelApplicationService = hotelApplicationService;
        this.paramService = paramService;
        this.emailService = emailService;
        this.reportService = reportService;
        this.priceService = priceService;
    }

    @Scheduled(cron = "0 40 9-21 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void hourlyJob() throws MessagingException, InterruptedException, IOException {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, false);
        List<PriceDto> priceDtoList = hotelApplicationService.save(toDto(searchResultExtDto));
        PriceEmailModel hourlyReport = reportService.getHourlyReport(priceDtoList, searchResultExtDto);
        emailService.sendMail(hourlyReport, params.get(Param.EMAIL_TO), "emailTemplate");
    }

    @Scheduled(cron = "0 10 22 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void periodicJob() throws MessagingException, InterruptedException, IOException {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, true);
        List<PriceDto> priceDtoList = hotelApplicationService.save(toDto(searchResultExtDto));
        PeriodicMailReport periodicReport = reportService.getPeriodicReport(priceDtoList, searchResultExtDto.getSearchCriteria());
        emailService.sendMail(periodicReport, params.get(Param.EMAIL_TO), "periodicEmailTemplate");
    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void testHourly() throws IOException, MessagingException, InterruptedException {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        List<PriceDto> allForReport = priceService.findAllForReport(1L, StoreTypeDto.HOURLY,
                new DateRange<>(LocalDate.of(2023, 6, 26),
                        LocalDate.of(2023, 7, 4)));
        SearchCriteriaExtDto searchCriteria = SearchCriteriaExtDto.builder()
                .adult(2)
                .location("Norwich, United Kingdom")
                .child(0)
                .room(1)
                .currency("GBP")
                .dayRange(1)
                .build();
        PeriodicResultExtDto periodicResultExtDto = PeriodicResultExtDto.builder()
                .dateRange(new DateRange<>(LocalDate.of(2023, 6, 28),
                        LocalDate.of(2023, 6, 29)))
                .build();
        SearchResultExtDto searchResultExtDto = SearchResultExtDto.builder()
                .searchCriteria(searchCriteria)
                .periodicResultList(List.of(periodicResultExtDto))
                .build();
        PriceEmailModel hourlyReport = reportService.getHourlyReport(allForReport, searchResultExtDto);
        emailService.sendMail(hourlyReport, params.get(Param.EMAIL_TO), "emailTemplate");
    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void testPeriodic() throws IOException, MessagingException, InterruptedException {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        List<PriceDto> allForReport = priceService.findAllForReport(1L, StoreTypeDto.PERIODIC,
                new DateRange<>(LocalDate.of(2023, 6, 26),
                        LocalDate.of(2023, 7, 4)));
        SearchCriteriaExtDto searchCriteria = SearchCriteriaExtDto.builder()
                .adult(2)
                .location("Norwich, United Kingdom")
                .child(0)
                .room(1)
                .currency("GBP")
                .dayRange(1)
                .build();
        PeriodicMailReport periodicReport = reportService.getPeriodicReport(allForReport, searchCriteria);
        emailService.sendMail(periodicReport, params.get(Param.EMAIL_TO), "periodicEmailTemplate");
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
