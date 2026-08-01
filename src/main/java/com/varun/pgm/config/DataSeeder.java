package com.varun.pgm.config;

import com.varun.pgm.entity.*;
import com.varun.pgm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Value("${app.data.seeding.enabled:true}")
    private boolean dataSeedingEnabled;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE tenant DROP CONSTRAINT IF EXISTS tenant_status_check");
            jdbcTemplate.execute("ALTER TABLE room DROP CONSTRAINT IF EXISTS room_status_check");
            jdbcTemplate.execute("ALTER TABLE payment DROP CONSTRAINT IF EXISTS payment_status_check");
            logger.info("Successfully dropped legacy status check constraints if present.");
        } catch (Exception e) {
            logger.warn("Could not drop legacy status check constraints: {}", e.getMessage());
        }

        if (!dataSeedingEnabled) {
            logger.info("Data seeding is disabled. Skipping...");
            return;
        }

        logger.info("Starting data seeding process...");
        try {
            seedRolesAndPermissions();
            seedAdminUser();
            seedStaffUsers();
            seedTenantUsers();
            seedRooms();
            seedTenants();
            seedInventory();
            seedPayments();
            logger.info("Data seeding completed successfully!");
        } catch (Exception e) {
            logger.error("Error during data seeding: ", e);
            throw e;
        }
    }

    private void seedRolesAndPermissions() {
        // Create permissions if they don't exist
        if (permissionRepository.count() == 0) {
            List<Permission> permissions = Arrays.asList(
                // Admin permissions
                createPermission("ADMIN_CREATE", "Create Admin", "Permission to create new admin users"),
                createPermission("ADMIN_READ", "Read Admin", "Permission to view admin users"),
                createPermission("ADMIN_UPDATE", "Update Admin", "Permission to update admin users"),
                createPermission("ADMIN_DELETE", "Delete Admin", "Permission to delete admin users"),

                // Dashboard permissions
                createPermission("DASHBOARD_VIEW", "View Dashboard", "Permission to view admin dashboard"),

                // Staff permissions
                createPermission("STAFF_CREATE", "Create Staff", "Permission to create new staff members"),
                createPermission("STAFF_READ", "Read Staff", "Permission to view staff members"),
                createPermission("STAFF_UPDATE", "Update Staff", "Permission to update staff members"),
                createPermission("STAFF_DELETE", "Delete Staff", "Permission to delete staff members"),

                // Room permissions
                createPermission("ROOM_CREATE", "Create Room", "Permission to create new rooms"),
                createPermission("ROOM_READ", "Read Room", "Permission to view rooms"),
                createPermission("ROOM_UPDATE", "Update Room", "Permission to update rooms"),
                createPermission("ROOM_DELETE", "Delete Room", "Permission to delete rooms"),

                // Tenant permissions
                createPermission("TENANT_CREATE", "Create Tenant", "Permission to create new tenants"),
                createPermission("TENANT_READ", "Read Tenant", "Permission to view tenants"),
                createPermission("TENANT_UPDATE", "Update Tenant", "Permission to update tenants"),
                createPermission("TENANT_DELETE", "Delete Tenant", "Permission to delete tenants"),

                // Payment permissions
                createPermission("PAYMENT_CREATE", "Create Payment", "Permission to create new payments"),
                createPermission("PAYMENT_READ", "Read Payment", "Permission to view payments"),
                createPermission("PAYMENT_UPDATE", "Update Payment", "Permission to update payments"),
                createPermission("PAYMENT_DELETE", "Delete Payment", "Permission to delete payments"),

                // Inventory permissions
                createPermission("INVENTORY_CREATE", "Create Inventory", "Permission to create inventory items"),
                createPermission("INVENTORY_READ", "Read Inventory", "Permission to view inventory items"),
                createPermission("INVENTORY_UPDATE", "Update Inventory", "Permission to update inventory items"),
                createPermission("INVENTORY_DELETE", "Delete Inventory", "Permission to delete inventory items"),


                // Notification permissions
                createPermission("NOTIFICATION_CREATE", "Create Notification", "Permission to create and send notifications"),
                createPermission("NOTIFICATION_READ", "Read Notification", "Permission to view and manage notifications")
            );

            permissionRepository.saveAll(permissions);
            System.out.println("✅ Created " + permissions.size() + " permissions");
        }

        // Create roles if they don't exist
        if (roleRepository.count() == 0) {
            List<Permission> allPermissions = permissionRepository.findAll();

            Role superAdminRole = createRole("SUPER_ADMIN", "Full system access", true, allPermissions);

            // Admin gets most permissions except some sensitive ones
            List<Permission> adminPermissions = allPermissions.stream()
                .filter(p -> !p.getKey().equals("ADMIN_DELETE"))
                .toList();
            Role adminRole = createRole("ADMIN", "Administrative access", false, adminPermissions);

            // Manager gets operational permissions
            List<Permission> managerPermissions = allPermissions.stream()
                .filter(p -> p.getKey().startsWith("ROOM_") ||
                           p.getKey().startsWith("TENANT_") ||
                           p.getKey().startsWith("PAYMENT_") ||
                           p.getKey().startsWith("INVENTORY_") ||
                           p.getKey().startsWith("STAFF_READ"))
                .toList();
            Role managerRole = createRole("MANAGER", "Management access", false, managerPermissions);

            // Staff gets limited permissions
            List<Permission> staffPermissions = allPermissions.stream()
                .filter(p -> p.getKey().startsWith("ROOM_READ") ||
                           p.getKey().startsWith("TENANT_READ") ||
                           p.getKey().startsWith("PAYMENT_READ") || 
                           p.getKey().startsWith("INVENTORY_READ")|| 
                           p.getKey().startsWith("NOTIFICATION_READ"))
                .toList();
            Role staffRole = createRole("STAFF", "Basic staff access", false, staffPermissions);

            // Tenant gets very limited permissions (only their own data)
            List<Permission> tenantPermissions = allPermissions.stream()
                .filter(p -> p.getKey().startsWith("PAYMENT_READ") ||
                           p.getKey().equals("NOTIFICATION_READ"))
                .toList();
            Role tenantRole = createRole("TENANT", "Tenant access to their own data", false, tenantPermissions);

            roleRepository.saveAll(Arrays.asList(superAdminRole, adminRole, managerRole, staffRole, tenantRole));
            System.out.println("✅ Created 5 roles with assigned permissions");
        }
    }

    private void seedAdminUser() {
        if (adminRepository.findByEmail("admin@pgm.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setName("Super Admin");
            admin.setEmail("admin@pgm.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setContactNo("9876543210");
            admin.setCreatedAt(LocalDateTime.now());

            Admin savedAdmin = adminRepository.save(admin);

            // Assign Super Admin role
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("Super Admin role not found"));

            UserRoleId userRoleId = new UserRoleId(savedAdmin.getId(), superAdminRole.getId());
            UserRole userRole = new UserRole();
            userRole.setId(userRoleId);
            userRole.setUserType(UserRole.UserType.ADMIN);
            userRole.setRole(superAdminRole);
            userRole.setAssignedAt(LocalDateTime.now());

            userRoleRepository.save(userRole);
            System.out.println("✅ Created default admin user: admin@pgm.com / admin123");
        } else {
            System.out.println("ℹ️  Admin user already exists, skipping creation");
        }
    }

    private void seedStaffUsers() {
        if (staffRepository.findByEmail("rajesh@pgm.com").isEmpty()) {
            Admin admin = adminRepository.findByEmail("admin@pgm.com")
                .orElseThrow(() -> new RuntimeException("Admin not found"));

            Role managerRole = roleRepository.findByName("MANAGER")
                .orElseThrow(() -> new RuntimeException("Manager role not found"));
            Role staffRole = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new RuntimeException("Staff role not found"));

            List<Staff> staffList = Arrays.asList(
                createStaff("Rajesh Kumar", "rajesh@pgm.com", "rajesh123", "9876543211", Staff.Role.MANAGER, Staff.Status.ACTIVE, admin, managerRole),
                createStaff("Priya Sharma", "priya@pgm.com", "priya123", "9876543212", Staff.Role.STAFF, Staff.Status.ACTIVE, admin, staffRole),
                createStaff("Amit Singh", "amit@pgm.com", "amit123", "9876543213", Staff.Role.STAFF, Staff.Status.ACTIVE, admin, staffRole)
            );

            staffRepository.saveAll(staffList);
            System.out.println("✅ Created " + staffList.size() + " staff members");
        } else {
            System.out.println("ℹ️  Staff users already exist, skipping creation");
        }
    }

    private void seedTenantUsers() {
        // Create user accounts for existing tenants so they can receive notifications
        if (tenantRepository.count() > 0) {
            List<Tenant> tenants = tenantRepository.findAll();
            Role tenantRole = roleRepository.findByName("TENANT")
                .orElseThrow(() -> new RuntimeException("Tenant role not found"));

            // Check if tenant user roles already exist
            List<UserRole> existingTenantRoles = userRoleRepository.findById_RoleId(tenantRole.getId());
            if (existingTenantRoles.isEmpty()) {
                for (Tenant tenant : tenants) {
                    // Create a user account for each tenant
                    createTenantUser(tenant, tenantRole);
                }
                System.out.println("✅ Created " + tenants.size() + " tenant user accounts");
            } else {
                System.out.println("ℹ️  Tenant users already exist, skipping creation");
            }
        }
    }

    private void createTenantUser(Tenant tenant, Role tenantRole) {
        // For demo purposes, create a simple user account for the tenant
        // In production, this would be handled during tenant registration

        // Note: We don't have a separate TenantUser entity, so we'll use the tenant's email
        // as a way to identify them. In a real app, you'd have a User entity that tenants can log in with.

        // For now, we'll just assign the tenant role to a dummy user ID
        // This is a simplified approach for demo purposes

        // Actually, let's create a simple approach - we'll assign the tenant role to the tenant's ID
        // treating the tenant as if they have a user account
        try {
            UserRoleId userRoleId = new UserRoleId(tenant.getId(), tenantRole.getId());
            UserRole userRole = new UserRole();
            userRole.setId(userRoleId);
            userRole.setUserType(UserRole.UserType.TENANT);
            userRole.setRole(tenantRole);
            userRole.setAssignedAt(LocalDateTime.now());

            userRoleRepository.save(userRole);
        } catch (Exception e) {
            logger.warn("Could not create user role for tenant {}: {}", tenant.getEmail(), e.getMessage());
        }
    }

    private Staff createStaff(String name, String email, String password, String contact,
                            Staff.Role role, Staff.Status status, Admin admin, Role userRole) {
        Staff staff = new Staff();
        staff.setName(name);
        staff.setEmail(email);
        staff.setPassword(passwordEncoder.encode(password));
        staff.setAdmin(admin);
        staff.setRole(role);
        staff.setStatus(status);
        staff.setCreatedAt(LocalDateTime.now());

        Staff savedStaff = staffRepository.save(staff);

        // Assign role
        UserRoleId userRoleId = new UserRoleId(savedStaff.getId(), userRole.getId());
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setId(userRoleId);
        userRoleEntity.setUserType(UserRole.UserType.STAFF);
        userRoleEntity.setRole(userRole);
        userRoleEntity.setAssignedAt(LocalDateTime.now());

        userRoleRepository.save(userRoleEntity);

        return savedStaff;
    }

    private void seedRooms() {
        if (roomRepository.count() == 0) {
            List<Room> rooms = Arrays.asList(
                createRoom("101", Room.RoomType.SINGLE, new BigDecimal("5000.00"), Room.RoomStatus.AVAILABLE, "WiFi, AC, Attached Bathroom"),
                createRoom("102", Room.RoomType.SINGLE, new BigDecimal("5500.00"), Room.RoomStatus.AVAILABLE, "WiFi, AC, Attached Bathroom, Balcony"),
                createRoom("201", Room.RoomType.DOUBLE, new BigDecimal("8000.00"), Room.RoomStatus.AVAILABLE, "WiFi, AC, Attached Bathroom, Shared Kitchen"),
                createRoom("202", Room.RoomType.DOUBLE, new BigDecimal("8500.00"), Room.RoomStatus.AVAILABLE, "WiFi, AC, Attached Bathroom, Private Kitchen"),
                createRoom("301", Room.RoomType.SHARED, new BigDecimal("3000.00"), Room.RoomStatus.AVAILABLE, "WiFi, AC, Shared Bathroom, Common Kitchen"),
                createRoom("302", Room.RoomType.SHARED, new BigDecimal("3200.00"), Room.RoomStatus.AVAILABLE, "WiFi, AC, Shared Bathroom, Common Kitchen, Study Area")
            );

            roomRepository.saveAll(rooms);
            System.out.println("✅ Created " + rooms.size() + " rooms");
        } else {
            System.out.println("ℹ️  Rooms already exist, skipping creation");
        }
    }

    private Room createRoom(String roomNumber, Room.RoomType roomType, BigDecimal rentAmount,
                          Room.RoomStatus status, String facilities) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setRentAmount(rentAmount);
        room.setStatus(status);
        room.setFacilities(facilities);
        room.setCreatedAt(LocalDateTime.now());
        return room;
    }

    private void seedTenants() {
        if (tenantRepository.count() == 0) {
            List<Room> availableRooms = roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == Room.RoomStatus.AVAILABLE)
                .limit(4)
                .toList();

            if (!availableRooms.isEmpty()) {
                List<Tenant> tenants = Arrays.asList(
                    createTenant("Arun Kumar", "arun.kumar@email.com", "9876543214",
                                "AADHAR", "1234-5678-9012", availableRooms.get(0),
                                LocalDateTime.now().minusDays(30), null, new BigDecimal("5000.00")),
                    createTenant("Meera Patel", "meera.patel@email.com", "9876543215",
                                "PAN", "ABCDE1234F", availableRooms.get(1),
                                LocalDateTime.now().minusDays(15), null, new BigDecimal("5500.00")),
                    createTenant("Vikram Singh", "vikram.singh@email.com", "9876543216",
                                "AADHAR", "5678-9012-3456", availableRooms.get(2),
                                LocalDateTime.now().minusDays(7), null, new BigDecimal("8000.00"))
                );

                tenantRepository.saveAll(tenants);

                // Update room status to occupied
                for (Tenant tenant : tenants) {
                    Room room = tenant.getRoom();
                    room.setStatus(Room.RoomStatus.OCCUPIED);
                    roomRepository.save(room);
                }

                System.out.println("✅ Created " + tenants.size() + " tenants and updated room statuses");
            } else {
                System.out.println("ℹ️  Tenants already exist, skipping creation");
            }
        }
    }

    private Tenant createTenant(String name, String email, String phone, String idProofType,
                              String idProofNumber, Room room, LocalDateTime checkInDate,
                              LocalDateTime checkOutDate, BigDecimal depositAmount) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setEmail(email);
        tenant.setPhone(phone);
        tenant.setIdProofType(idProofType);
        tenant.setIdProofNumber(idProofNumber);
        tenant.setRoom(room);
        tenant.setCheckInDate(checkInDate.toLocalDate());
        if (checkOutDate != null) {
            tenant.setCheckOutDate(checkOutDate.toLocalDate());
        }
        tenant.setDepositAmount(depositAmount);
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant.setCreatedAt(LocalDateTime.now());
        return tenant;
    }

    private void seedInventory() {
        if (inventoryRepository.count() == 0) {
            List<Room> rooms = roomRepository.findAll();

            List<Inventory> inventoryItems = Arrays.asList(
                createInventory("Bed", 1, Inventory.ConditionStatus.GOOD, rooms.get(0)),
                createInventory("Mattress", 1, Inventory.ConditionStatus.GOOD, rooms.get(0)),
                createInventory("Study Table", 1, Inventory.ConditionStatus.GOOD, rooms.get(0)),
                createInventory("Chair", 1, Inventory.ConditionStatus.GOOD, rooms.get(0)),
                createInventory("Wardrobe", 1, Inventory.ConditionStatus.NEEDS_REPAIR, rooms.get(1)),
                createInventory("AC", 1, Inventory.ConditionStatus.GOOD, rooms.get(1)),
                createInventory("Refrigerator", 1, Inventory.ConditionStatus.GOOD, rooms.get(2)),
                createInventory("Microwave", 1, Inventory.ConditionStatus.GOOD, rooms.get(2)),
                createInventory("Washing Machine", 1, Inventory.ConditionStatus.REPLACED, rooms.get(3))
            );

            inventoryRepository.saveAll(inventoryItems);
            System.out.println("✅ Created " + inventoryItems.size() + " inventory items");
        } else {
            System.out.println("ℹ️  Inventory items already exist, skipping creation");
        }
    }

    private Inventory createInventory(String itemName, int quantity, Inventory.ConditionStatus conditionStatus, Room room) {
        Inventory inventory = new Inventory();
        inventory.setItemName(itemName);
        inventory.setQuantity(quantity);
        inventory.setConditionStatus(conditionStatus);
        inventory.setRoom(room);
        inventory.setLastUpdated(LocalDateTime.now());
        return inventory;
    }

    private void seedPayments() {
        if (paymentRepository.count() == 0) {
            List<Tenant> tenants = tenantRepository.findAll();

            if (!tenants.isEmpty()) {
                List<Payment> payments = Arrays.asList(
                    createPayment(tenants.get(0), new BigDecimal("5000.00"), LocalDateTime.now().minusDays(30),
                                "November-2024", "BANK_TRANSFER", "RCP001", Payment.PaymentStatus.PAID),
                    createPayment(tenants.get(0), new BigDecimal("5000.00"), LocalDateTime.now().minusDays(2),
                                "December-2024", "UPI", "RCP002", Payment.PaymentStatus.PAID),
                    createPayment(tenants.get(1), new BigDecimal("5500.00"), LocalDateTime.now().minusDays(15),
                                "November-2024", "CASH", "RCP003", Payment.PaymentStatus.PAID),
                    createPayment(tenants.get(2), new BigDecimal("8000.00"), LocalDateTime.now().minusDays(7),
                                "November-2024", "ONLINE", "RCP004", Payment.PaymentStatus.PENDING)
                );

                paymentRepository.saveAll(payments);
                System.out.println("✅ Created " + payments.size() + " payment records");
            } else {
                System.out.println("ℹ️  Payment records already exist, skipping creation");
            }
        }
    }

    private Payment createPayment(Tenant tenant, BigDecimal amount, LocalDateTime paymentDate,
                                String paymentMonth, String paymentMethod, String receiptNumber,
                                Payment.PaymentStatus status) {
        Payment payment = new Payment();
        payment.setTenant(tenant);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate.toLocalDate());
        payment.setPaymentMonth(paymentMonth);
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentMethod));
        payment.setReceiptNumber(receiptNumber);
        payment.setStatus(status);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    private Permission createPermission(String key, String name, String description) {
        Permission permission = new Permission();
        permission.setKey(key);
        permission.setName(name);
        permission.setDescription(description);
        permission.setCreatedAt(LocalDateTime.now());
        return permission;
    }

    private Role createRole(String name, String description, boolean isDefault, List<Permission> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setIsDefault(isDefault);
        role.setPermissions(permissions);
        role.setCreatedAt(LocalDateTime.now());
        return role;
    }
}
