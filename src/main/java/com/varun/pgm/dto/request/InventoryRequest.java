package com.varun.pgm.dto.request;

import com.varun.pgm.entity.Inventory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must be less than 100 characters")
    private String itemName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    @NotNull(message = "Condition status is required")
    private Inventory.ConditionStatus conditionStatus = Inventory.ConditionStatus.GOOD;
}
