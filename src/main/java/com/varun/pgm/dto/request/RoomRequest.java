package com.varun.pgm.dto.request;

import com.varun.pgm.entity.Room;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 20, message = "Room number must be less than 20 characters")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private Room.RoomType roomType;

    @NotNull(message = "Rent amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent amount must be greater than 0")
    private BigDecimal rentAmount;

    private Room.RoomStatus status = Room.RoomStatus.AVAILABLE;

    private String facilities;
}
