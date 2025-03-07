public class ParkingSpotAllocator {
    private final ParkingDatabase database;

    public ParkingSpotAllocator(ParkingDatabase database) {
        this.database = database;
    }

    public Optional<ParkingSpot> allocateSpot(VehicleType type) {
        database.lock.writeLock().lock();
        try {
            return database.spots.values().stream()
                .filter(spot -> !spot.isOccupied())
                .filter(spot -> spot.supportedType() == type)
                .filter(spot -> spot.size() >= type.getSizeFactor())
                .min(Comparator.comparingInt(ParkingSpot::floor)
                    .thenComparingInt(ParkingSpot::spotId))
                .map(spot -> {
                    ParkingSpot occupiedSpot = new ParkingSpot(
                        spot.spotId(), spot.floor(), spot.supportedType(), true, spot.size()
                    );
                    database.spots.put(spot.spotId(), occupiedSpot);
                    return occupiedSpot;
                });
        } finally {
            database.lock.writeLock().unlock();
        }
    }

    public void releaseSpot(int spotId) {
        database.lock.writeLock().lock();
        try {
            ParkingSpot spot = database.spots.get(spotId);
            if (spot != null) {
                ParkingSpot freeSpot = new ParkingSpot(
                    spot.spotId(), spot.floor(), spot.supportedType(), false, spot.size()
                );
                database.spots.put(spotId(), freeSpot);
            }
        } finally {
            database.lock.writeLock().unlock();
        }
    }
}