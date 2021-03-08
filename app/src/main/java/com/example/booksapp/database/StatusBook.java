package com.example.booksapp.database;

public enum StatusBook {
    Unknown("Unknown"),
    Reading("Reading"),
    OnHold("On hold"),
    PlanToRead("Plan to read"),
    Dropped("Dropped"),
    ReReading("Re-reading");

    private final String name;

    StatusBook(String name) {
        this.name = name;
    }

    public boolean equalsName(String name) {
        return this.name.equals(name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Convert string to a StatusBook
     * @param name String
     * @return StatusBook, by default Unknown
     */
    public static StatusBook toStatus(String name) {
        for(StatusBook status: StatusBook.values()) {
            if(status.equalsName(name)) {
                return status;
            }
        }
        return Unknown;
    }
}
