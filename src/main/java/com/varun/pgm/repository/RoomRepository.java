package com.varun.pgm.repository;

import com.varun.pgm.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    long countByStatus(Room.RoomStatus status);

    @Query("SELECT COUNT(r) FROM Room r")
    long countTotalRooms();

    Optional<Room> findByRoomNumber(String roomNumber);
}
