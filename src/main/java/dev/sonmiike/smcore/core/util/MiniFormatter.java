package dev.sonmiike.smcore.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import static java.lang.StringTemplate.STR;


public final class MiniFormatter {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private MiniFormatter() {}

    public static final StringTemplate.Processor<Component, RuntimeException> MM = stringTemplate -> {
        final String interpolated = STR.process(stringTemplate);
        return toComponent(interpolated);
    };

    public static Component toComponent(String string) {
        return miniMessage.deserialize(STR."<!i>\{string}");
    }

}
