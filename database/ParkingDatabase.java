public class ParkingDatabase {
    private final Map<Integer, ParkingSpot> spots;
    private final Map<String, Vehicle> activeVehicles;
    private final Map<String, ParkingTransaction> transactions;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ParkingDatabase(int floors, int spotsPerFloor) {
        this.spots = new ConcurrentHashMap<>();
        this.activeVehicles = new ConcurrentHashMap<>();
        this.transactions = new ConcurrentHashMap<>();
        initializeParkingSpots(floors, spotsPerFloor);
    }

    private void initializeParkingSpots(int floors, int spotsPerFloor) {
        int spotId = 1;
        for (int floor = 1; floor <= floors; floor++) {
            for (int i = 0; i < spotsPerFloor; i++) {
                VehicleType type = i % 3 == 0 ? VehicleType.MOTORCYCLE :
                                 i % 3 == 1 ? VehicleType.CAR : VehicleType.BUS;
                spots.put(spotId, new ParkingSpot(spotId, floor, type, false, type.getSizeFactor()));
                spotId++;
            }
        }
    }
}