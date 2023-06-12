package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.PriceDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.PriceEntity;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import com.shameless.bookingtech.domain.factory.PriceFactory;
import com.shameless.bookingtech.domain.mapper.PriceMapper;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.repository.HotelRepository;
import com.shameless.bookingtech.domain.repository.PriceRepository;
import com.shameless.bookingtech.domain.repository.SearchCriteriaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private PriceDto addPrice(PriceDto priceDto, HotelEntity hotel, SearchCriteriaEntity searchCriteria) {
        PriceEntity from = priceFactory.from(priceDto, hotel, searchCriteria);
        PriceEntity save = priceRepository.save(from);
        return PriceMapper.INSTANCE.toDto(save);
    }

    private PriceDto updatePrice(PriceDto priceDto, PriceEntity oldPrice) {
        priceDto.setPreviousPrice(oldPrice.getCurrentValue());
        priceDto.setPreviousCurrency(oldPrice.getCurrentCurrency());
        oldPrice.update(priceDto);
        PriceEntity save = priceRepository.save(oldPrice);
        return PriceMapper.INSTANCE.toDto(save);
    }

    public List<PriceDto> setAllPrices(List<HotelPriceModel> hotelPriceModelList, Long searchCriteriaId){
        SearchCriteriaEntity searchCriteriaEntity = searchCriteriaRepository.findById(searchCriteriaId)
                .orElseThrow(() -> new IllegalArgumentException("Not found search criteria! Id: "
                        + searchCriteriaId));
        List<PriceDto> priceDtoList = new ArrayList<>();
        hotelPriceModelList.forEach(hotelPriceModel -> {
            HotelEntity hotelEntity = hotelRepository.findByName(hotelPriceModel.getHotelName())
                    .orElseThrow(() -> new IllegalArgumentException("Not found hotel! Name: "
                            + hotelPriceModel.getHotelName()));
            Optional<PriceEntity> priceOpt = priceRepository.findFirstByHotelAndSearchCriteriaOrderByLastModifiedDateDesc(hotelEntity, searchCriteriaEntity);
            PriceDto priceDto = PriceDto.builder()
                    .currentCurrency(hotelPriceModel.getPrice().getCurrency())
                    .currentPrice(hotelPriceModel.getPrice().getValue())
                    .build();
            if (priceOpt.isPresent()) {
                PriceEntity oldPrice = priceOpt.get();
                priceDtoList.add(updatePrice(priceDto, oldPrice));
            } else {
                priceDtoList.add(addPrice(priceDto, hotelEntity, searchCriteriaEntity));
            }
        });
        return priceDtoList;
    }
}
