package dev.sonmiike.smcore.core.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


public record MuteInfo(UUID uuid, String reason, String mutedBy, LocalDateTime muteDate, LocalDateTime expiresAt) {

}

