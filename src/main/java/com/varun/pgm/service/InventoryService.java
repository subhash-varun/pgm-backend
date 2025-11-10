package com.varun.pgm.service;

import com.varun.pgm.entity.Inventory;
import com.varun.pgm.entity.Room;
import com.varun.pgm.repository.InventoryRepository;
import com.varun.pgm.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Inventory createInventory(Inventory inventory) {
        // Validate room exists
        if (inventory.getRoom() != null && inventory.getRoom().getId() != null) {
            Room room = roomRepository.findById(inventory.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            inventory.setRoom(room);
        }

        inventory.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Page<Inventory> getAllInventory(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    public Optional<Inventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    public List<Inventory> getInventoryByRoomId(Long roomId) {
        return inventoryRepository.findByRoomId(roomId);
    }

    public List<Inventory> getInventoryByConditionStatus(Inventory.ConditionStatus conditionStatus) {
        return inventoryRepository.findAll().stream()
                .filter(inventory -> inventory.getConditionStatus().equals(conditionStatus))
                .toList();
    }

    public List<Inventory> getInventoryByItemName(String itemName) {
        return inventoryRepository.findAll().stream()
                .filter(inventory -> itemName.equalsIgnoreCase(inventory.getItemName()))
                .toList();
    }

    public Inventory updateInventory(Long id, Inventory inventoryDetails) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        // Update room if provided
        if (inventoryDetails.getRoom() != null && inventoryDetails.getRoom().getId() != null) {
            Room room = roomRepository.findById(inventoryDetails.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            inventory.setRoom(room);
        }

        inventory.setItemName(inventoryDetails.getItemName());
        inventory.setQuantity(inventoryDetails.getQuantity());
        inventory.setConditionStatus(inventoryDetails.getConditionStatus());
        inventory.setLastUpdated(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));
        inventoryRepository.delete(inventory);
    }
}
