package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.RoomRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.RoomResponse;
import com.varun.pgm.entity.Room;
import com.varun.pgm.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/rooms")
@Tag(name = "Rooms", description = "Room management endpoints")
@SecurityRequirement(name = "bearer-key")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    @Operation(summary = "Create a new room")
    @RequirePermission("ROOM_CREATE")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setRentAmount(request.getRentAmount());
        room.setStatus(request.getStatus());
        room.setFacilities(request.getFacilities());

        Room savedRoom = roomService.createRoom(room);
        RoomResponse response = new RoomResponse(
                savedRoom.getId(),
                savedRoom.getRoomNumber(),
                savedRoom.getRoomType(),
                savedRoom.getRentAmount(),
                savedRoom.getStatus(),
                savedRoom.getFacilities(),
                savedRoom.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Room created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all rooms with pagination")
    @RequirePermission("ROOM_READ")
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getAllRooms(Pageable pageable) {
        Page<Room> rooms = roomService.getAllRooms(pageable);
        Page<RoomResponse> response = rooms.map(room -> new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getRentAmount(),
                room.getStatus(),
                room.getFacilities(),
                room.getCreatedAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Rooms retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    @RequirePermission("ROOM_READ")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        RoomResponse response = new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getRentAmount(),
                room.getStatus(),
                room.getFacilities(),
                room.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Room retrieved successfully", response));
    }

    @GetMapping("/number/{roomNumber}")
    @Operation(summary = "Get room by room number")
    @RequirePermission("ROOM_READ")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomByRoomNumber(@PathVariable String roomNumber) {
        Room room = roomService.getRoomByRoomNumber(roomNumber)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        RoomResponse response = new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getRentAmount(),
                room.getStatus(),
                room.getFacilities(),
                room.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Room retrieved successfully", response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get rooms by status")
    @RequirePermission("ROOM_READ")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByStatus(@PathVariable Room.RoomStatus status) {
        List<Room> rooms = roomService.getRoomsByStatus(status);
        List<RoomResponse> response = rooms.stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getRentAmount(),
                        room.getStatus(),
                        room.getFacilities(),
                        room.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("success", "Rooms retrieved successfully", response));
    }

    @GetMapping("/type/{roomType}")
    @Operation(summary = "Get rooms by type")
    @RequirePermission("ROOM_READ")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByType(@PathVariable Room.RoomType roomType) {
        List<Room> rooms = roomService.getRoomsByType(roomType);
        List<RoomResponse> response = rooms.stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getRentAmount(),
                        room.getStatus(),
                        room.getFacilities(),
                        room.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("success", "Rooms retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room")
    @RequirePermission("ROOM_UPDATE")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        Room roomDetails = new Room();
        roomDetails.setRoomNumber(request.getRoomNumber());
        roomDetails.setRoomType(request.getRoomType());
        roomDetails.setRentAmount(request.getRentAmount());
        roomDetails.setStatus(request.getStatus());
        roomDetails.setFacilities(request.getFacilities());

        Room updatedRoom = roomService.updateRoom(id, roomDetails);
        RoomResponse response = new RoomResponse(
                updatedRoom.getId(),
                updatedRoom.getRoomNumber(),
                updatedRoom.getRoomType(),
                updatedRoom.getRentAmount(),
                updatedRoom.getStatus(),
                updatedRoom.getFacilities(),
                updatedRoom.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Room updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room")
    @RequirePermission("ROOM_DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Room deleted successfully", null));
    }
}
