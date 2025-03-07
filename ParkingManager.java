public class ParkingManager {
    private final ParkingDatabase database;
    private final ParkingSpotAllocator allocator;
    private final FeeCalculator feeCalculator;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public ParkingManager(int floors, int spotsPerFloor) {
        this.database = new ParkingDatabase(floors, spotsPerFloor);
        this.allocator = new ParkingSpotAllocator(database);
        this.feeCalculator = new FeeCalculator();
    }

    public CompletableFuture<Vehicle> checkIn(String licensePlate, VehicleType type) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<ParkingSpot> spot = allocator.allocateSpot(type);
            if (spot.isEmpty()) {
                throw new IllegalStateException("No available parking spots");
            }
            
            Vehicle vehicle = new Vehicle(
                licensePlate, type, LocalDateTime.now(), spot.get()
            );
            database.activeVehicles.put(licensePlate, vehicle);
            return vehicle;
        }, executor);
    }

    public CompletableFuture<ParkingTransaction> checkOut(String licensePlate) {
        return CompletableFuture.supplyAsync(() -> {
            Vehicle vehicle = database.activeVehicles.remove(licensePlate);
            if (vehicle == null) {
                throw new IllegalStateException("Vehicle not found");
            }

            LocalDateTime exitTime = LocalDateTime.now();
            double fee = feeCalculator.calculateFee(vehicle, exitTime);
            allocator.releaseSpot(vehicle.assignedSpot().spotId());

            ParkingTransaction transaction = new ParkingTransaction(
                UUID.randomUUID().toString(), vehicle, vehicle.entryTime(), exitTime, fee
            );
            database.transactions.put(transaction.transactionId(), transaction);
            return transaction;
        }, executor);
    }

    public int getAvailableSpots(VehicleType type) {
        database.lock.readLock().lock();
        try {
            return (int) database.spots.values().stream()
                .filter(spot -> !spot.isOccupied())
                .filter(spot -> spot.supportedType() == type)
                .count();
        } finally {
            database.lock.readLock().unlock();
        }
    }
}