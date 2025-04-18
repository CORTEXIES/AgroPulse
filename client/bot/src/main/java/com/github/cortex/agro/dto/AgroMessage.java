package com.github.cortex.agro.dto;

import java.util.Optional;

public record AgroMessage(Agronomist agronomist, Optional<String> report, Optional<String> photoUrl) { }