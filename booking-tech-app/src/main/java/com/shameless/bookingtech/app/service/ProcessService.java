package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.PriceModel;
import com.shameless.bookingtech.app.model.PriceReportModel;
import com.shameless.bookingtech.app.model.PriceStatus;
import com.shameless.bookingtech.common.util.JsonUtil;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.BookingResultDto;
import com.shameless.bookingtech.domain.dto.ParamDto;
import com.shameless.bookingtech.domain.dto.PeriodicHotelPriceModel;
import com.shameless.bookingtech.domain.dto.PriceDto;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProcessService {

    private final BookingProviderImpl bookingProvider;
    private final HotelApplicationService hotelApplicationService;
    private final ParamService paramService;
    private final EmailService emailService;

    public ProcessService(BookingProviderImpl bookingProvider, HotelApplicationService hotelApplicationService, ParamService paramService, EmailService emailService) {
        this.bookingProvider = bookingProvider;
        this.hotelApplicationService = hotelApplicationService;
        this.paramService = paramService;
        this.emailService = emailService;
    }

    //@Scheduled(cron = "0 12 9-21 * * ?")
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void hourlyJob() throws MessagingException, InterruptedException, IOException {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, false);
        List<PriceDto> priceDtoList = hotelApplicationService.save(toDto(searchResultExtDto));
        Map<PriceStatus, List<PriceModel>> groupsByPriceStatus = priceDtoList.stream().map(PriceModel::new)
                .filter(priceModel -> !PriceStatus.STATIC.equals(priceModel.getPriceStatus()))
                .collect(Collectors.groupingBy(PriceModel::getPriceStatus));
        List<PriceReportModel> priceReportModelList = groupsByPriceStatus.values().stream().map(PriceReportModel::new).collect(Collectors.toList());
        PriceEmailModel priceEmailModel = new PriceEmailModel(priceReportModelList, searchResultExtDto);
        emailService.sendMail(priceEmailModel, params.get(Param.EMAIL_TO), "emailTemplate");
    }

    //@Scheduled(cron = "0 0 22 * * ?")
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void periodicJob() throws MessagingException, InterruptedException, IOException {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        SearchResultExtDto searchResultExtDto = bookingProvider.fetchBookingData(params, true);
        List<PriceDto> priceDtoList = hotelApplicationService.save(toDto(searchResultExtDto));
        System.out.println("Bitti");
    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    public void testService() throws IOException, MessagingException, InterruptedException {
        Map<Param, String> params = new HashMap<>();
        List<ParamDto> allParams = paramService.getAllParams();
        allParams.forEach(param -> params.put(param.getKey(), param.getValue()));
        Resource resource = new ClassPathResource("hotels.json");
        String filePath = resource.getFile().getAbsolutePath();
        SearchResultExtDto searchResultExtDto = (SearchResultExtDto) JsonUtil.getInstance().readFile(SearchResultExtDto.class, filePath, "hotels");
/*        List<PriceDto> priceDtoList = hotelApplicationService.save(toDto(searchResultExtDto));
        Map<PriceStatus, List<PriceModel>> groupsByPriceStatus = priceDtoList.stream().map(PriceModel::new)
                .filter(priceModel -> !PriceStatus.STATIC.equals(priceModel.getPriceStatus()))
                .collect(Collectors.groupingBy(PriceModel::getPriceStatus));
        List<PriceReportModel> priceReportModelList = groupsByPriceStatus.values().stream().map(PriceReportModel::new).collect(Collectors.toList());
        PriceEmailModel priceEmailModel = new PriceEmailModel(priceReportModelList, searchResultExtDto.getSearchCriteria());
        emailService.sendMail(priceEmailModel, params.get(Param.EMAIL_TO), "emailTemplate");*/
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
                .build();
    }
}
