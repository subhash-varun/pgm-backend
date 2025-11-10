package com.varun.pgm.service;

import com.varun.pgm.entity.Room;
import com.varun.pgm.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public Room createRoom(Room room) {
        // Check if room number already exists
        if (roomRepository.findAll().stream().anyMatch(r -> r.getRoomNumber().equals(room.getRoomNumber()))) {
            throw new RuntimeException("Room number already exists");
        }
        room.setCreatedAt(LocalDateTime.now());
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Optional<Room> getRoomByRoomNumber(String roomNumber) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getRoomNumber().equals(roomNumber))
                .findFirst();
    }

    public List<Room> getRoomsByStatus(Room.RoomStatus status) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getStatus().equals(status))
                .toList();
    }

    public List<Room> getRoomsByType(Room.RoomType roomType) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getRoomType().equals(roomType))
                .toList();
    }

    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check if room number is being changed and if it already exists
        if (!room.getRoomNumber().equals(roomDetails.getRoomNumber()) &&
            roomRepository.findAll().stream().anyMatch(r -> r.getRoomNumber().equals(roomDetails.getRoomNumber()))) {
            throw new RuntimeException("Room number already exists");
        }

        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setRoomType(roomDetails.getRoomType());
        room.setRentAmount(roomDetails.getRentAmount());
        room.setStatus(roomDetails.getStatus());
        room.setFacilities(roomDetails.getFacilities());

        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        roomRepository.delete(room);
    }
}
