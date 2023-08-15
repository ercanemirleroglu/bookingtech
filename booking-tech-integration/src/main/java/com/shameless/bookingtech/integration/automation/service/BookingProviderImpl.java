package com.shameless.bookingtech.integration.automation.service;

import com.shameless.bookingtech.common.util.Constants;
import com.shameless.bookingtech.common.util.JsonUtil;
import com.shameless.bookingtech.common.util.model.AppMoney;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.integration.automation.infra.AppDriver;
import com.shameless.bookingtech.integration.automation.infra.AppDriverFactory;
import com.shameless.bookingtech.integration.automation.infra.AppElement;
import com.shameless.bookingtech.integration.automation.model.*;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.stereotype.Component;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.shameless.bookingtech.common.util.StringUtil.returnJustDigits;

@Component
@Slf4j
public class BookingProviderImpl {
    private final AppDriverFactory appDriverFactory;

    public BookingProviderImpl(AppDriverFactory appDriverFactory) {
        this.appDriverFactory = appDriverFactory;
    }

    public SearchResultExtDto fetchBookingData(Map<Param, String> params, boolean isPeriodic, LocalDate date) {
        return manageOperation(params, isPeriodic, date);
    }


    public void dummyBrowser() throws MalformedURLException, InterruptedException {
        AppDriver driver = appDriverFactory.createDriver("https://www.booking.com/");
        log.info("Dummy browser! Hey server, do not sleep!");
        driver.terminateDriver();
    }

    private SearchResultExtDto manageOperation(Map<Param, String> params, boolean isPeriodic, LocalDate date) {
        List<PeriodicResultExtDto> periodicResultExtDtoList = new ArrayList<>();
        SearchCriteriaExtDto criteria = new SearchCriteriaExtDto();
        criteria.setCurrency(params.get(Param.APP_CURRENCY_UNIT));
        criteria.setLocation(params.get(Param.SEARCH_LOCATION));
        List<CustomerSelectModel> customerSelectModels = CustomerSelectModel.toModel(params);
        criteria.setCustomerCounts(customerSelectModels);
        criteria.setDayRange(Integer.parseInt(params.get(Param.SEARCH_DATE_RANGE)));
        if (isPeriodic) {
            ExecutorService executor = Executors.newFixedThreadPool(Constants.CONCURRENT_SIZE);
            BookingProviderImpl obj = new BookingProviderImpl(appDriverFactory);
            for (int j = 0; j < Constants.CONCURRENT_COUNT; j++) {
                CountDownLatch innerLatch = new CountDownLatch(Constants.CONCURRENT_SIZE);
                for (int i = 0; i < Constants.CONCURRENT_SIZE; i++) {
                    int finalI = (j * Constants.CONCURRENT_SIZE) + i;
                    executor.execute(() -> {
                        try {
                            obj.bookingScreenAutomationProcess(params, periodicResultExtDtoList, customerSelectModels, date.plusDays(finalI));
                        } catch (Exception e) {
                            log.error("Error occurred: ", e);
                            throw new IllegalArgumentException("Error occurred!");
                        } finally {
                            innerLatch.countDown();
                        }
                    });
                }
                try {
                    innerLatch.await();
                } catch (InterruptedException e) {
                    log.error("Inner Latch Error: ", e);
                }
            }
            executor.shutdown();
        }
        else {
            bookingScreenAutomationProcess(params, periodicResultExtDtoList, customerSelectModels, date);
        }
        return SearchResultExtDto.builder()
                .searchCriteria(criteria)
                .periodicResultList(periodicResultExtDtoList)
                .build();
    }

    private void bookingScreenAutomationProcess(Map<Param, String> params, List<PeriodicResultExtDto> periodicResultExtDtoList, List<CustomerSelectModel> customerSelectModels, LocalDate start) {
        List<HotelPriceExtDto> hotelPriceExtDtoList = new ArrayList<>();
        AppDriver driver = null;
        DateRange<LocalDate> localDateDateRange;
        try {
            driver = startDriver(start);
            closeRegisterModal(driver);
            changeCurrency(driver, params.get(Param.APP_CURRENCY_UNIT));
            enterLocation(driver, params.get(Param.SEARCH_LOCATION));
            localDateDateRange = enterDateByDayRange(driver, params.get(Param.SEARCH_DATE_RANGE), start);
            enterCustomerTypeAndCount(driver, customerSelectModels);
            clickSearchButton(driver);
        }
         catch (Exception e) {
            log.error("Unexpected error when automation: ", e);
            if (driver != null)
                driver.terminateDriver();
            throw new IllegalArgumentException("Unexpected error when automation!");
        }
        try {
            scanHotelAndPriceDesktop(driver, hotelPriceExtDtoList, params);
        } catch (Exception e) {
            log.error("error occurred. But still continue: ", e);
        } finally {
            log.info("Total {} hotel and price fetched!", hotelPriceExtDtoList.size());
            printDtoOnLog(hotelPriceExtDtoList);
            PeriodicResultExtDto build = PeriodicResultExtDto.builder()
                    .hotelPriceList(hotelPriceExtDtoList)
                    .dateRange(localDateDateRange)
                    .build();
            periodicResultExtDtoList.add(build);
            driver.terminateDriver();
        }
    }

    private AppDriver startDriver(LocalDate start) {
        AppDriver driver;
        try {
            driver = appDriverFactory.createDriver("https://www.booking.com/");
        } catch (MalformedURLException | InterruptedException e) {
            throw new IllegalArgumentException("Error occurred when driver start: ", e);
        }
        log.info(" +++++ Start Driver : {}", driver.getId().toString());
        log.info(" +++++ Start Date : {}", start.toString());
        return driver;
    }

    private String getHotelCountTitle(AppDriver driver) {
        AppElement title = driver.findOneElementByCssSelector("[data-component='arp-header']", driver.javaScriptExecutor())
                .map(e -> e.findOneElementByCssSelector("[aria-live='assertive']", driver.javaScriptExecutor())
                        .orElseGet(() -> {
                            log.error("Property count title not found!");
                            throw new NoSuchElementException("Property count title not found!");
                        }))
                .orElseGet(() -> {
                    log.error("Arp header not found! You should control search button also!");
                    throw new NoSuchElementException("Arp header not found! You should control search button also!");
                });
        String text = title.getText();
        log.info(text);
        return text;
    }

    private int calculatePageCount(int hotelCountTotal, int hotelCountOnPage) {
        int divide = hotelCountTotal / hotelCountOnPage;
        int mod = hotelCountTotal % hotelCountOnPage;
        return (mod > 0) ? (divide + 1) : divide;
    }

    private String fetchAndSetHotelName(AppDriver driver, AppElement hotel) {
        log.info("Checking hotel name...");
        Optional<AppElement> titleList = hotel.findOneElementByCssSelector("[data-testid='title']", driver.javaScriptExecutor());
        if (titleList.isEmpty()) {
            log.error("Hotel title not found!");
            throw new NoSuchElementException("Hotel title not found!");
        }
        String hotelName = titleList.get().getText();
        log.info("hotel name fetched successful: {}", hotelName);
        return hotelName;
    }

    private List<HotelPriceExtDto> fetchDataFromPage(AppDriver driver, List<AppElement> hotelDivList, int page, String currency) {
        log.info("Page {} data fetching...", page);
        List<HotelPriceExtDto> hotelPriceExtDtoList = hotelDivList.stream().map(hotel ->
                HotelPriceExtDto.builder()
                        .hotelName(fetchAndSetHotelName(driver, hotel))
                        .price(fetchAndSetPrice(driver, hotel, currency))
                        .location(fetchAndSetLocation(driver, hotel))
                        .rating(fetchAndSetRating(driver, hotel))
                        .build()).collect(Collectors.toList());
        log.info("Total {} hotel and price fetched from page {}", hotelPriceExtDtoList.size(), page);
        printDtoOnLog(hotelPriceExtDtoList);
        return hotelPriceExtDtoList;
    }

    private void printDtoOnLog(List<HotelPriceExtDto> hotelPriceExtDtoList) {
        @SuppressWarnings("unchecked")
        JsonUtil<HotelPriceExtDto> jsonUtil = (JsonUtil<HotelPriceExtDto>) JsonUtil.getInstance();
        String json = jsonUtil.toJson(hotelPriceExtDtoList);
        log.info(json);
    }

    private Double fetchAndSetRating(AppDriver driver, AppElement hotel) {
        log.info("Checking Rating...");
        Optional<AppElement> reviewScore = hotel.findOneElementByCssSelector("[data-testid='review-score']", driver.javaScriptExecutor());
        if (reviewScore.isEmpty()) {
            log.warn("Rating div not found!");
            return null;
        }
        Optional<AppElement> ratingDiv = reviewScore.get().findOneElementByCssSelector("div.a3b8729ab1.d86cee9b25", driver.javaScriptExecutor());
        if (ratingDiv.isEmpty()) {
            log.warn("Ratings not found!");
            return null;
        }
        String rating = ratingDiv.get().getText();
        log.info("Rating fetched successful: {}", rating);
        return Double.parseDouble(rating);
    }

    private String fetchAndSetLocation(AppDriver driver, AppElement hotel) {
        log.info("Checking location...");
        Optional<AppElement> locationList = hotel.findOneElementByCssSelector("[data-testid='address']", driver.javaScriptExecutor());
        if (locationList.isEmpty()) {
            log.error("Location not found!");
            throw new NoSuchElementException("Location not found!");
        }
        String location = locationList.get().getText();
        log.info("Location fetched successful: {}", location);
        return location;
    }

    private AppMoney fetchAndSetPrice(AppDriver driver, AppElement hotel, String currency) {
        log.info("Checking price...");
        List<AppElement> priceList = hotel.findAllElementsByExecutor("[data-testid='price-and-discounted-price']", driver.javaScriptExecutor());
        if (priceList.isEmpty()) {
            log.error("Hotel price not found!");
            throw new NoSuchElementException("Hotel title not found!");
        }
        String price = priceList.get(0).getText();
        log.info("hotel price fetched successful: {}", price);
        String priceDigit = returnJustDigits(price);
        BigDecimal priceNum = BigDecimal.valueOf(Double.parseDouble(priceDigit));
        Money money = Money.of(priceNum, Monetary.getCurrency(currency));
        return new AppMoney(money);
    }

    private void scanHotelAndPriceDesktop(AppDriver driver, List<HotelPriceExtDto> hotelPriceExtDtoList, Map<Param, String> params) {
        String hotelCountTitle = getHotelCountTitle(driver);
        int hotelCountTotal = Integer.parseInt(returnJustDigits(hotelCountTitle));
        List<AppElement> hotelDivList = getHotelDivList(driver);
        int hotelCountOnPage = hotelDivList.size();
        log.info("Hotel Div Count on this page: {}", hotelCountOnPage);
        if (hotelCountTotal > hotelCountOnPage) {
            int pageCount = calculatePageCount(hotelCountTotal, hotelCountOnPage);
            if (pageCount > 1) {
                hotelPriceExtDtoList.addAll(fetchDataFromPage(driver, hotelDivList, 1, params.get(Param.APP_CURRENCY_UNIT)));
                for (int currentPage = 2; currentPage <= pageCount; currentPage++) {
                    Optional<AppElement> pagination = driver.findOneElementByCssSelector("[data-testid='pagination']", driver.javaScriptExecutor());
                    int finalCurrentPage = currentPage;
                    pagination.ifPresentOrElse(page -> {
                        page.findOneElementByCssSelector("[aria-label=' " + finalCurrentPage + "']", driver.javaScriptExecutor())
                                .ifPresentOrElse(button -> {
                                    log.info("Page button count {}", finalCurrentPage);
                                    button.click(driver.javaScriptExecutor());
                                    driver.timeout(10);
                                    List<AppElement> hotelDivListInside = getHotelDivList(driver);
                                    hotelPriceExtDtoList.addAll(fetchDataFromPage(driver, hotelDivListInside, finalCurrentPage, params.get(Param.APP_CURRENCY_UNIT)));
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
            hotelPriceExtDtoList.addAll(fetchDataFromPage(driver, hotelDivList, 1, params.get(Param.APP_CURRENCY_UNIT)));
        } else {
            hotelPriceExtDtoList.addAll(fetchDataFromPage(driver, hotelDivList, 1, params.get(Param.APP_CURRENCY_UNIT)));
        }
    }

    private List<AppElement> getHotelDivList(AppDriver driver) {
        String csPropertyCard = null;
        /*if(DeviceType.MOBILE.equals(operation.getDeviceType())) {
            csPropertyCard = "[data-testid='property-card-content']";
        } else if (DeviceType.DESKTOP.equals(operation.getDeviceType())) {
            csPropertyCard = "[data-testid='property-card']";
        }*/
        csPropertyCard = "[data-testid='property-card']";
        log.info("Hotel divs taking...");
        ;
        List<AppElement> hotelDivList = driver.findAllElementsByCssSelector(csPropertyCard, driver.javaScriptExecutor());
        if (hotelDivList.isEmpty()) {
            log.error("Hotels not found!");
            throw new NoSuchElementException("Hotel not found!");
        }
        return hotelDivList;
    }

    private void clickSearchButton(AppDriver driver) {
        log.info(" >>>>>>> Starting: To click Search Button...");
        String btn;
        Optional<AppElement> searchButton = Optional.empty();
        if (DeviceType.MOBILE.equals(driver.getDeviceType())) {
            btn = "submit_search";
            searchButton = driver.findElementById(btn);
        }
        if (searchButton.isEmpty()) {
            btn = "button.a83ed08757.c21c56c305.a4c1805887.f671049264.d2529514af.c082d89982.aa11d0d5cd";
            searchButton = driver.findOneElementByCssSelector(btn, driver.javaScriptExecutor());
        }
        log.info("Clicking search button...");
        searchButton.ifPresent(e -> e.click(driver.javaScriptExecutor()));
        log.info("Clicked search button successfully");
        driver.timeout(20);
    }

    private void enterCustomerTypeAndCount(AppDriver driver, List<CustomerSelectModel> customerSelectModels) {
        Optional<AppElement> elementByCssSelector = driver.findOneElementByCssSelector("div.d67edddcf0", driver.javaScriptExecutor());
        elementByCssSelector.ifPresent(e -> e.click(driver.javaScriptExecutor()));
        driver.timeout(3);
        Optional<AppElement> occupancyPopup = driver.findOneElementByCssSelector("[data-testid='occupancy-popup']", driver.javaScriptExecutor());
        if (occupancyPopup.isEmpty()) {
            log.error("Occupancy Popup not found!");
            throw new NoSuchElementException("Occupancy Popup not found!");
        }
        List<AppElement> customerTypeAndCountDivList = occupancyPopup.get()
                .findAllElementsByCssSelector("div.a7a72174b8", driver.javaScriptExecutor());
        outerLoop:
        for (AppElement customerTypeAndCountDiv : customerTypeAndCountDivList) {
            List<AppElement> customerTypeAndCountLabelList = customerTypeAndCountDiv.findAllElementsByCssSelector("label.a984a491d9", driver.javaScriptExecutor());
            if (customerTypeAndCountLabelList.isEmpty()) {
                log.error("customerTypeAndCount Label not found!");
                throw new NoSuchElementException("customerTypeAndCountDiv not found!");
            }
            log.info("customerTypeAndCount Label is available");
            String forGroup = customerTypeAndCountLabelList.get(0).getAttribute("for");
            for (CustomerSelectModel selectModel : customerSelectModels) {
                boolean isReturn = chooseCustomerTypeAndCount(driver, selectModel, forGroup, customerTypeAndCountDiv);
                if (isReturn) continue outerLoop;
            }
        }
    }

    private boolean chooseCustomerTypeAndCount(AppDriver driver, CustomerSelectModel selectModel, String forGroup, AppElement customerTypeAndCountDiv) {
        if (selectModel.getType().getForGroup().equals(forGroup)) {
            Optional<AppElement> customerCountSpanList = customerTypeAndCountDiv.findOneElementByCssSelector("span.d723d73d5f", driver.javaScriptExecutor());
            if (customerCountSpanList.isEmpty()) {
                log.error("customerCountSpan not found!");
                throw new NoSuchElementException("customerCountSpan not found!");
            }
            log.info("customerCountSpan is available");
            log.info("customerCountSpan text fetching...");
            String text = customerCountSpanList.get().getText();
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
                    pressButton(driver, customerTypeAndCountDiv, clickCount, ButtonType.MINUS);
                } else {
                    //plus
                    int clickCount = selectModel.getCount() - customerCount;
                    pressButton(driver, customerTypeAndCountDiv, clickCount, ButtonType.PLUS);
                }
            }
            return true;
        }
        return false;
    }

    private void pressButton(AppDriver driver, AppElement customerTypeAndCountDiv, int clickCount, ButtonType buttonType) {
        for (int i = 0; i < clickCount; i++) {
            Optional<AppElement> button = customerTypeAndCountDiv.findOneElementByCssSelector(buttonType.getSelector(), driver.javaScriptExecutor());
            if (button.isEmpty()) {
                log.error(buttonType.name() + " button not found!");
                throw new IllegalArgumentException(buttonType.name() + " button not found!");
            }
            button.ifPresent(e -> e.click(driver.javaScriptExecutor()));
            driver.timeout(1);
        }
    }

    private DateRange<LocalDate> enterDateByDayRange(AppDriver driver, String day, LocalDate start) {
        Optional<AppElement> dateRangeInput = driver.findOneElementByCssSelector("[data-testid='searchbox-dates-container']", driver.javaScriptExecutor());
        dateRangeInput.ifPresentOrElse(e -> {
            Optional<AppElement> calendar = Optional.empty();
            for (int i = 0; (i < 10 && (calendar.isEmpty() || !calendar.get().isDisplayed())); i++) {
                e.click(driver.javaScriptExecutor());
                driver.timeout(1);
                calendar = driver.findOneElementByCssSelector("[data-testid='datepicker-tabs']", driver.javaScriptExecutor());
            }
        }, () -> {
            log.error("Date Range Input not found!");
            throw new NoSuchElementException("Date Range Input not found!");
        });
        DateRange<LocalDate> dateRange = new DateRange<>(start, start.plusDays(Long.parseLong(day)));
        selectDateRangeFromCalendar(driver, dateRange);
        return dateRange;
    }

    private void selectDateRangeFromCalendar(AppDriver driver, DateRange<LocalDate> dateRange) {
        List<AppElement> dates = driver.findAllElementsByCssSelector("span.cf06f772fa", driver.javaScriptExecutor());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dates = goToDayOnCalendar(driver, dateRange.getStartDate(), dates, formatter);
        goToDayOnCalendar(driver, dateRange.getEndDate(), dates, formatter);
    }

    private List<AppElement> goToDayOnCalendar(AppDriver driver, LocalDate date, List<AppElement> dates, DateTimeFormatter formatter) {
        String today = date.format(formatter);
        int cnt = 0;
        boolean found = false;
        do {
            cnt++;
            if (cnt > 1)
                dates = driver.findAllElementsByCssSelector("span.cf06f772fa", driver.javaScriptExecutor());
            Optional<AppElement> dateOpt = dates.stream().filter(e -> today.equals(e.getAttribute("data-date"))).findFirst();
            if (dateOpt.isPresent()) {
                found = true;
                dateOpt.get().click(driver.javaScriptExecutor());
            } else {
                driver.findOneElementByCssSelector("button.a83ed08757.c21c56c305.f38b6daa18.d691166b09.f671049264.deab83296e.f4552b6561.dc72a8413c.f073249358",
                        driver.javaScriptExecutor()).ifPresentOrElse(nextBtn -> {
                    nextBtn.click(driver.javaScriptExecutor());
                    driver.timeout(1);
                }, () -> {
                    log.error("Next button not found in date Range Popup");
                    throw new NoSuchElementException("Next button not found in date Range Popup");
                });
            }
        } while (!found && cnt <=3);
        if (!found) {
            log.error("Date not found");
            throw new NoSuchElementException("Date not found!");
        }
        return dates;
    }

    private void enterLocation(AppDriver driver, String location) {
        log.info(" >>>>>>> Starting: To enter location...");
        Optional<AppElement> locationDiv = driver.findOneElementByCssSelector("div.a5761ae4af", driver.javaScriptExecutor());
        if (locationDiv.isEmpty()) {
            log.error("Location div not found!");
            throw new NoSuchElementException("Location div not found!");
        }
        Optional<AppElement> input = locationDiv.get().findOneElementByCssSelector("input", driver.javaScriptExecutor());
        if (input.isEmpty()) {
            log.error("Location input not found!");
            throw new NoSuchElementException("Location input not found!");
        }
        AppElement appElement = input.get();
        appElement.click(driver.javaScriptExecutor());
        appElement.sendKeys(location);
    }

    private void openMobileHamburegerMenu(AppDriver driver) {
        log.info("In Hambureger Operations");
        Optional<AppElement> hamburgerButton = driver.findOneElementByCssSelector("[data-testid='header-mobile-menu-button']", driver.javaScriptExecutor());
        hamburgerButton.ifPresent(e -> e.click(driver.javaScriptExecutor()));
        log.info("Hamburger Menu Opened!");
    }

    private void changeCurrency(AppDriver driver, String currency) {
        log.info(" >>>>>>> Starting: To change currency...");
        String csModalBtn = null;
        String csElmList = "[data-testid='selection-item']";
        String csDiv = "div.ea1163d21f";
        if (DeviceType.MOBILE.equals(driver.getDeviceType())) {
            openMobileHamburegerMenu(driver);
            csModalBtn = "[data-testid='header-mobile-menu-currency-picker-menu-item']";
        } else if (DeviceType.DESKTOP.equals(driver.getDeviceType())) {
            csModalBtn = "[data-testid='header-currency-picker-trigger']";
        }
        Optional<AppElement> currencyModalButton = driver.findOneElementByCssSelector(csModalBtn, driver.javaScriptExecutor());
        currencyModalButton.ifPresent(e -> e.click(driver.javaScriptExecutor()));
        List<AppElement> currencyList = driver.findAllElementsByCssSelector(csElmList, driver.javaScriptExecutor());
        if (currencyList.isEmpty()) {
            log.error("Currency List not found!");
            throw new NoSuchElementException("Currency List not found!");
        }
        currencyList.stream().flatMap(cur -> {
            List<AppElement> elm = cur.findAllElementsByCssSelector(csDiv, driver.javaScriptExecutor());
            if (!elm.isEmpty() && currency.equalsIgnoreCase(elm.get(0).getText())) {
                log.info("{} currency found successfully", currency);
                return Stream.of(cur);
            }
            return Stream.empty();
        }).findFirst().ifPresent(e -> e.click(driver.javaScriptExecutor()));
    }

    private void closeRegisterModal(AppDriver driver) {
        log.info(" >>>>>>> Starting: To close register Modal...");
        for (int i = 2; i > 0; i--) {
            log.info("Clicking close register modal x button...");
            Optional<AppElement> xButtonInRegisterModal = driver.findOneElementByCssSelector("button.a83ed08757.c21c56c305.f38b6daa18.d691166b09.ab98298258.deab83296e.f4552b6561", driver.javaScriptExecutor());
            if (xButtonInRegisterModal.isPresent()) {
                log.info("Close register modal x button found successfully...");
                xButtonInRegisterModal.get().click(driver.javaScriptExecutor());
                log.info("Clicked register modal close button found successfully...");
                break;
            }
            log.warn("Not found register modal close button still. Trying again...");
            driver.timeout(2);
        }
        log.info(" <<<<<<< Finished: To close register Modal...");
    }
}
