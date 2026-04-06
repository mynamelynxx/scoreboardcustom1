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
            if (client.world == null || client.player == null) return;

            Scoreboard scoreboard = client.world.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            if (objective == null) return;

            renderFakeScoreboard(drawContext, client, cfg, scoreboard, objective);
        });
    }

    private void renderFakeScoreboard(DrawContext context, MinecraftClient client,
                                      ModConfig cfg, Scoreboard scoreboard, ScoreboardObjective objective) {
        TextRenderer tr = client.textRenderer;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Correct API for 1.21.4
        Collection<ScoreboardEntry> entries = scoreboard.getScoreboardEntries(objective);
        if (entries == null || entries.isEmpty()) return;

        List<ScoreboardEntry> sorted = new ArrayList<>(entries);
        sorted.sort((a, b) -> Integer.compare(b.value(), a.value()));
        if (sorted.size() > 15) sorted = sorted.subList(0, 15);

        int lineHeight = tr.fontHeight + 1;
        int totalHeight = sorted.size() * lineHeight + lineHeight + 2;
        int startY = (screenHeight - totalHeight) / 2 + 10;

        // Calculate max width for X position
        int maxWidth = tr.getWidth(objective.getDisplayName());
        for (ScoreboardEntry entry : sorted) {
            maxWidth = Math.max(maxWidth, tr.getWidth(entry.owner()));
        }
        maxWidth += 8;
        int startX = screenWidth - maxWidth - 3;

        int y = startY + lineHeight; // skip header line
        for (ScoreboardEntry entry : sorted) {
            String originalName = entry.owner();
            Text replacement = getReplacement(originalName, cfg);

            if (replacement != null) {
                // Cover original text with background rectangle
                context.fill(startX - 2, y - 1, screenWidth - 2, y + lineHeight - 1, 0x88000000);
                // Draw replacement text
                context.drawTextWithShadow(tr, replacement, startX, y, 0xFFFFFF);
            }
            y += lineHeight;
        }
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

        if (!s.isEmpty() && !s.contains(":") && !s.contains(" ")) {
            if (s.equals(cfg.fakeNickname) || raw.contains(cfg.fakeNickname)) {
                return Text.literal("§f" + cfg.fakeNickname);
            }
        }
        return null;
    }
}
