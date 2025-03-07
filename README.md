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
### ParkingSpot (Record)
