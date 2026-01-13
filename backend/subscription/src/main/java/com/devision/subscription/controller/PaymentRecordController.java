package com.devision.subscription.controller;

import com.devision.subscription.dto.PaymentRecordDTO;
import com.devision.subscription.model.PaymentTransaction;
import com.devision.subscription.service.PaymentRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-records")
public class PaymentRecordController {

    private static final Logger log = LoggerFactory.getLogger(PaymentRecordController.class);
    private final PaymentRecordService service;

    public PaymentRecordController(PaymentRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> receivePaymentRecord(
            @RequestBody PaymentRecordDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // TODO: validate JWT token from JM (when available)
            if (dto.getSubsystem() == null || !"JOB_APPLICANT".equals(dto.getSubsystem())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid subsystem. Expected JOB_APPLICANT"));
            }

            PaymentTransaction saved = service.saveFromCallback(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Payment record received and saved",
                    "id", saved.getId(),
                    "transactionId", saved.getId()));
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid payment record: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Error processing payment record", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Failed to process payment record"));
        }
    }

    @GetMapping("/applicant/{applicantId}")
    public List<PaymentTransaction> getPayments(@PathVariable String applicantId) {
        return service.getApplicantPayments(applicantId);
    }
}
