package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.InventoryRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.InventoryResponse;
import com.varun.pgm.entity.Inventory;
import com.varun.pgm.entity.Room;
import com.varun.pgm.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
@Tag(name = "Inventory", description = "Inventory management endpoints")
@SecurityRequirement(name = "bearer-key")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    @Operation(summary = "Create a new inventory item")
    @RequirePermission("INVENTORY_CREATE")
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(@Valid @RequestBody InventoryRequest request) {
        Inventory inventory = new Inventory();
        // Set room relationship
        if (request.getRoomId() != null) {
            Room room = new Room();
            room.setId(request.getRoomId());
            inventory.setRoom(room);
        }
        inventory.setItemName(request.getItemName());
        inventory.setQuantity(request.getQuantity());
        inventory.setConditionStatus(request.getConditionStatus());

        Inventory savedInventory = inventoryService.createInventory(inventory);
        InventoryResponse response = new InventoryResponse(
                savedInventory.getId(),
                savedInventory.getRoom() != null ? savedInventory.getRoom().getId() : null,
                savedInventory.getRoom() != null ? savedInventory.getRoom().getRoomNumber() : null,
                savedInventory.getItemName(),
                savedInventory.getQuantity(),
                savedInventory.getConditionStatus(),
                savedInventory.getLastUpdated()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory item created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all inventory items with pagination")
    @RequirePermission("INVENTORY_READ")
    public ResponseEntity<ApiResponse<Page<InventoryResponse>>> getAllInventory(Pageable pageable) {
        Page<Inventory> inventory = inventoryService.getAllInventory(pageable);
        Page<InventoryResponse> response = inventory.map(item -> new InventoryResponse(
                item.getId(),
                item.getRoom() != null ? item.getRoom().getId() : null,
                item.getRoom() != null ? item.getRoom().getRoomNumber() : null,
                item.getItemName(),
                item.getQuantity(),
                item.getConditionStatus(),
                item.getLastUpdated()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item by ID")
    @RequirePermission("INVENTORY_READ")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryById(@PathVariable Long id) {
        Inventory inventory = inventoryService.getInventoryById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));
        InventoryResponse response = new InventoryResponse(
                inventory.getId(),
                inventory.getRoom() != null ? inventory.getRoom().getId() : null,
                inventory.getRoom() != null ? inventory.getRoom().getRoomNumber() : null,
                inventory.getItemName(),
                inventory.getQuantity(),
                inventory.getConditionStatus(),
                inventory.getLastUpdated()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory item retrieved successfully", response));
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get inventory by room ID")
    @RequirePermission("INVENTORY_READ")
    public ResponseEntity<ApiResponse<Page<InventoryResponse>>> getInventoryByRoomId(@PathVariable Long roomId, Pageable pageable) {
        List<Inventory> inventoryList = inventoryService.getInventoryByRoomId(roomId);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), inventoryList.size());
        List<Inventory> subList = inventoryList.subList(start, end);
        Page<Inventory> inventory = new PageImpl<>(subList, pageable, inventoryList.size());

        Page<InventoryResponse> response = inventory.map(item -> new InventoryResponse(
                item.getId(),
                item.getRoom() != null ? item.getRoom().getId() : null,
                item.getRoom() != null ? item.getRoom().getRoomNumber() : null,
                item.getItemName(),
                item.getQuantity(),
                item.getConditionStatus(),
                item.getLastUpdated()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory retrieved successfully", response));
    }

    @GetMapping("/condition/{conditionStatus}")
    @Operation(summary = "Get inventory by condition status")
    @RequirePermission("INVENTORY_READ")
    public ResponseEntity<ApiResponse<Page<InventoryResponse>>> getInventoryByConditionStatus(@PathVariable Inventory.ConditionStatus conditionStatus, Pageable pageable) {
        List<Inventory> inventoryList = inventoryService.getInventoryByConditionStatus(conditionStatus);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), inventoryList.size());
        List<Inventory> subList = inventoryList.subList(start, end);
        Page<Inventory> inventory = new PageImpl<>(subList, pageable, inventoryList.size());

        Page<InventoryResponse> response = inventory.map(item -> new InventoryResponse(
                item.getId(),
                item.getRoom() != null ? item.getRoom().getId() : null,
                item.getRoom() != null ? item.getRoom().getRoomNumber() : null,
                item.getItemName(),
                item.getQuantity(),
                item.getConditionStatus(),
                item.getLastUpdated()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory retrieved successfully", response));
    }

    @GetMapping("/item/{itemName}")
    @Operation(summary = "Get inventory by item name")
    @RequirePermission("INVENTORY_READ")
    public ResponseEntity<ApiResponse<Page<InventoryResponse>>> getInventoryByItemName(@PathVariable String itemName, Pageable pageable) {
        List<Inventory> inventoryList = inventoryService.getInventoryByItemName(itemName);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), inventoryList.size());
        List<Inventory> subList = inventoryList.subList(start, end);
        Page<Inventory> inventory = new PageImpl<>(subList, pageable, inventoryList.size());

        Page<InventoryResponse> response = inventory.map(item -> new InventoryResponse(
                item.getId(),
                item.getRoom() != null ? item.getRoom().getId() : null,
                item.getRoom() != null ? item.getRoom().getRoomNumber() : null,
                item.getItemName(),
                item.getQuantity(),
                item.getConditionStatus(),
                item.getLastUpdated()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inventory item")
    @RequirePermission("INVENTORY_UPDATE")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(@PathVariable Long id, @Valid @RequestBody InventoryRequest request) {
        Inventory inventoryDetails = new Inventory();
        // Set room relationship
        if (request.getRoomId() != null) {
            Room room = new Room();
            room.setId(request.getRoomId());
            inventoryDetails.setRoom(room);
        }
        inventoryDetails.setItemName(request.getItemName());
        inventoryDetails.setQuantity(request.getQuantity());
        inventoryDetails.setConditionStatus(request.getConditionStatus());

        Inventory updatedInventory = inventoryService.updateInventory(id, inventoryDetails);
        InventoryResponse response = new InventoryResponse(
                updatedInventory.getId(),
                updatedInventory.getRoom() != null ? updatedInventory.getRoom().getId() : null,
                updatedInventory.getRoom() != null ? updatedInventory.getRoom().getRoomNumber() : null,
                updatedInventory.getItemName(),
                updatedInventory.getQuantity(),
                updatedInventory.getConditionStatus(),
                updatedInventory.getLastUpdated()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory item updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory item")
    @RequirePermission("INVENTORY_DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory item deleted successfully", null));
    }
}
