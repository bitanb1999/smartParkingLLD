public record ParkingSpot(
    int spotId,
    int floor,
    VehicleType supportedType,
    boolean isOccupied,
    double size
) {}