import roles.*;
import database.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
public class FeeCalculator {
    private static final Map<VehicleType, Double> HOURLY_RATES = Map.of(
        VehicleType.MOTORCYCLE, 2.0,
        VehicleType.CAR, 5.0,
        VehicleType.BUS, 10.0
    );

    public double calculateFee(Vehicle vehicle, LocalDateTime exitTime) {
        Duration duration = Duration.between(vehicle.entryTime(), exitTime);
        double hours = duration.toHours() + (duration.toMinutesPart() > 0 ? 1 : 0);
        return hours * HOURLY_RATES.get(vehicle.type());
    }
}