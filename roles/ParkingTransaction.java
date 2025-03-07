
package roles;

import java.time.LocalDateTime;

public record ParkingTransaction(
    String transactionId,
    Vehicle vehicle,
    LocalDateTime entryTime,
    LocalDateTime exitTime,
    double fee
) {}