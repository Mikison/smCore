package dev.sonmiike.smcore.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MiniFormatter {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    private MiniFormatter() {}

    public static Component deserialize(String string) {
        return mm.deserialize(string);
    }
}
