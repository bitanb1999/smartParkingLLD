# Smart Parking Lot System

A comprehensive backend system for managing a multi-floor parking lot with automated spot allocation, vehicle tracking, and fee calculation.

## Table of Contents
1. [Overview](#overview)
2. [System Architecture](#system-architecture)
3. [Core Components](#core-components)
   - [Data Model](#data-model)
   - [Database Schema](#database-schema)
   - [Spot Allocator](#spot-allocator)
   - [Fee Calculator](#fee-calculator)
   - [Parking Manager](#parking-manager)
4. [Key Features](#key-features)
5. [Technical Details](#technical-details)
6. [Usage](#usage)
7. [Design Considerations](#design-considerations)
8. [Future Enhancements](#future-enhancements)

## Overview
This system manages a smart parking lot with multiple floors and various parking spot types. It handles vehicle entry/exit, automatic spot allocation based on vehicle size, real-time availability tracking, and fee calculation.

## System Architecture
The system follows a modular design with separation of concerns:
[ParkingManager] ↔ [ParkingDatabase]
↓              ↕
[FeeCalculator]   [ParkingSpotAllocator]

- **ParkingManager**: Main interface for operations
- **ParkingDatabase**: Data storage and management
- **ParkingSpotAllocator**: Spot assignment logic
- **FeeCalculator**: Fee computation logic

## Core Components

### Data Model
#### VehicleType (Enum)
```java
public enum VehicleType {
    MOTORCYCLE(1.0), CAR(2.0), BUS(4.0);
}
```
Defines vehicle categories with size factors
Size factors determine spot compatibility
Hourly rates: Motorcycle ($2), Car ($5), Bus ($10)

```java
public record ParkingSpot(int spotId, int floor, VehicleType supportedType, 
    boolean isOccupied, double size) {}
```
### ParkingSpot (Record)
```java
public record ParkingSpot(int spotId, int floor, VehicleType supportedType, 
    boolean isOccupied, double size) {}
```
Represents individual parking spots
Tracks occupancy status and location
Immutable data structure
### Vehicle (Record)
```java
public record Vehicle(String licensePlate, VehicleType type, 
    LocalDateTime entryTime, ParkingSpot assignedSpot) {}
```
Stores vehicle information
Links to assigned parking spot
Records entry timestamp
### ParkingTransaction (Record)
```java
public record ParkingTransaction(String transactionId, Vehicle vehicle, 
    LocalDateTime entryTime, LocalDateTime exitTime, double fee) {}
```
Records completed parking sessions
Stores timing and fee information
## Database Schema
### ParkingDatabase
```java
public class ParkingDatabase {
    private Map<Integer, ParkingSpot> spots;
    private Map<String, Vehicle> activeVehicles;
    private Map<String, ParkingTransaction> transactions;
    private ReadWriteLock lock;
}
```
Uses ConcurrentHashMap for thread-safe storage
Maintains three main collections:
Parking spots by ID
Active vehicles by license plate
Completed transactions by ID
Initializes spots based on floors and spots per floor
Uses ReadWriteLock for concurrent access control
## Spot Allocator
### ParkingSpotAllocator
```java
public class ParkingSpotAllocator {
    public Optional<ParkingSpot> allocateSpot(VehicleType type);
    public void releaseSpot(int spotId);
}
```
Implements allocation algorithm:
Filters available spots
Matches vehicle type and size
Prioritizes lower floors and lower spot IDs
Thread-safe spot allocation and release
Returns Optional to handle no-availability cases
## Fee Calculator
### FeeCalculator
```java
public class FeeCalculator {
    public double calculateFee(Vehicle vehicle, LocalDateTime exitTime);
}
```
Calculates fees based on:
Parking duration (rounded up to nearest hour)
Vehicle type-specific hourly rates
Simple, extensible rate structure
## Parking Manager
### ParkingManager
```java
public class ParkingManager {
    public CompletableFuture<Vehicle> checkIn(String licensePlate, VehicleType type);
    public CompletableFuture<ParkingTransaction> checkOut(String licensePlate);
    public int getAvailableSpots(VehicleType type);
}
```
Main system coordinator
Handles check-in/check-out operations
Provides availability information
Uses async operations with ExecutorService
## Key Features
<ol>Automatic Spot Allocation: Assigns optimal spots based on vehicle size
Real-time Tracking: Updates availability instantly
Fee Calculation: Computes charges based on duration and vehicle type
Concurrent Processing: Handles multiple simultaneous operations
</ol>

## Technical Details
<ol>Language: Java 23
Concurrency:
ReadWriteLock for database access
ConcurrentHashMap for collections
ExecutorService with thread pool
CompletableFuture for async operations
Data Structures: Record classes for immutable entities
Thread Safety: Fully thread-safe operations
</ol>

## Usage
```java
ParkingManager manager = new ParkingManager(3, 10); // 3 floors, 10 spots each

// Check-in
manager.checkIn("ABC123", VehicleType.CAR)
    .thenAccept(vehicle -> System.out.println("Spot: " + vehicle.assignedSpot().spotId()));

// Check-out
manager.checkOut("ABC123")
    .thenAccept(tx -> System.out.println("Fee: $" + tx.fee()));

// Availability
int available = manager.getAvailableSpots(VehicleType.CAR);
```
