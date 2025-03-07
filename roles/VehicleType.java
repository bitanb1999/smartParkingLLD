package roles;
public enum VehicleType {
    MOTORCYCLE(1.0), CAR(2.0), BUS(4.0);
    
    private final double sizeFactor;
    
    VehicleType(double sizeFactor) {
        this.sizeFactor = sizeFactor;
    }
    
    public double getSizeFactor() {
        return sizeFactor;
    }
}