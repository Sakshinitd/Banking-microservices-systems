package com.banking.dto.response;

import com.banking.constant.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse implements Serializable {

    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String referenceId;

}
