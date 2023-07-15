package com.shameless.bookingtech.app.api;

import com.shameless.bookingtech.app.model.PriceEmailModel;
import com.shameless.bookingtech.app.model.periodic.PeriodicMailReport;
import com.shameless.bookingtech.app.service.EmailService;
import com.shameless.bookingtech.app.service.ReportService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportRestController {

    private final ReportService reportService;
    private final EmailService emailService;

    @GetMapping("/hourly")
    public ResponseEntity<PriceEmailModel> getHourlyReport() {
        PriceEmailModel hourlyReport = reportService.getHourlyReport();
        return ResponseEntity.ok().body(hourlyReport);
    }

    @PostMapping("/hourly/send-mail")
    public ResponseEntity<Void> sendHourlyMail() throws MessagingException {
        PriceEmailModel hourlyReport = reportService.getHourlyReport();
        emailService.sendMail(hourlyReport, "emailTemplate");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/periodic")
    public ResponseEntity<PeriodicMailReport> getPeriodicReport() {
        PeriodicMailReport periodicReport = reportService.getPeriodicReport();
        return ResponseEntity.ok().body(periodicReport);
    }

    @PostMapping("/periodic/send-mail")
    public ResponseEntity<Void> sendPeriodicMail() throws MessagingException {
        PeriodicMailReport periodicReport = reportService.getPeriodicReport();
        emailService.sendMail(periodicReport, "periodicEmailTemplate");
        return ResponseEntity.ok().build();
    }
}
