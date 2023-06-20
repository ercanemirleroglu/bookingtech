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
import org.openqa.selenium.NoSuchElementException;
import org.springframework.stereotype.Component;

import javax.money.Monetary;
import java.io.IOException;
import java.math.BigDecimal;
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

    public SearchResultExtDto fetchBookingData(Map<Param, String> params) throws IOException, InterruptedException {
        int tryCount = 1;
        boolean mustStart = true;
        while (tryCount <= 5) {
            try {
                return manageOperation(params, mustStart);
            } catch (Exception e) {
                //operation.screenShot();
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

    private SearchResultExtDto manageOperation(Map<Param, String> params, boolean mustStart) throws InterruptedException, IOException {
        List<HotelPriceExtDto> hotelPriceExtDtoList = new ArrayList<>();
        SearchCriteriaExtDto criteria = new SearchCriteriaExtDto();
        if (mustStart) operation.start("https://www.booking.com/");
        Map<String, Object> cap = operation.getCap();
        cap.forEach((k, v) -> log.info("key: {}, value: {}", k, v));
        closeRegisterModal();
        //changeLanguage(params.get(Param.APP_LANGUAGE));
        changeCurrency(params.get(Param.APP_CURRENCY_UNIT));
        criteria.setCurrency(params.get(Param.APP_CURRENCY_UNIT));

        criteria.setLocation(params.get(Param.SEARCH_LOCATION));
        enterLocation(criteria.getLocation());

        DateRange<LocalDate> localDateDateRange = enterDateByDayRange(params.get(Param.SEARCH_DATE_RANGE));
        criteria.setDateRange(localDateDateRange);

        List<CustomerSelectModel> customerSelectModels = CustomerSelectModel.toModel(params);
        criteria.setCustomerCounts(customerSelectModels);
        enterCustomerTypeAndCount(customerSelectModels);

        clickSearchButton();
        operation.timeout(20);
        scanHotelAndPriceDesktop(hotelPriceExtDtoList, params);
        /*if (DeviceType.DESKTOP.equals(operation.getDeviceType())) {
            scanHotelAndPriceDesktop(hotelPriceExtDtoList, params);
        } else if (DeviceType.MOBILE.equals(operation.getDeviceType())) {
            scanHotelAndPriceMobile(hotelPriceExtDtoList, params);
        }*/
        log.info("Total {} hotel and price fetched!", hotelPriceExtDtoList.size());
        printDtoOnLog(hotelPriceExtDtoList);
        operation.finish();
        return SearchResultExtDto.builder()
                .searchCriteria(criteria)
                .hotelPriceList(hotelPriceExtDtoList)
                .build();
    }

    private void scanHotelAndPriceMobile(List<HotelPriceExtDto> hotelPriceExtDtoList, Map<Param, String> params) throws InterruptedException, IOException {
        log.info(" >>>>>>> Scanning mobile screen...");
        //operation.screenShot();
        //operation.timeout(30);
        List<WebElement> hotelDivList = getHotelDivList();
        int page = 1;
        boolean hasNextBtn = true;
        while(hasNextBtn) {
            hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivList, page, params.get(Param.APP_CURRENCY_UNIT)));
            log.info("Fetching paginator...");
            Optional<WebElement> paginator = operation.findElementByCssSelector("div.a96794a348", ReturnAttitude.EMPTY);
            if (paginator.isPresent()) {
                WebElement webElement = paginator.get();
                log.info("Fetching found");
                log.info("Fetching next button...");
                List<WebElement> nextList = webElement.findElements(By.cssSelector("a.fc63351294.a822bdf511.d4b6b7a9e7.cfb238afa1.c334e6f658.f4605622ad"));
                if (!nextList.isEmpty()) {
                    WebElement nextBtn = nextList.get(0);
                    log.info("Next button found");
                    operation.click(nextBtn);
                    page++;
                    continue;
                }
                log.warn("Next button can not be found!");
            }
            log.warn("Paginator can not be found!");
            hasNextBtn = false;
        }
    }

    private void scanHotelAndPriceDesktop(List<HotelPriceExtDto> hotelPriceExtDtoList, Map<Param, String> params) {
        String hotelCountTitle = getHotelCountTitle();
        int hotelCountTotal = Integer.parseInt(returnJustDigits(hotelCountTitle));
        List<WebElement> hotelDivList = getHotelDivList();
        int hotelCountOnPage = hotelDivList.size();
        log.info("Hotel Div Count on this page: {}", hotelCountOnPage);
        if (hotelCountTotal > hotelCountOnPage) {
            int pageCount = calculatePageCount(hotelCountTotal, hotelCountOnPage);
            if (pageCount > 1) {
                hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivList, 1, params.get(Param.APP_CURRENCY_UNIT)));
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
                                    hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivListInside, finalCurrentPage, params.get(Param.APP_CURRENCY_UNIT)));
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
            hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivList, 1, params.get(Param.APP_CURRENCY_UNIT)));
        } else {
            hotelPriceExtDtoList.addAll(fetchDataFromPage(hotelDivList, 1, params.get(Param.APP_CURRENCY_UNIT)));
        }
    }

    private String getHotelCountTitle(){
        WebElement title = operation.findElementByCssSelector("[data-component='arp-header']", ReturnAttitude.ERROR)
                .map(e -> e.findElements(By.cssSelector("[aria-live='assertive']")).stream().findFirst()
                        .orElseGet(() -> {
                            log.error("Property count title not found!");
                            throw new NoSuchElementException("Property count title not found!");
                        })).orElseGet(() -> {
                    log.error("Property count title not found!");
                    throw new NoSuchElementException("Property count title not found!");
                });
        String text = title.getText();
        log.info(text);
        return text;
    }

    private int calculatePageCount(int hotelCountTotal, int hotelCountOnPage){
        int divide = hotelCountTotal / hotelCountOnPage;
        int mod = hotelCountTotal % hotelCountOnPage;
        return (mod > 0) ? (divide + 1) : divide;
    }

    private void openMobileHamburegerMenu() {
        log.info("In Hambureger Operations");
        Optional<WebElement> hamburgerButton = operation.findElementByCssSelector("[data-testid='header-mobile-menu-button']", ReturnAttitude.ERROR);
        operation.click(hamburgerButton);
        log.info("Hamburger Menu Opened!");
    }

    private void printDtoOnLog(List<HotelPriceExtDto> hotelPriceExtDtoList) {
        @SuppressWarnings("unchecked")
        JsonUtil<HotelPriceExtDto> jsonUtil = (JsonUtil<HotelPriceExtDto>) JsonUtil.getInstance();
        String json = jsonUtil.toJson(hotelPriceExtDtoList);
        log.info(json);
    }

    private List<WebElement> getHotelDivList() {
        String csPropertyCard = null;
        /*if(DeviceType.MOBILE.equals(operation.getDeviceType())) {
            csPropertyCard = "[data-testid='property-card-content']";
        } else if (DeviceType.DESKTOP.equals(operation.getDeviceType())) {
            csPropertyCard = "[data-testid='property-card']";
        }*/
        csPropertyCard = "[data-testid='property-card']";
        log.info("Hotel divs taking...");
;        List<WebElement> hotelDivList = operation.findElementsByCssSelector(csPropertyCard);
        if (hotelDivList.isEmpty()) {
            log.error("Hotels not found!");
            throw new NoSuchElementException("Hotel not found!");
        }
        return hotelDivList;
    }

    private List<HotelPriceExtDto> fetchDataFromPage(List<WebElement> hotelDivList, int page, String currency) {
        log.info("Page {} data fetching...", page);
        List<HotelPriceExtDto> hotelPriceExtDtoList = hotelDivList.stream().map(hotel ->
                HotelPriceExtDto.builder()
                    .hotelName(fetchAndSetHotelName(hotel))
                    .price(fetchAndSetPrice(hotel, currency))
                    .location(fetchAndSetLocation(hotel))
                    .rating(fetchAndSetRating(hotel))
                    .build()).collect(Collectors.toList());
        log.info("Total {} hotel and price fetched from page {}", hotelPriceExtDtoList.size(), page);
        printDtoOnLog(hotelPriceExtDtoList);
        return hotelPriceExtDtoList;
    }

    private void changeLanguage(String language) throws InterruptedException {
        log.info(" >>>>>>> Starting: To change language...");
        String csModalBtn = null;
        String csElmList = "[data-testid='selection-item']";
        String csSpan = "span.cf67405157";
        if(DeviceType.MOBILE.equals(operation.getDeviceType())) {
            openMobileHamburegerMenu();
            csModalBtn = "[data-testid='header-mobile-menu-language-picker-menu-item']";
        } else if (DeviceType.DESKTOP.equals(operation.getDeviceType())) {
            csModalBtn = "[data-testid='header-language-picker-trigger']";
        }
        Optional<WebElement> languageModalButton = operation.findElementByCssSelector(csModalBtn, ReturnAttitude.ERROR);
        operation.click(languageModalButton);
        List<WebElement> languageList = operation.findElementsByCssSelector(csElmList);
        if (languageList.isEmpty()) {
            log.error("Language List not found!");
            throw new NoSuchElementException("Language List not found!");
        }
        languageList.stream().flatMap(lang -> {
            List<WebElement> elm = lang.findElements(By.cssSelector(csSpan));
            if (!elm.isEmpty() && language.equalsIgnoreCase(elm.get(0).getText())) {
                return Stream.of(lang);
            }
            return Stream.empty();
        }).findFirst().ifPresent(operation::click);
    }

    private void changeCurrency(String currency) {
        log.info(" >>>>>>> Starting: To change currency...");
        String csModalBtn = null;
        String csElmList = "[data-testid='selection-item']";
        String csDiv = "div.ea1163d21f";
        if(DeviceType.MOBILE.equals(operation.getDeviceType())) {
            openMobileHamburegerMenu();
            csModalBtn = "[data-testid='header-mobile-menu-currency-picker-menu-item']";
        } else if (DeviceType.DESKTOP.equals(operation.getDeviceType())) {
            csModalBtn = "[data-testid='header-currency-picker-trigger']";
        }
        Optional<WebElement> currencyModalButton = operation.findElementByCssSelector(csModalBtn, ReturnAttitude.ERROR);
        operation.click(currencyModalButton);
        List<WebElement> currencyList = operation.findElementsByCssSelector(csElmList);
        if (currencyList.isEmpty()) {
            log.error("Currency List not found!");
            throw new NoSuchElementException("Currency List not found!");
        }
        currencyList.stream().flatMap(cur -> {
            List<WebElement> elm = cur.findElements(By.cssSelector(csDiv));
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

    private AppMoney fetchAndSetPrice(WebElement hotel, String currency) {
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
        Money money = Money.of(priceNum, Monetary.getCurrency(currency));
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
        log.info(" >>>>>>> Starting: To close register Modal...");
        for (int i = 2; i > 0; i--) {
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
        log.info(" <<<<<<< Finished: To close register Modal...");
    }

    private void enterLocation(String location) {
        log.info(" >>>>>>> Starting: To enter location...");
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
        goToDayOnCalendar(dateRange.getEndDate(), dates, formatter);
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
        log.info(" >>>>>>> Starting: To click Search Button...");
        String btn;
        Optional<WebElement> searchButton = Optional.empty();
        if(DeviceType.MOBILE.equals(operation.getDeviceType())) {
            btn = "submit_search";
            searchButton = operation.findElementById(btn);
        }
        if (searchButton.isEmpty()) {
            btn = "button.fc63351294.a822bdf511.d4b6b7a9e7.cfb238afa1.c938084447.f4605622ad.aa11d0d5cd";
            searchButton = operation.findElementByCssSelector(btn, ReturnAttitude.ERROR);
        }
        log.info("Clicking search button...");
        operation.click(searchButton);
        log.info("Clicked search button successfully");
    }
}
