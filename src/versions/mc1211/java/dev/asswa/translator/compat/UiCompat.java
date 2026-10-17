package dev.asswa.translator.compat;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * UI compatibility for 1.21.11: like the 26.1.x line, the current screen lives
 * in the {@code Minecraft.screen} field and the overlay message is set on
 * {@code Gui}, but the screen API exposes widgets through
 * {@code Screens.getButtons}.
 */
public final class UiCompat {
    private UiCompat() {}

    public static boolean isCurrentScreen(Minecraft client, ChatScreen screen) {
        return client.screen == screen;
    }

    public static void showOverlayMessage(Minecraft client, Component message) {
        client.gui.setOverlayMessage(message, false);
    }

    public static void addWidget(Screen screen, AbstractWidget widget) {
        Screens.getButtons(screen).add(widget);
    }
}
