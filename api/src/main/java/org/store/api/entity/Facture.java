package org.store.api.entity;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facture {
    private Long id;
    private Long commandId;
    private Long userId;
    private String invoiceDate;
    private Double totalAmount;
    private String status;
    private List<LineCommand> lines;  // Added to store order details
}