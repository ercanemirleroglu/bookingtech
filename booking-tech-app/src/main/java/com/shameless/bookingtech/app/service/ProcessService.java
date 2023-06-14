package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.PriceModel;
import com.shameless.bookingtech.app.model.PriceReportModel;
import com.shameless.bookingtech.app.model.PriceStatus;
import com.shameless.bookingtech.common.util.JsonUtil;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.BookingResultDto;
import com.shameless.bookingtech.domain.dto.ParamDto;
import com.shameless.bookingtech.domain.dto.PriceDto;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.model.SearchCriteriaModel;
import com.shameless.bookingtech.domain.service.HotelApplicationService;
import com.shameless.bookingtech.domain.service.ParamService;
import com.shameless.bookingtech.integration.automation.model.HotelPriceExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import com.shameless.bookingtech.integration.automation.service.BookingService;
import jakarta.mail.MessagingException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProcessService {

    private final BookingService bookingService;
    private final HotelApplicationService hotelApplicationService;
    private final ParamService paramService;
    private final EmailService emailService;

    public ProcessService(BookingService bookingService, HotelApplicationService hotelApplicationService, ParamService paramService, EmailService emailService) {
        this.bookingService = bookingService;
        this.hotelApplicationService = hotelApplicationService;
        this.paramService = paramService;
        this.emailService = emailService;
    }

    //@Scheduled(cron = "0 39 10-22 * * ?")
    public void checkServices() {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        SearchResultExtDto searchResultExtDto = bookingService.fetchBookingData(params);
        hotelApplicationService.save(toDto(searchResultExtDto));
    }

    @Scheduled(cron = "0 53 9-21 * * ?")
    public void testService() throws IOException, MessagingException {
        Resource resource = new ClassPathResource("hotels.json");
        String filePath = resource.getFile().getAbsolutePath();
        SearchResultExtDto searchResultExtDto = (SearchResultExtDto) JsonUtil.getInstance().readFile(SearchResultExtDto.class, filePath, "hotels");
        List<PriceDto> priceDtoList = hotelApplicationService.save(toDto(searchResultExtDto));
        Map<PriceStatus, List<PriceModel>> groupsByPriceStatus = priceDtoList.stream().map(PriceModel::new)
                .filter(priceModel -> !PriceStatus.STATIC.equals(priceModel.getPriceStatus()))
                .collect(Collectors.groupingBy(PriceModel::getPriceStatus));
        List<PriceReportModel> priceReportModelList = groupsByPriceStatus.values().stream().map(PriceReportModel::new).collect(Collectors.toList());
        PriceEmailModel priceEmailModel = new PriceEmailModel(priceReportModelList, searchResultExtDto.getSearchCriteria());
        emailService.sendMail(priceEmailModel, "emailTemplate");
    }

    private BookingResultDto toDto(SearchResultExtDto searchResultExtDto) {
        return BookingResultDto.builder()
                .searchCriteria(mapSearchCriteria(searchResultExtDto.getSearchCriteria()))
                .hotelPriceList(mapHotelPriceList(searchResultExtDto.getHotelPriceList()))
                .build();
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
                .dateRange(searchCriteria.getDateRange())
                .build();
    }
}
