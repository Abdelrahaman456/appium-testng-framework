package enums;

public enum InsuranceFlow {
    FLOW_1_NEW_INSURANCE_SEQUENCE("Flow 1: New Insurance + Sequence Number"),
    FLOW_2_NEW_INSURANCE_CUSTOM_CARD("Flow 2: New Insurance + Custom Card"),
    FLOW_3_OWNERSHIP_TRANSFER_SEQUENCE("Flow 3: Ownership Transfer + Sequence Number"),
    FLOW_4_OWNERSHIP_TRANSFER_CUSTOM_CARD("Flow 4: Ownership Transfer + Custom Card");

    private final String description;

    InsuranceFlow(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
