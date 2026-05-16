package com.app.fraudengine.extended.DTOs;

import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedTransactionResponse {

    private List<TransactionDTO> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean last;

    private boolean first;

    private long numberOfGreen;
    private long numberOfYellow;
    private long numberOfOrange;
    private long numberOfRed;


    public PagedTransactionResponse() {}

    public PagedTransactionResponse(Page<TransactionDTO> pageData) {

        this.content = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalElements = pageData.getTotalElements();
        this.totalPages = pageData.getTotalPages();
        this.last = pageData.isLast();
        this.first = pageData.isFirst();
    }

    // getters and setters
}
