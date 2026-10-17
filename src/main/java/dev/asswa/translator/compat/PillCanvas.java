package dev.asswa.translator.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * The tiny subset of the GUI drawing surface the pill buttons use. Every
 * Minecraft line provides its own implementation ({@code PillWidget}), so the
 * common button code does not depend on version-specific graphics classes.
 */
public interface PillCanvas {
    int guiHeight();

    void fill(int x1, int y1, int x2, int y2, int color);

    void text(Font font, Component text, int x, int y, int color, boolean shadow);
}
