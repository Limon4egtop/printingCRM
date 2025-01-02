package ru.limon4egtop.printingCRM.models;

public enum Machine {
    GALLUS_R200(0),
    SOHN_4400(1),
    ARSOMA_280(2);

    private final int code;

    Machine(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Machine fromCode(int code) {
        for (Machine machine : Machine.values()) {
            if (machine.getCode() == code) {
                return machine;
            }
        }
        throw new IllegalArgumentException("Invalid machine code: " + code);
    }
}

