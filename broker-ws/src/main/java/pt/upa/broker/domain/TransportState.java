package pt.upa.broker.domain;


public enum TransportState {
	REQUESTED,
    BUDGETED,
    FAILED,
    BOOKED,
    HEADING,
    ONGOING,
    COMPLETED;

    public String value() {
        return name();
    }

    public static TransportState fromValue(String v) {
        return valueOf(v);
    }
}
