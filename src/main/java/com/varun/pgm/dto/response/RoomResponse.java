package com.varun.pgm.dto.response;

import com.varun.pgm.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String roomNumber;
    private Room.RoomType roomType;
    private BigDecimal rentAmount;
    private Room.RoomStatus status;
    private String facilities;
    private LocalDateTime createdAt;
}
