package roles;

import java.time.LocalDateTime;

public record Vehicle(
    String licensePlate,
    VehicleType type,
    LocalDateTime entryTime,
    ParkingSpot assignedSpot
) {}