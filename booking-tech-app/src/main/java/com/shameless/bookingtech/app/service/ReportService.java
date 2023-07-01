package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.app.model.*;
import com.shameless.bookingtech.app.model.periodic.HotelPricePeriodicModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.app.model.periodic.PriceByDateRangeModel;
import com.shameless.bookingtech.app.model.periodic.PricesByDateRange;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.domain.dto.PriceDto;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReportService {

    public PriceEmailModel getHourlyReport(List<PriceDto> priceDtoList, SearchResultExtDto searchResultExtDto) {
        Map<PriceStatus, List<PriceModel>> groupsByPriceStatus = priceDtoList.stream().map(PriceModel::new)
                .filter(priceModel -> !PriceStatus.STATIC.equals(priceModel.getPriceStatus()))
                .collect(Collectors.groupingBy(PriceModel::getPriceStatus));
        List<PriceReportModel> priceReportModelList = groupsByPriceStatus.values().stream().map(PriceReportModel::new).collect(Collectors.toList());
        return new PriceEmailModel(priceReportModelList, searchResultExtDto);
    }

    public PeriodicMailReport getPeriodicReport(List<PriceDto> priceDtoList, SearchCriteriaExtDto searchCriteria) {
        List<PricesByDateRange> pricesByDateRangeList = getPricesByDateRange(priceDtoList);
        List<String> hotelNames = getHotelNames(pricesByDateRangeList);
        List<DateRange<LocalDate>> dateRanges = getDateRanges(pricesByDateRangeList);
        PeriodicMailReport periodicMailReport = new PeriodicMailReport();
        periodicMailReport.setEmailParam(new EmailParamModel(searchCriteria,
                new DateRange<>(dateRanges.get(0).getStartDate(),
                        dateRanges.get(dateRanges.size() - 1).getEndDate())));
        periodicMailReport.setColumns(dateRanges);
        periodicMailReport.setRows(Lists.newArrayList());
        hotelNames.forEach(hotel -> {
            HotelPricePeriodicModel hotelPricePeriodicModel = new HotelPricePeriodicModel();
            hotelPricePeriodicModel.setHotelName(hotel);
            hotelPricePeriodicModel.setPrices(Lists.newArrayList());
            dateRanges.forEach(dateRange -> {
                PriceByDateRangeModel priceByDateRangeModel = new PriceByDateRangeModel();
                Optional<PriceModel> first = pricesByDateRangeList.stream().filter(p -> dateRange.equals(p.getDateRange()))
                        .flatMap(p -> p.getPrices().stream())
                        .filter(p -> hotel.equals(p.getHotelName()))
                        .findFirst();
                PriceModel priceModel;
                if (first.isEmpty()) {
                    priceModel = new PriceModel(hotel);
                } else {
                    priceModel = first.get();
                }
                priceByDateRangeModel.setDateRange(dateRange);
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
