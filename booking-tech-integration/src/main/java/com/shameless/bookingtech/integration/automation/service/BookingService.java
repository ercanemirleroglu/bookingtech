package com.shameless.bookingtech.integration.automation.service;

import com.shameless.bookingtech.common.util.JsonUtil;
import com.shameless.bookingtech.common.util.model.AppMoney;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.integration.automation.*;
import com.shameless.bookingtech.integration.automation.model.*;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.openqa.selenium.*;
import org.springframework.stereotype.Component;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.shameless.bookingtech.common.util.StringUtil.returnJustDigits;

@Component
@Slf4j
public class BookingService {
    private final AutomationOperation operation;

    public BookingService(AutomationOperation operation) {
        this.operation = operation;
    }

    public SearchResultExtDto fetchBookingData(Map<Param, String> params) {
        int tryCount = 1;
        boolean mustStart = true;
        while (tryCount <= 5) {
            try {
                return manageOperation(params, mustStart);
            } catch (Exception e) {
                log.error("Try count: " + tryCount + " Error message: " + e);
                tryCount++;
                if (e instanceof WebDriverException)
                    operation.finish();
                else {
                    mustStart = false;
                    operation.refreshPage("https://www.booking.com/");
                }
            }
        }
        throw new IllegalArgumentException("Oops! Automation is finished unsuccessfully!");
    }

    private SearchResultExtDto manageOperation(Map<Param, String> params, boolean mustStart) throws InterruptedException, MalformedURLException {
        List<HotelPriceExtDto> hotelPriceExtDtoList = new ArrayList<>();
        SearchCriteriaExtDto criteria = new SearchCriteriaExtDto();
        if (mustStart) operation.start("https://www.booking.com/");
        closeRegisterModal();
        changeLanguage(params.get(Param.APP_LANGUAGE));
        changeCurrency(params.get(Param.APP_CURRENCY_UNIT));

        criteria.setLocation(params.get(Param.SEARCH_LOCATION));
        enterLocation(criteria.getLocation());

        DateRange<LocalDate> localDateDateRange = enterDateByDayRange(params.get(Param.SEARCH_DATE_RANGE));
        criteria.setDateRange(localDateDateRange);

        List<CustomerSelectModel> customerSelectModels = CustomerSelectModel.toModel(params);
        criteria.setCustomerCounts(customerSelectModels);
        enterCustomerTypeAndCount(customerSelectModels);

        clickSearchButton();
        //operation.timeout(20);
        WebElement title = operation.findElementByCssSelector("[data-component='arp-header']", ReturnAttitude.ERROR)
                .map(e -> e.findElements(By.cssSelector("[aria-live='assertive']")).stream().findFirst()
                        .orElseGet(() -> {
                            log.error("Property count title not found!");
                            throw new NoSuchElementException("Property count title not found!");
                        })).orElseGet(() -> {
                    log.error("Property count title not found!");
                    throw new NoSuchElementException("Property count title not found!");
                });
        log.info(title.getText());
        int hotelCountTotal = Integer.parseInt(returnJustDigits(title.getText()));
        List<WebElement> hotelDivList = getHotelDivList();
        int hotelCountOnPage = hotelDivList.size();
        log.info("Hotel Div Count on this page: {}", hotelCountOnPage);
        if (hotelCountTotal > hotelCountOnPage) {
            int divide = hotelCountTotal / hotelCountOnPage;
            int mod = hotelCountTotal % hotelCountOnPage;
            int pageCount = (mod > 0) ? (divide + 1) : divide;
            if (pageCount > 1) {
                hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivList, 1));
                Optional<WebElement> pagination = operation.findElementByCssSelector("[data-testid='pagination']", ReturnAttitude.ERROR);
                for (int currentPage = 2; currentPage <= pageCount; currentPage++) {
                    int finalCurrentPage = currentPage;
                    pagination.ifPresentOrElse(e -> {
                        e.findElements(By.cssSelector("[aria-label=' " + finalCurrentPage + "'"))
                                .stream()
                                //.filter(e1 -> e1.getText().equals(String.valueOf(finalCurrentPage)))
                                .findFirst().ifPresentOrElse(button -> {
                                    log.info("Page button count {}", finalCurrentPage);
                                    operation.click(button);
                                    try {
                                        operation.timeout(10);
                                    } catch (InterruptedException ex) {
                                        log.warn("Timeout interrupted!");
                                    }
                                    List<WebElement> hotelDivListInside = getHotelDivList();
                                    hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivListInside, finalCurrentPage));
                                }, () -> {
                                    log.error("Pagination not found!");
                                    throw new NoSuchElementException("Pagination not found!");
                                });

                    }, () -> {
                        log.error("Pagination not found!");
                        throw new NoSuchElementException("Pagination not found!");
                    });

                }
            }
        } else if (hotelCountTotal < hotelCountOnPage) {
            hotelDivList = hotelDivList.stream().limit(hotelCountTotal).collect(Collectors.toList());
            hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivList, 1));
        } else {
            hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivList, 1));
        }
        log.info("Total {} hotel and price fetched!", hotelPriceExtDtoList.size());
        printDtoOnLog(hotelPriceExtDtoList);
        operation.finish();
        return SearchResultExtDto.builder()
                .searchCriteria(criteria)
                .hotelPriceList(hotelPriceExtDtoList)
                .build();
    }

    private void printDtoOnLog(List<HotelPriceExtDto> hotelPriceExtDtoList) {
        @SuppressWarnings("unchecked")
        JsonUtil<HotelPriceExtDto> jsonUtil = (JsonUtil<HotelPriceExtDto>) JsonUtil.getInstance();
        String json = jsonUtil.toJson(hotelPriceExtDtoList);
        log.info(json);
    }

    private List<WebElement> getHotelDivList() {
        log.info("Hotel divs taking...");
        List<WebElement> hotelDivList = operation.findElementsByCssSelector("[data-testid='property-card']");
        if (hotelDivList.isEmpty()) {
            log.error("Hotels not found!");
            throw new NoSuchElementException("Hotel not found!");
        }
        return hotelDivList;
    }

    private List<HotelPriceExtDto> fetchDataFromPage(List<WebElement> hotelDivList, int page) {
        log.info("Page {} data fetching...", page);
        List<HotelPriceExtDto> hotelPriceExtDtoList = hotelDivList.stream().map(hotel ->
                HotelPriceExtDto.builder()
                    .hotelName(fetchAndSetHotelName(hotel))
                    .price(fetchAndSetPrice(hotel))
                    .location(fetchAndSetLocation(hotel))
                    .rating(fetchAndSetRating(hotel))
                    .build()).collect(Collectors.toList());
        log.info("Total {} hotel and price fetched from page {}", hotelPriceExtDtoList.size(), page);
        printDtoOnLog(hotelPriceExtDtoList);
        return hotelPriceExtDtoList;
    }

    private void changeLanguage(String language) throws InterruptedException {
        Optional<WebElement> languageModalButton = operation.findElementByCssSelector("[data-testid='header-language-picker-trigger']", ReturnAttitude.ERROR);
        operation.click(languageModalButton);
        List<WebElement> languageList = operation.findElementsByCssSelector("[data-testid='selection-item']");
        if (languageList.isEmpty()) {
            log.error("Language List not found!");
            throw new NoSuchElementException("Language List not found!");
        }
        languageList.stream().flatMap(lang -> {
            List<WebElement> elm = lang.findElements(By.cssSelector("span.cf67405157"));
            if (!elm.isEmpty() && language.equalsIgnoreCase(elm.get(0).getText())) {
                return Stream.of(lang);
            }
            return Stream.empty();
        }).findFirst().ifPresent(operation::click);
    }

    private void changeCurrency(String currency) {
        Optional<WebElement> currencyModalButton = operation.findElementByCssSelector("[data-testid='header-currency-picker-trigger']", ReturnAttitude.ERROR);
        operation.click(currencyModalButton);
        List<WebElement> currencyList = operation.findElementsByCssSelector("[data-testid='selection-item']");
        if (currencyList.isEmpty()) {
            log.error("Currency List not found!");
            throw new NoSuchElementException("Currency List not found!");
        }
        currencyList.stream().flatMap(cur -> {
            List<WebElement> elm = cur.findElements(By.cssSelector("div.ea1163d21f"));
            if (!elm.isEmpty() && currency.equalsIgnoreCase(elm.get(0).getText())) {
                log.info("{} currency found successfully", currency);
                return Stream.of(cur);
            }
            return Stream.empty();
        }).findFirst().ifPresent(operation::click);
    }

    private void closeSelectionModal(String modalName) {
        operation.findElementByCssSelector("[data-testid='selection-modal-close']", ReturnAttitude.ERROR)
                .ifPresentOrElse(operation::click, () -> {
                    log.error("{} Modal Close Button not found!", modalName);
                    throw new NoSuchElementException(modalName + " Modal Close Button not found!");
                });
    }

    private AppMoney fetchAndSetPrice(WebElement hotel) {
        log.info("Checking price...");
        List<WebElement> priceList = operation.findElementsByExecutor(hotel, "[data-testid='price-and-discounted-price']");
        if (priceList.isEmpty()) {
            log.error("Hotel price not found!");
            throw new NoSuchElementException("Hotel title not found!");
        }
        String price = priceList.get(0).getText();
        log.info("hotel price fetched successful: {}", price);
        String priceDigit = returnJustDigits(price);
        BigDecimal priceNum = BigDecimal.valueOf(Double.parseDouble(priceDigit));
        Money money = Money.of(priceNum, Monetary.getCurrency("GBP"));
        return new AppMoney(money);
    }

    private String fetchAndSetHotelName(WebElement hotel) {
        log.info("Checking hotel name...");
        List<WebElement> titleList = hotel.findElements(By.cssSelector("[data-testid='title']"));
        if (titleList.isEmpty()) {
            log.error("Hotel title not found!");
            throw new NoSuchElementException("Hotel title not found!");
        }
        String hotelName = titleList.get(0).getText();
        log.info("hotel name fetched successful: {}", hotelName);
        return hotelName;
    }

    private Double fetchAndSetRating(WebElement hotel) {
        log.info("Checking Rating...");
        List<WebElement> ratingList = hotel.findElements(By.cssSelector("[data-testid='review-score']"));
        if (ratingList.isEmpty()) {
            log.warn("Rating div not found!");
            return null;
        }
        List<WebElement> ratings = ratingList.get(0).findElements(By.cssSelector("div.b5cd09854e.d10a6220b4"));
        if (ratings.isEmpty()) {
            log.warn("Ratings not found!");
            return null;
        }
        String rating = ratings.get(0).getText();
        log.info("Rating fetched successful: {}", rating);
        return Double.parseDouble(rating);
    }

    private String fetchAndSetLocation(WebElement hotel) {
        log.info("Checking location...");
        List<WebElement> locationList = hotel.findElements(By.cssSelector("[data-testid='address']"));
        if (locationList.isEmpty()) {
            log.error("Location not found!");
            throw new NoSuchElementException("Location not found!");
        }
        String location = locationList.get(0).getText();
        log.info("Location fetched successful: {}", location);
        return location;
    }

    private void closeRegisterModal() throws InterruptedException {
        for (int i = 10; i > 0; i--) {
            log.info("Clicking close register modal x button...");
            List<WebElement> xButtonInRegisterModal = operation.findElementsByCssSelector("button.fc63351294.a822bdf511.e3c025e003.fa565176a8.f7db01295e.c334e6f658.ae1678b153");
            if (xButtonInRegisterModal.size() == 1) {
                log.info("Close register modal x button found successfully...");
                operation.click(xButtonInRegisterModal.get(0));
                log.info("Clicked register modal close button found successfully...");
                break;
            }
            log.warn("Not found register modal close button still. Trying again...");
            operation.timeout(2);
        }
    }

    private void enterLocation(String location) {
        Optional<WebElement> locationInput = operation.findElementById(":Ra9:");
        operation.click(locationInput);
        operation.sendKeys(location, locationInput);
    }

    private DateRange<LocalDate> enterDateByDayRange(String day) throws InterruptedException {

        Optional<WebElement> dateRangeInput = operation.findElementByCssSelector("[data-testid='searchbox-dates-container']", ReturnAttitude.ERROR);
        dateRangeInput.ifPresentOrElse(e -> {
            Optional<WebElement> calendar = Optional.empty();
            for (int i = 0; (i < 10 && (calendar.isEmpty() || !calendar.get().isDisplayed())); i++) {
                operation.click(e);
                try {
                    operation.timeout(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                calendar = operation.findElementByCssSelector("[data-testid='datepicker-tabs']", ReturnAttitude.EMPTY);
            }


        }, () -> {
            log.error("Hata");
            throw new NoSuchElementException("Hata");
        });
        LocalDate today = LocalDate.now();
        DateRange<LocalDate> dateRange = new DateRange<>(today, today.plusDays(Long.parseLong(day)));
        selectDateRangeFromCalendar(dateRange);
        return dateRange;
    }

    private void selectDateRangeFromCalendar(DateRange<LocalDate> dateRange) throws InterruptedException {
        List<WebElement> dates = operation.findElementsByCssSelector("span.b21c1c6c83");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        goToDayOnCalendar(dateRange.getStartDate(), dates, formatter);
        //goToDayOnCalendar(plusDays, dates, formatter);
    }

    private void goToDayOnCalendar(LocalDate date, List<WebElement> dates, DateTimeFormatter formatter) {
        String today = date.format(formatter);
        dates.stream()
                .filter(e -> today.equals(e.getAttribute("data-date"))).findFirst()
                .ifPresentOrElse(operation::click, () -> {
                    log.error("Date not found");
                    throw new NoSuchElementException("Date not found!");
                });
    }

    private void enterCustomerTypeAndCount(List<CustomerSelectModel> customerSelectModels) throws InterruptedException {
        Optional<WebElement> elementByCssSelector = operation.findElementByCssSelector("div.d67edddcf0", ReturnAttitude.ERROR);
        operation.click(elementByCssSelector);
        List<WebElement> customerTypeAndCountDivList = operation.findElementsByCssSelector("div.b2b5147b20");
        outerLoop:
        for (WebElement customerTypeAndCountDiv : customerTypeAndCountDivList) {
            List<WebElement> customerTypeAndCountLabelList = customerTypeAndCountDiv.findElements(By.cssSelector("label.a68a7ee8ee"));
            if (customerTypeAndCountLabelList.isEmpty()) {
                log.error("customerTypeAndCount Label not found!");
                throw new NoSuchElementException("customerTypeAndCountDiv not found!");
            }
            log.info("customerTypeAndCount Label is available");
            String forGroup = customerTypeAndCountLabelList.get(0).getAttribute("for");
            for (CustomerSelectModel selectModel : customerSelectModels) {
                boolean isReturn = chooseCustomerTypeAndCount(selectModel, forGroup, customerTypeAndCountDiv);
                if (isReturn) continue outerLoop;
            }
        }

    }

    private boolean chooseCustomerTypeAndCount(CustomerSelectModel selectModel, String forGroup, WebElement customerTypeAndCountDiv) throws InterruptedException {
        if (selectModel.getType().getForGroup().equals(forGroup)) {
            List<WebElement> customerCountSpanList = customerTypeAndCountDiv.findElements(By.cssSelector("span.e615eb5e43"));
            if (customerCountSpanList.isEmpty()) {
                log.error("customerCountSpan not found!");
                throw new NoSuchElementException("customerCountSpan not found!");
            }
            log.info("customerCountSpan is available");
            log.info("customerCountSpan text fetching...");
            String text = customerCountSpanList.get(0).getText();
            log.info("customerCountSpan text is {}", text);
            if (text == null) {
                log.error("customerCountSpan text is empty!");
                throw new IllegalArgumentException("customerCountSpan text is empty!");
            }
            int customerCount = Integer.parseInt(text);
            if (customerCount != selectModel.getCount()) {
                if (customerCount > selectModel.getCount()) {
                    //minus
                    int clickCount = customerCount - selectModel.getCount();
                    pressButton(customerTypeAndCountDiv, clickCount, ButtonType.MINUS);
                } else {
                    //plus
                    int clickCount = selectModel.getCount() - customerCount;
                    pressButton(customerTypeAndCountDiv, clickCount, ButtonType.PLUS);
                }
            }
            return true;
        }
        return false;
    }

    private void pressButton(WebElement customerTypeAndCountDiv, int clickCount, ButtonType buttonType) throws InterruptedException {
        for (int i = 0; i < clickCount; i++) {
            List<WebElement> button = customerTypeAndCountDiv.findElements(By.cssSelector(buttonType.getSelector()));
            if (button.isEmpty()) {
                log.error(buttonType.name() + " button not found!");
                throw new IllegalArgumentException(buttonType.name() + " button not found!");
            }
            operation.click(button.get(0));
            operation.timeout(1);
        }
    }

    private void clickSearchButton() {
        log.info("Clicking search button...");
        Optional<WebElement> searchButton = operation.findElementByCssSelector("button.fc63351294.a822bdf511.d4b6b7a9e7.cfb238afa1.c938084447.f4605622ad.aa11d0d5cd", ReturnAttitude.ERROR);
        operation.click(searchButton);
        log.info("Clicked search button successfully");
    }
}
