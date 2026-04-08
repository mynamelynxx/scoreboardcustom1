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
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        context.fill(panelLeft, startY - 1, panelRight, startY + lineHeight, 0xFF1B1B1B);
        context.drawCenteredTextWithShadow(tr, Text.literal("§e[DEBUG]"), (panelLeft + panelRight) / 2, startY, 0xFFFF55);

        for (int i = 0; i < lines.size(); i++) {
            int lineY = startY + lineHeight + i * lineHeight;
            context.fill(panelLeft, lineY - 1, panelRight, lineY + lineHeight - 1,
                    i % 2 == 0 ? 0xAA1B1B1B : 0xAA222222);
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
            context.fill(boardLeft - 2, lineY - 1, boardRight + 2, lineY + lineHeight - 1, 0xFF000000);
            context.fill(boardLeft - 1, lineY - 1, boardRight + 1, lineY + lineHeight - 1,
                    i % 2 == 0 ? 0xAA000000 : 0x88000000);
            context.drawTextWithShadow(tr, replacement, boardLeft, lineY, 0xFFFFFF);
        }
    }

    private List<Text> buildFakeLines(ModConfig cfg) {
        List<Text> lines = new ArrayList<>();
        lines.add(Text.literal("&#FFFFFF&#FF1A1A╔&#F51717═&#EB1515═&#E01212═&#D61010═&#CC0D0D═&#C20A0A═&#B80808═&#AD0505═&#A30303═&#990000═&#FFFF");
        lines.add(Text.literal("§f" + cfg.fakeNickname));
        lines.add(Text.literal("§7Ранг: " + cfg.fakeRankColor + cfg.fakeRank));
        lines.add(Text.literal("§7Монет: §6" + cfg.fakeCoins));
        lines.add(Text.literal("§7Токенов: §b" + cfg.fakeTokens));
        lines.add(Text.literal("§7Черепков: §d" + cfg.fakeSkulls));
        lines.add(Text.literal("&#FFFFFF&#FF1A1A╠&#FFFFE6 §fУбийств: §a" + cfg.fakeKills));
        lines.add(Text.literal("§7Смертей: §c" + cfg.fakeDeaths));
        lines.add(Text.literal("§7Наиграно: §e" + cfg.fakePlaytime));
        return lines;
    }

    private Text getReplacement(String raw, ModConfig cfg) {
        String s = raw.replaceAll("§.", "").trim();
        if (s.contains("Ранг:"))     return Text.literal("§7Ранг: " + cfg.fakeRankColor + cfg.fakeRank);
        if (s.contains("Монет:"))    return Text.literal("§7Монет: §6" + cfg.fakeCoins);
        if (s.contains("Токенов:"))  return Text.literal("§7Токенов: §b" + cfg.fakeTokens);
        if (s.contains("Черепков:")) return Text.literal("§7Черепков: §d" + cfg.fakeSkulls);
        if (s.contains("Убийств:"))  return Text.literal("§7Убийств: §a" + cfg.fakeKills);
        if (s.contains("Смертей:"))  return Text.literal("§7Смертей: §c" + cfg.fakeDeaths);
        if (s.contains("Наиграно:")) return Text.literal("§7Наиграно: §e" + cfg.fakePlaytime);
        if (!s.isEmpty() && !s.contains(":") && !s.contains(" ") && !cfg.fakeNickname.isEmpty())
            return Text.literal("§f" + cfg.fakeNickname);
        return null;
    }
}
