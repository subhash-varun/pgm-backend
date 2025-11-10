package com.varun.pgm.repository;

import com.varun.pgm.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    long countByStatus(Room.RoomStatus status);

    @Query("SELECT COUNT(r) FROM Room r")
    long countTotalRooms();
}
