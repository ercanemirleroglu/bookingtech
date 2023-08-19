package com.shameless.bookingtech.app.rest.api;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.app.rest.RestResponse;
import com.shameless.bookingtech.app.rest.adapter.ReportAdapter;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportRestController {

    private final ReportAdapter reportAdapter;

    @GetMapping("/hourly")
    public ResponseEntity<PriceEmailModel> getHourlyReport() {
        PriceEmailModel hourlyReport = reportAdapter.getHourlyReport();
        return ResponseEntity.ok().body(hourlyReport);
    }

    @PostMapping("/hourly/send-mail")
    public ResponseEntity<Void> sendHourlyMail() throws MessagingException {
        reportAdapter.sendHourlyMail();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hourly/trigger")
    public ResponseEntity<RestResponse> triggerHourlyJob() {
        RestResponse response = reportAdapter.triggerHourlyJob();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/periodic")
    public ResponseEntity<PeriodicMailReport> getPeriodicReport() {
        PeriodicMailReport periodicReport = reportAdapter.getPeriodicReport();
        return ResponseEntity.ok().body(periodicReport);
    }

    @PostMapping("/periodic/send-mail")
    public ResponseEntity<Void> sendPeriodicMail() throws MessagingException {
        reportAdapter.sendPeriodicMail();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/periodic/trigger")
    public ResponseEntity<RestResponse> triggerPeriodicJob() {
        RestResponse response = reportAdapter.triggerPeriodicJob();
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
