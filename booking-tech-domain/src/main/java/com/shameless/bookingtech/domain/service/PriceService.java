package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.domain.dto.PeriodicHotelPriceModel;
import com.shameless.bookingtech.domain.dto.PriceDto;
import com.shameless.bookingtech.domain.dto.StoreTypeDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.PriceEntity;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import com.shameless.bookingtech.domain.entity.StoreType;
import com.shameless.bookingtech.domain.factory.PriceFactory;
import com.shameless.bookingtech.domain.mapper.PriceMapper;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.repository.HotelRepository;
import com.shameless.bookingtech.domain.repository.PriceRepository;
import com.shameless.bookingtech.domain.repository.SearchCriteriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriceService {
    private final PriceRepository priceRepository;
    private final SearchCriteriaRepository searchCriteriaRepository;
    private final HotelRepository hotelRepository;
    private final PriceFactory priceFactory;

    public PriceService(PriceRepository priceRepository, SearchCriteriaRepository searchCriteriaRepository, HotelRepository hotelRepository, PriceFactory priceFactory) {
        this.priceRepository = priceRepository;
        this.searchCriteriaRepository = searchCriteriaRepository;
        this.hotelRepository = hotelRepository;
        this.priceFactory = priceFactory;
    }

    public List<PriceDto> findAllForReport(Long scId, StoreTypeDto storeType, DateRange<LocalDate> dateRange){
        return priceRepository.findAllForReport(scId, StoreType.valueOf(storeType.name()),
                dateRange.getStartDate(), dateRange.getEndDate())
                .stream().map(PriceMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    private PriceDto addPrice(PriceDto priceDto, HotelEntity hotel, SearchCriteriaEntity searchCriteria) {
        PriceEntity from = priceFactory.from(priceDto, hotel, searchCriteria);
        PriceEntity save = priceRepository.save(from);
        return PriceMapper.INSTANCE.toDto(save);
    }

    private PriceDto updatePrice(PriceDto priceDto, PriceEntity oldPrice) {
        priceDto.setPreviousPrice(oldPrice.getCurrentValue());
        oldPrice.update(priceDto);
        PriceEntity save = priceRepository.save(oldPrice);
        return PriceMapper.INSTANCE.toDto(save);
    }

    public List<PriceDto> setAllPrices(List<PeriodicHotelPriceModel> periodicHotelPriceModelList, Long searchCriteriaId){
        SearchCriteriaEntity searchCriteriaEntity = searchCriteriaRepository.findById(searchCriteriaId)
                .orElseThrow(() -> new IllegalArgumentException("Not found search criteria! Id: "
                        + searchCriteriaId));
        List<PriceDto> priceDtoList = new ArrayList<>();
        StoreType storeType;
        if (Objects.nonNull(periodicHotelPriceModelList) && periodicHotelPriceModelList.size() == 1) {
            storeType = StoreType.HOURLY;
            StoreType finalStoreType = storeType;
            List<HotelPriceModel> hotelPriceList = periodicHotelPriceModelList.get(0).getHotelPriceList();
            DateRange<LocalDate> dateRange = periodicHotelPriceModelList.get(0).getDateRange();
            hotelPriceList.forEach(hotelPriceModel -> {
                HotelEntity hotelEntity = hotelRepository.findByName(hotelPriceModel.getHotelName())
                        .orElseThrow(() -> new IllegalArgumentException("Not found hotel! Name: "
                                + hotelPriceModel.getHotelName()));
                Optional<PriceEntity> priceOpt = priceRepository.findFirstByHotelAndSearchCriteriaAndStoreTypeOrderByLastModifiedDateDesc(hotelEntity, searchCriteriaEntity, finalStoreType);
                PriceDto priceDto = PriceDto.builder()
                        .currentPrice(hotelPriceModel.getPrice().getValue())
                        .fromDate(dateRange.getStartDate())
                        .toDate(dateRange.getEndDate())
                        .storeType(finalStoreType.toDto())
                        .build();
                if (priceOpt.isPresent()) {
                    PriceEntity oldPrice = priceOpt.get();
                    priceDtoList.add(updatePrice(priceDto, oldPrice));
                } else {
                    priceDtoList.add(addPrice(priceDto, hotelEntity, searchCriteriaEntity));
                }
            });
        }
        else if (Objects.nonNull(periodicHotelPriceModelList) && periodicHotelPriceModelList.size() > 1) {
            storeType = StoreType.PERIODIC;
            StoreType finalStoreType = storeType;
            periodicHotelPriceModelList.forEach(periodicHotelPriceModel -> {
                DateRange<LocalDate> dateRange = periodicHotelPriceModel.getDateRange();
                periodicHotelPriceModel.getHotelPriceList().forEach(hotelPriceModel -> {
                    HotelEntity hotelEntity = hotelRepository.findByName(hotelPriceModel.getHotelName())
                            .orElseThrow(() -> new IllegalArgumentException("Not found hotel! Name: "
                                    + hotelPriceModel.getHotelName()));
                    Optional<PriceEntity> priceOpt = priceRepository.findByDateRange(hotelEntity.getId(),
                            searchCriteriaEntity.getId(), dateRange.getStartDate(), dateRange.getEndDate());
                    PriceDto priceDto = PriceDto.builder()
                            .currentPrice(hotelPriceModel.getPrice().getValue())
                            .fromDate(dateRange.getStartDate())
                            .toDate(dateRange.getEndDate())
                            .storeType(finalStoreType.toDto())
                            .build();
                    if (priceOpt.isPresent()) {
                        PriceEntity oldPrice = priceOpt.get();
                        priceDtoList.add(updatePrice(priceDto, oldPrice));
                    } else {
                        priceDtoList.add(addPrice(priceDto, hotelEntity, searchCriteriaEntity));
                    }
                });
            });
        }
        return priceDtoList;
    }
}
