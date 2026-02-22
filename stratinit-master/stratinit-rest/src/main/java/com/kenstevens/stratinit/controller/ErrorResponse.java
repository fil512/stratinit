package com.kenstevens.stratinit.controller;

import java.util.List;

public record ErrorResponse(String error, List<String> messages) {
}
