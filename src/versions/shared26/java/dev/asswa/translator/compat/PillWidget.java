package dev.asswa.translator.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

/**
 * Widget plumbing for the 26.x lines: the render hook is
 * {@code extractWidgetRenderState} and drawing goes through
 * {@code GuiGraphicsExtractor}.
 */
public abstract class PillWidget extends AbstractWidget {
    protected PillWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        paintPill(new Canvas(graphics));
    }

    /** Draws the pill using only the primitives this mod needs. */
    protected abstract void paintPill(PillCanvas canvas);

    private static final class Canvas implements PillCanvas {
        private final GuiGraphicsExtractor graphics;

        private Canvas(GuiGraphicsExtractor graphics) {
            this.graphics = graphics;
        }

        @Override
        public int guiHeight() {
            return graphics.guiHeight();
        }

        @Override
        public void fill(int x1, int y1, int x2, int y2, int color) {
            graphics.fill(x1, y1, x2, y2, color);
        }

        @Override
        public void text(Font font, Component text, int x, int y, int color, boolean shadow) {
            graphics.text(font, text, x, y, color, shadow);
        }
    }
}
