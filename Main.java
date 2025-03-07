import roles.*;

class Main {
    public static void main(String[] args) {
        ParkingManager parkingManager = new ParkingManager(3, 10);

        // Check-in example
        parkingManager.checkIn("ABC123", VehicleType.CAR)
            .thenAccept(vehicle -> 
                System.out.println("Assigned spot: " + vehicle.assignedSpot().spotId()))
            .exceptionally(throwable -> {
                System.err.println("Error: " + throwable.getMessage());
                return null;
            });

        // Check-out example
        parkingManager.checkOut("ABC123")
            .thenAccept(transaction -> 
                System.out.println("Fee: $" + transaction.fee()))
            .exceptionally(throwable -> {
                System.err.println("Error: " + throwable.getMessage());
                return null;
            });

        // Check availability
        System.out.println("Available car spots: " + 
            parkingManager.getAvailableSpots(VehicleType.CAR));
    }
}