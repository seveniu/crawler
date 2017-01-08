package com.seveniu.def;


public enum TaskStatus {
    WAIT(1),
    RUNNING(2),
    STOP(3),
    FAIL(4),
    FULL(5);

    private final int value;

    private TaskStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TaskStatus findByValue(int value) {
        switch (value) {
            case 1:
                return WAIT;
            case 2:
                return RUNNING;
            case 3:
                return STOP;
            case 4:
                return FAIL;
            case 5:
                return FULL;
            default:
                return null;
        }
    }
}
