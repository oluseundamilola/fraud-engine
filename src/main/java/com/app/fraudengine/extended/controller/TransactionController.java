package com.app.fraudengine.extended.controller;


import com.app.fraudengine.extended.DTOs.PagedTransactionResponse;
import com.app.fraudengine.extended.service.PoliceService;
import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(
    origins = "http://localhost:5173"
)
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final PoliceService policeService;

    @GetMapping(value = "/api/user/transactions/{accountNumber}")
    public List<TransactionDTO>  getTransactionsByFromAccount(@PathVariable String accountNumber){
        return policeService.getTransactionsByFromAccount(accountNumber);
    }

    @GetMapping("/api/user/transactions/search")
    public PagedTransactionResponse searchTransactions(
        @RequestParam(required = false) String accountNumber,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,

        @RequestParam(required = false) String status,
        @RequestParam(required = false) String color,
        @RequestParam(required = false) String transactionType,

        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TransactionDTO> result = policeService.searchTransactions(
            accountNumber,
            date,
            status,
            color,
            transactionType,
            pageable
        );

        List<TransactionDTO> content = result.getContent();

        long greenCount = content.stream()
            .filter(t -> "green".equalsIgnoreCase(t.getColor().name()))
            .count();

        long yellowCount = content.stream()
            .filter(t -> "yellow".equalsIgnoreCase(t.getColor().name()))
            .count();

        long orangeCount = content.stream()
            .filter(t -> "orange".equalsIgnoreCase(t.getColor().name()))
            .count();

        long redCount = content.stream()
            .filter(t -> "red".equalsIgnoreCase(t.getColor().name()))
            .count();

        PagedTransactionResponse response = new PagedTransactionResponse(result);

        response.setNumberOfGreen(greenCount);
        response.setNumberOfYellow(yellowCount);
        response.setNumberOfOrange(orangeCount);
        response.setNumberOfRed(redCount);

        return response;
    }
}
