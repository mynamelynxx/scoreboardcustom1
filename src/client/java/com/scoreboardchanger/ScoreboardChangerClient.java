package com.scoreboardchanger;

import com.scoreboardchanger.config.ModConfig;
import com.scoreboardchanger.gui.ScoreboardChangerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ScoreboardChangerClient implements ClientModInitializer {

    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scoreboardchanger.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.scoreboardchanger"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ScoreboardChangerScreen());
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            ModConfig cfg = ModConfig.getInstance();
            if (!cfg.enabled) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;

            if (cfg.debugMode) {
                renderDebugOverlay(drawContext, client, cfg);
                return;
            }

            if (client.world == null || client.player == null) return;

            Scoreboard scoreboard = client.world.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            if (objective == null) return;

            renderOverlay(drawContext, client, cfg, scoreboard, objective);
        });
    }

    private void renderDebugOverlay(DrawContext context, MinecraftClient client, ModConfig cfg) {
        TextRenderer tr = client.textRenderer;
        int screenWidth = client.getWindow().getScaledWidth();
        int lineHeight = tr.fontHeight + 1;

        List<Text> lines = buildFakeLines(cfg);
        int maxWidth = 100;
        for (Text line : lines) {
            maxWidth = Math.max(maxWidth, tr.getWidth(line));
        }
        maxWidth += 6;

        int panelRight = screenWidth - 2 + cfg.offsetX;
        int panelLeft = panelRight - maxWidth;
        int startY = 10 + cfg.offsetY;

        context.drawCenteredTextWithShadow(tr, parseColoredText("§e[DEBUG]"), (panelLeft + panelRight) / 2, startY, 0xFFFF55);

        for (int i = 0; i < lines.size(); i++) {
            int lineY = startY + lineHeight + i * lineHeight;
            context.drawTextWithShadow(tr, lines.get(i), panelLeft + 3, lineY, 0xFFFFFF);
        }
    }

    private void renderOverlay(DrawContext context, MinecraftClient client,
                                ModConfig cfg, Scoreboard scoreboard, ScoreboardObjective objective) {
        TextRenderer tr = client.textRenderer;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        Collection<ScoreboardEntry> entries = scoreboard.getScoreboardEntries(objective);
        if (entries == null || entries.isEmpty()) return;

        List<ScoreboardEntry> sorted = new ArrayList<>(entries);
        sorted.sort((a, b) -> Integer.compare(b.value(), a.value()));
        if (sorted.size() > 15) sorted = sorted.subList(0, 15);

        int lineHeight = tr.fontHeight + 1;
        int entryCount = sorted.size();

        int maxWidth = tr.getWidth(objective.getDisplayName());
        for (ScoreboardEntry entry : sorted) {
            maxWidth = Math.max(maxWidth, tr.getWidth(entry.owner()));
        }
        maxWidth += 8;

        int boardRight = screenWidth - 3 + cfg.offsetX;
        int boardLeft = boardRight - maxWidth;
        int bottomY = screenHeight / 2 + entryCount * lineHeight / 3 + cfg.offsetY;

        for (int i = 0; i < sorted.size(); i++) {
            ScoreboardEntry entry = sorted.get(i);
            Text replacement = getReplacement(entry.owner(), cfg);
            if (replacement == null) continue;

            int lineY = bottomY - (i + 1) * lineHeight;
            context.drawTextWithShadow(tr, replacement, boardLeft, lineY, 0xFFFFFF);
        }
    }

    private List<Text> buildFakeLines(ModConfig cfg) {
        List<Text> lines = new ArrayList<>();
        lines.add(parseColoredText("§x§F§C§1§A§1§A╔§x§F§5§1§7§1§7═"));
        lines.add(parseColoredText("§x§F§C§1§A§1§A╠╣ §x§F§C§9§7§0§0" + cfg.fakeNickname));
        lines.add(parseColoredText("§7Ранг: " + cfg.fakeRankColor + cfg.fakeRank));
        lines.add(parseColoredText("§7Монет: §6" + cfg.fakeCoins));
        lines.add(parseColoredText("§7Токенов: §b" + cfg.fakeTokens));
        lines.add(parseColoredText("§7Черепков: §d" + cfg.fakeSkulls));
        lines.add(parseColoredText("§x§F§C§1§A§1§A╠ §fУбийств: §a" + cfg.fakeKills));
        lines.add(parseColoredText("§7Смертей: §c" + cfg.fakeDeaths));
        lines.add(parseColoredText("§7Наиграно: §e" + cfg.fakePlaytime));
        return lines;
    }

    private Text getReplacement(String raw, ModConfig cfg) {
        String s = raw.replaceAll("§.", "").trim();
        if (s.contains("Ранг:"))
            return parseColoredText("§7Ранг: " + cfg.fakeRankColor + cfg.fakeRank);
        if (s.contains("Монет:"))
            return parseColoredText("§7Монет: §6" + cfg.fakeCoins);
        if (s.contains("Токенов:"))
            return parseColoredText("§7Токенов: §b" + cfg.fakeTokens);
        if (s.contains("Черепков:"))
            return parseColoredText("§7Черепков: §d" + cfg.fakeSkulls);
        if (s.contains("Убийств:"))
            return parseColoredText("§7Убийств: §a" + cfg.fakeKills);
        if (s.contains("Смертей:"))
            return parseColoredText("§7Смертей: §c" + cfg.fakeDeaths);
        if (s.contains("Наиграно:"))
            return parseColoredText("§7Наиграно: §e" + cfg.fakePlaytime);
        if (!s.isEmpty() && !s.contains(":") && !s.contains(" ") && !cfg.fakeNickname.isEmpty())
            return parseColoredText("§f" + cfg.fakeNickname);
        return null;
    }

    /**
     * Преобразует строку с hex-цветами (&#RRGGBB или #RRGGBB) и стандартными кодами (§)
     * в объект Text с правильным форматированием.
     */
    private Text parseColoredText(String raw) {
        if (raw == null || raw.isEmpty()) return Text.literal("");

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(&?#[0-9a-fA-F]{6})|(§.)");
        java.util.regex.Matcher matcher = pattern.matcher(raw);
        MutableText result = Text.literal("");
        int lastEnd = 0;
        Style currentStyle = Style.EMPTY;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String textPart = raw.substring(lastEnd, matcher.start());
                result.append(Text.literal(textPart).setStyle(currentStyle));
            }
            String code = matcher.group();
            if (code.startsWith("&#") || code.startsWith("#")) {
                String hex = code.startsWith("&#") ? code.substring(2) : code.substring(1);
                try {
                    Optional<TextColor> optionalColor = TextColor.parse(hex).result();
                    if (optionalColor.isPresent()) {
                        currentStyle = currentStyle.withColor(optionalColor.get());
                    }
                } catch (Exception ignored) {}
            } else if (code.startsWith("§")) {
                char c = code.charAt(1);
                currentStyle = applyFormatting(currentStyle, c);
            }
            lastEnd = matcher.end();
        }
        if (lastEnd < raw.length()) {
            result.append(Text.literal(raw.substring(lastEnd)).setStyle(currentStyle));
        }
        return result;
    }

    /**
     * Применяет стандартный код форматирования Minecraft (цвет, жирный, курсив и т.д.)
     */
    private Style applyFormatting(Style style, char code) {
        switch (code) {
            case '0': return style.withColor(TextColor.fromRgb(0x000000));
            case '1': return style.withColor(TextColor.fromRgb(0x0000AA));
            case '2': return style.withColor(TextColor.fromRgb(0x00AA00));
            case '3': return style.withColor(TextColor.fromRgb(0x00AAAA));
            case '4': return style.withColor(TextColor.fromRgb(0xAA0000));
            case '5': return style.withColor(TextColor.fromRgb(0xAA00AA));
            case '6': return style.withColor(TextColor.fromRgb(0xFFAA00));
            case '7': return style.withColor(TextColor.fromRgb(0xAAAAAA));
            case '8': return style.withColor(TextColor.fromRgb(0x555555));
            case '9': return style.withColor(TextColor.fromRgb(0x5555FF));
            case 'a': return style.withColor(TextColor.fromRgb(0x55FF55));
            case 'b': return style.withColor(TextColor.fromRgb(0x55FFFF));
            case 'c': return style.withColor(TextColor.fromRgb(0xFF5555));
            case 'd': return style.withColor(TextColor.fromRgb(0xFF55FF));
            case 'e': return style.withColor(TextColor.fromRgb(0xFFFF55));
            case 'f': return style.withColor(TextColor.fromRgb(0xFFFFFF));
            case 'l': return style.withBold(true);
            case 'o': return style.withItalic(true);
            case 'n': return style.withUnderline(true);
            case 'm': return style.withStrikethrough(true);
            case 'r': return Style.EMPTY;
            default: return style;
        }
    }
}
