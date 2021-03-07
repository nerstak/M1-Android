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
}
