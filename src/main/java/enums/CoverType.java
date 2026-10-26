package enums;

public enum CoverType {
    COMPREHENSIVE("Comprehensive Cover"),
    SMART("Smart Cover"),
    SAVER("Saver Cover");

    private final String displayName;

    CoverType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
