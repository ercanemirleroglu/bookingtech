package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.app.mapper.BookingResultMapper;
import com.shameless.bookingtech.app.model.*;
import com.shameless.bookingtech.app.model.periodic.HotelPricePeriodicModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.app.model.periodic.PriceByDateRangeModel;
import com.shameless.bookingtech.app.model.periodic.PricesByDateRange;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.*;
import com.shameless.bookingtech.domain.service.*;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import com.shameless.bookingtech.integration.automation.service.BookingProviderImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReportApplicationService {

    private final SearchCriteriaService searchCriteriaService;
    private final PriceService priceService;
    private final ReportService reportService;
    private final BookingProviderImpl bookingProvider;
    private final ParamService paramService;
    private final HotelApplicationService hotelApplicationService;
    private final EmailService emailService;
    private final JobService jobService;

    public ReportApplicationService(SearchCriteriaService searchCriteriaService,
                                    PriceService priceService,
                                    ReportService reportService, BookingProviderImpl bookingProvider, ParamService paramService, HotelApplicationService hotelApplicationService, EmailService emailService, JobService jobService) {
        this.searchCriteriaService = searchCriteriaService;
        this.priceService = priceService;
        this.reportService = reportService;
        this.bookingProvider = bookingProvider;
        this.paramService = paramService;
        this.hotelApplicationService = hotelApplicationService;
        this.emailService = emailService;
        this.jobService = jobService;
    }

    public void triggerHourlyJob(TriggerTypeDto triggerType) {
        log.info("Hourly Job: it is starting...");
        String jobName = "Hourly Job";
        if (TriggerTypeDto.SYSTEM.equals(triggerType))
            jobService.start(jobName, TriggerTypeDto.SYSTEM);
        try{
            log.info("Hourly Job: Params are fetching");
            Map<Param, String> params = getAllParamsMap();
            log.info("Hourly Job: Params fetched: {}", params);

            log.info("Hourly Job: Booking data is fetching...");
            SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, false, LocalDate.now());
            log.info("Hourly Job: Booking data fetched");

            log.info("Hourly Job: Booking data is saving...");
            hotelApplicationService.save(BookingResultMapper.INSTANCE.toDto(searchResultExtDto));
            log.info("Hourly Job: Booking data saved");

            log.info("Hourly Job: Report is generating...");
            PriceEmailModel hourlyReport = getHourlyReport();
            log.info("Hourly Job: report generated");

            log.info("Hourly Job: Mail is sending...");
            emailService.sendMail(hourlyReport, "emailTemplate");
            log.info("Hourly Job: Mail sent");
            jobService.finish(jobName);
        } catch (Exception e) {
            log.error("Hourly Job: An error occurred: ", e);
            jobService.error(jobName, e.getMessage());
        } finally {
            log.info("Hourly Job: It is finished");
        }
    }

    public void triggerPeriodicJob(TriggerTypeDto triggerType) {
        log.info("Periodic Job: it is starting...");
        String jobName = "Periodic Job";
        if (TriggerTypeDto.SYSTEM.equals(triggerType))
            jobService.start(jobName, TriggerTypeDto.SYSTEM);
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
            hotelApplicationService.save(BookingResultMapper.INSTANCE.toDto(searchResultExtDto));
            log.info("Periodic Job: Booking data saved");

            log.info("Periodic Job: Report is generating...");
            PeriodicMailReport periodicReport = getPeriodicReport();
            log.info("Periodic Job: report generated");

            log.info("Periodic Job: Mail is sending...");
            emailService.sendMail(periodicReport, "periodicEmailTemplate");
            log.info("Periodic Job: Mail sent");
            jobService.finish(jobName);
        } catch (Exception e) {
            log.error("Periodic Job: An error occurred: ", e);
            jobService.error(jobName, e.getMessage());
        } finally {
            log.info("Periodic Job: It is finished");
        }
    }

    public void triggerDummyJob() {
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

    private Map<Param, String> getAllParamsMap(){
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        return params;
    }

    public PriceEmailModel getHourlyReport() {
        SearchCriteriaDto criteriaByParams = searchCriteriaService.getCriteriaByParams();
        ReportDto report = reportService.getReportByType(StoreTypeDto.HOURLY);
        List<PriceDto> priceDtoList = priceService.findAllByReportDateTime(criteriaByParams.getId(),
                StoreTypeDto.HOURLY, report.getLastReportDate());
        DateRange<LocalDate> dateRange = new DateRange<>(report.getFromDate(),
                report.getToDate());
        Map<PriceStatus, List<PriceModel>> groupsByPriceStatus = priceDtoList.stream().map(PriceModel::new)
                .filter(priceModel -> !PriceStatus.STATIC.equals(priceModel.getPriceStatus()))
                .collect(Collectors.groupingBy(PriceModel::getPriceStatus));
        List<PriceReportModel> priceReportModelList = groupsByPriceStatus.values().stream()
                .map(PriceReportModel::new).collect(Collectors.toList());
        return new PriceEmailModel(priceReportModelList, criteriaByParams,
                dateRange, report.getLastReportDate());
    }

    public PeriodicMailReport getPeriodicReport() {
        SearchCriteriaDto criteriaByParams = searchCriteriaService.getCriteriaByParams();
        ReportDto report = reportService.getReportByType(StoreTypeDto.PERIODIC);
        List<PriceDto> priceDtoList = priceService.findAllByReportDateTime(criteriaByParams.getId(),
                StoreTypeDto.PERIODIC, report.getLastReportDate());
        List<PricesByDateRange> pricesByDateRangeList = getPricesByDateRange(priceDtoList);
        List<String> hotelNames = getHotelNames(pricesByDateRangeList);
        List<DateRange<LocalDate>> dateRanges = getDateRanges(pricesByDateRangeList);
        PeriodicMailReport periodicMailReport = new PeriodicMailReport();
        periodicMailReport.setEmailParam(new EmailParamModel(criteriaByParams,
                new DateRange<>(dateRanges.get(0).getStartDate(),
                        dateRanges.get(dateRanges.size() - 1).getEndDate()),
                report.getLastReportDate()));
        periodicMailReport.setColumns(dateRanges);
        periodicMailReport.setRows(Lists.newArrayList());
        hotelNames.forEach(hotel -> {
            HotelPricePeriodicModel hotelPricePeriodicModel = new HotelPricePeriodicModel();
            hotelPricePeriodicModel.setHotelName(hotel);
            hotelPricePeriodicModel.setPrices(Lists.newArrayList());
            dateRanges.forEach(dr -> {
                PriceByDateRangeModel priceByDateRangeModel = new PriceByDateRangeModel();
                Optional<PriceModel> first = pricesByDateRangeList.stream().filter(p -> dr.equals(p.getDateRange()))
                        .flatMap(p -> p.getPrices().stream())
                        .filter(p -> hotel.equals(p.getHotelName()))
                        .findFirst();
                PriceModel priceModel;
                if (first.isEmpty()) {
                    priceModel = new PriceModel(hotel);
                } else {
                    priceModel = first.get();
                }
                priceByDateRangeModel.setDateRange(dr);
                priceByDateRangeModel.setPrice(priceModel);
                hotelPricePeriodicModel.getPrices().add(priceByDateRangeModel);
            });
            periodicMailReport.getRows().add(hotelPricePeriodicModel);
        });
        return periodicMailReport;
    }

    private List<DateRange<LocalDate>> getDateRanges(List<PricesByDateRange> pricesByDateRangeList) {
        return pricesByDateRangeList.stream().map(PricesByDateRange::getDateRange)
                .distinct()
                .sorted(Comparator.comparing(DateRange::getStartDate))
                .collect(Collectors.toList());
    }

    private List<String> getHotelNames(List<PricesByDateRange> pricesByDateRangeList) {
        return pricesByDateRangeList.stream().flatMap(p -> p.getPrices().stream())
                .map(PriceModel::getHotelName).distinct().sorted().collect(Collectors.toList());
    }

    private List<PricesByDateRange> getPricesByDateRange(List<PriceDto> priceDtoList) {
        Map<DateRange<LocalDate>, List<PriceDto>> priceMapByDate = priceDtoList.stream().collect(Collectors.groupingBy(price ->
                new DateRange<>(price.getFromDate(), price.getToDate())));
        return priceMapByDate.entrySet().stream().map(e -> {
            List<PriceModel> priceModelList = e.getValue().stream().map(PriceModel::new)
                    .collect(Collectors.toList());
            return PricesByDateRange.builder()
                    .dateRange(e.getKey())
                    .prices(priceModelList)
                    .build();
        }).collect(Collectors.toList());
    }
}
