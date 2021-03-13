package com.kenstevens.stratinit.dto.news;

public interface SINewsCountable {
    void increment(int count);

    default void decrement(int count) {
        this.increment(-1 * count);
    }

    int getCount();
}
