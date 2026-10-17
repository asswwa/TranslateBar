package dev.asswa.translator.compat;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * UI compatibility for 26.2 and newer: {@code Gui} exposes the current screen
 * through a getter and the overlay message moved to {@code Gui.hud}.
 */
public final class UiCompat {
    private UiCompat() {}

    public static boolean isCurrentScreen(Minecraft client, ChatScreen screen) {
        return client.gui.screen() == screen;
    }

    public static void showOverlayMessage(Minecraft client, Component message) {
        client.gui.hud.setOverlayMessage(message, false);
    }

    public static void addWidget(Screen screen, AbstractWidget widget) {
        Screens.getWidgets(screen).add(widget);
    }
}
