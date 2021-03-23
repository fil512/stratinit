package com.kenstevens.stratinit.client.api;

public interface ICommandList {
    void removeLast();

    void removeAll();

    void addItem(String description);
}
