package dev.asswa.translator;

import dev.asswa.translator.compat.PillCanvas;
import dev.asswa.translator.compat.PillWidget;
import dev.asswa.translator.compat.UiCompat;
import dev.asswa.translator.mixin.ChatScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TranslatorClient implements ClientModInitializer {
    public static final String MOD_ID = "translatebar";

    private record Language(String code, String label, String nameKey) {}

    // Source language is auto-detected; the chip cycles through the target languages.
    // Display names live in the language files (assets/translatebar/lang) and are
    // resolved by the game according to the player's selected language.
    private static final List<Language> LANGUAGES = List.of(
            new Language("en", "EN", "translatebar.language.en"),
            new Language("de", "DE", "translatebar.language.de"),
            new Language("fr", "FR", "translatebar.language.fr"),
            new Language("es", "ES", "translatebar.language.es"),
            new Language("pl", "PL", "translatebar.language.pl"),
            new Language("zh-CN", "ZH", "translatebar.language.zh"),
            new Language("ru", "RU", "translatebar.language.ru"),
            new Language("ja", "JA", "translatebar.language.ja"),
            new Language("it", "IT", "translatebar.language.it"),
            new Language("pt", "PT", "translatebar.language.pt")
    );
    private static int languageIndex = 0;

    // --- Chat-bar look: black, translucent, tuned to blend into the vanilla row ----
    private static final int PILL_RADIUS = 2;
    private static final int BG_ALPHA_BONUS = 0x1E; // extra darkness layered over the bar
    private static final int BG_ALPHA_MIN = 0x4D;   // stays visible even if the bar is off
    private static final int BG_ALPHA_MAX = 0x96;
    private static final int HOVER_ALPHA_BONUS = 0x28;
    private static final int BORDER_NORMAL = 0x00000000; // no rim; the darker pill defines itself
    private static final int BORDER_HOVER = 0x47FFFFFF;  // faint light rim on hover
    private static final int SHINE_TOP = 0x1AFFFFFF;
    private static final int SHINE_TOP_HOVER = 0x2EFFFFFF;
    private static final int SHADE_BOTTOM = 0x40000000;
    private static final int DROP_SHADOW = 0x38000000;
    private static final int TEXT_NORMAL = 0xE6FFFFFF;
    private static final int TEXT_HOVER = 0xFFFFFFFF;

    // Vanilla draws the chat input row (its dark bar) at [height - 14, height - 2),
    // while the edit box itself is flush with the bottom: [height - 12, height].
    private static final int BAR_LIFT_FROM_INPUT = 2;
    private static final int LEFT_MARGIN = 2; // flush with the left edge of the chat bar
    private static final int TRANSLATE_WIDTH = 14;
    private static final int LANGUAGE_WIDTH = 20;
    private static final int BUTTON_GAP = 2;

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof ChatScreen chatScreen)
                    || !(screen instanceof ChatScreenAccessor accessor)) return;

            EditBox input = accessor.ruEnTranslator$getInput();

            // Match the chat bar exactly: 12 px tall, bottom edge 2 px above the screen
            // edge, so the pills never poke above the row or fall off the bottom.
            int h = Math.max(10, Math.min(input.getHeight(), scaledHeight));
            int y = clamp(input.getY() - BAR_LIFT_FROM_INPUT, 0, Math.max(0, scaledHeight - h - 2));

            int reserved = LEFT_MARGIN + TRANSLATE_WIDTH + BUTTON_GAP + LANGUAGE_WIDTH + BUTTON_GAP;
            int rightEdge = input.getX() + input.getWidth();
            input.setX(reserved);
            input.setWidth(Math.max(40, rightEdge - reserved));

            GlassButton translateButton = new GlassButton(input, LEFT_MARGIN, y, TRANSLATE_WIDTH, h,
                    Component.literal("文"));
            translateButton.setTooltip(Tooltip.create(Component.translatable("translatebar.tooltip.translate")));
            translateButton.setAction(() -> {
                focusInput(chatScreen, input);
                translateInput(client, chatScreen, input, LANGUAGES.get(languageIndex));
            });

            GlassButton languageButton = new GlassButton(input,
                    LEFT_MARGIN + TRANSLATE_WIDTH + BUTTON_GAP, y, LANGUAGE_WIDTH, h,
                    Component.literal(LANGUAGES.get(languageIndex).label()));
            languageButton.setTooltip(Tooltip.create(Component.translatable("translatebar.tooltip.language")));
            languageButton.setAction(() -> {
                languageIndex = (languageIndex + 1) % LANGUAGES.size();
                languageButton.setMessage(Component.literal(LANGUAGES.get(languageIndex).label()));
                showMessage(client, Component.translatable("translatebar.message.language_set",
                        Component.translatable(LANGUAGES.get(languageIndex).nameKey())));
                focusInput(chatScreen, input);
            });

            UiCompat.addWidget(screen, translateButton);
            UiCompat.addWidget(screen, languageButton);
        });
    }

    private static void focusInput(ChatScreen screen, EditBox input) {
        screen.setFocused(input);
        input.setFocused(true);
    }

    private static void translateInput(Minecraft client, ChatScreen screen, EditBox input, Language target) {
        String original = input.getValue();
        if (original == null || original.isBlank()) {
            showMessage(client, Component.translatable("translatebar.message.empty"));
            return;
        }
        if (original.startsWith("/")) {
            showMessage(client, Component.translatable("translatebar.message.command"));
            return;
        }

        input.setEditable(false);
        showMessage(client, Component.translatable("translatebar.message.translating",
                Component.translatable(target.nameKey())));
        CompletableFuture.supplyAsync(() -> MyMemoryTranslator.translate(original, target.code()))
                .whenComplete((translated, error) -> client.execute(() -> {
                    if (!UiCompat.isCurrentScreen(client, screen)) return;
                    input.setEditable(true);
                    if (error != null || translated == null || translated.isBlank()) {
                        showMessage(client, Component.translatable("translatebar.message.error"));
                    } else {
                        input.setValue(translated);
                        showMessage(client, Component.translatable("translatebar.message.done"));
                    }
                    focusInput(screen, input);
                }));
    }

    private static void showMessage(Minecraft client, Component message) {
        UiCompat.showOverlayMessage(client, message);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * A compact black translucent pill styled after the chat row. Its own rendering
     * keeps it vertically aligned with the vanilla chat bar every frame.
     */
    private static final class GlassButton extends PillWidget {
        private final EditBox input;
        private Runnable action = () -> {};

        private GlassButton(EditBox input, int x, int y, int width, int height, Component message) {
            super(x, y, width, height, message);
            this.input = input;
        }

        void setAction(Runnable action) {
            this.action = action;
        }

        @Override
        public void onClick(MouseButtonEvent event, boolean doubleClick) {
            if (event.button() != 0) return; // left click only
            playDownSound(Minecraft.getInstance().getSoundManager());
            action.run();
        }

        @Override
        protected void paintPill(PillCanvas canvas) {
            // Defensive sync: stay on the chat row even if something else moves the input.
            int maxY = Math.max(0, canvas.guiHeight() - height - BAR_LIFT_FROM_INPUT);
            setY(clamp(input.getY() - BAR_LIFT_FROM_INPUT, 0, maxY));

            int x = getX();
            int y = getY();
            boolean hovered = isHovered();

            // Follow the player's chat background opacity so the pills sit on the row
            // like a deeper double-layered patch of the same black.
            int barAlpha = Minecraft.getInstance().options.getBackgroundColor(Integer.MIN_VALUE) >>> 24;
            int bgAlpha = clamp(barAlpha + BG_ALPHA_BONUS, BG_ALPHA_MIN, BG_ALPHA_MAX);
            if (hovered) bgAlpha = clamp(bgAlpha + HOVER_ALPHA_BONUS, 0, 0xE0);
            int background = bgAlpha << 24; // pure black with the tuned alpha
            int border = hovered ? BORDER_HOVER : BORDER_NORMAL;

            // Soft drop shadow, then the translucent body (with a light rim on hover).
            roundedRect(canvas, x, y + 1, width, height, PILL_RADIUS, DROP_SHADOW);
            if ((border >>> 24) != 0) {
                roundedRect(canvas, x, y, width, height, PILL_RADIUS, border);
                roundedRect(canvas, x + 1, y + 1, width - 2, height - 2, Math.max(0, PILL_RADIUS - 1), background);
            } else {
                roundedRect(canvas, x, y, width, height, PILL_RADIUS, background);
            }

            // A light top edge and a soft shade along the bottom keep the cell readable.
            canvas.fill(x + 1 + PILL_RADIUS, y + 1, x + width - 1 - PILL_RADIUS, y + 2,
                    hovered ? SHINE_TOP_HOVER : SHINE_TOP);
            canvas.fill(x + 1 + PILL_RADIUS, y + height - 2, x + width - 1 - PILL_RADIUS, y + height - 1, SHADE_BOTTOM);

            var font = Minecraft.getInstance().font;
            var message = getMessage();
            // Vanilla-style centering: for unicode glyphs (e.g. 文, 8 px wide but 9 px
            // advance) this keeps the icon optically centered inside the pill.
            int textX = x + width / 2 - font.width(message) / 2;
            int textY = y + (height - 8) / 2;
            canvas.text(font, message, textX, textY, hovered ? TEXT_HOVER : TEXT_NORMAL, true);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }

    /** Fills a rectangle with slightly rounded corners plus an antialiased fringe. */
    private static void roundedRect(PillCanvas canvas, int x, int y, int w, int h, int radius, int color) {
        if (w <= 0 || h <= 0) return;
        int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));
        int fringe = fade(color, 0.45f);
        int x2 = x + w;

        for (int row = 0; row < h; row++) {
            int inset = Math.max(Math.max(0, r - row), Math.max(0, r - (h - 1 - row)));
            int rowY = y + row;
            canvas.fill(x + inset, rowY, x2 - inset, rowY + 1, color);
            if (inset > 0 && (fringe >>> 24) != 0) {
                canvas.fill(x + inset - 1, rowY, x + inset, rowY + 1, fringe);
                canvas.fill(x2 - inset, rowY, x2 - inset + 1, rowY + 1, fringe);
            }
        }
    }

    private static int fade(int argb, float multiplier) {
        int alpha = (int) (((argb >>> 24) & 0xFF) * multiplier);
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }
}
