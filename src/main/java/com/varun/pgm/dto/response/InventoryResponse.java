package com.varun.pgm.dto.response;

import com.varun.pgm.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private Long id;
    private Long roomId;
    private String roomNumber;
    private String itemName;
    private Integer quantity;
    private Inventory.ConditionStatus conditionStatus;
    private LocalDateTime lastUpdated;
}
