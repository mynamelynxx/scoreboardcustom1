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

            renderOverlay(drawContext, client, cfg, scoreboard, objective);
        });
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

        // === Точная копия логики позиционирования из InGameHud ===
        int entryCount = sorted.size();

        // Считаем ширину скорборда — максимум между шириной заголовка и строк
        int maxWidth = tr.getWidth(objective.getDisplayName());
        for (ScoreboardEntry entry : sorted) {
            String name = entry.owner();
            // Убираем форматирование для расчёта ширины
            maxWidth = Math.max(maxWidth, tr.getWidth(name) + 8);
        }

        int boardWidth = maxWidth + 3;
        int lineHeight = tr.fontHeight + 1; // = 10

        // Высота всего блока
        int boardHeight = entryCount * lineHeight;

        // Позиция Y — по центру экрана
        int startY = screenHeight / 2 + boardHeight / 3;

        // Позиция X — правый край
        int rightX = screenWidth - 3;
        int leftX = rightX - boardWidth;

        // Рисуем замену для каждой строки снизу вверх (как MC)
        for (int i = 0; i < sorted.size(); i++) {
            ScoreboardEntry entry = sorted.get(i);
            String originalName = entry.owner();
            Text replacement = getReplacement(originalName, cfg);

            if (replacement != null) {
                // Y позиция этой строки (MC рисует снизу вверх)
                int lineY = startY - (i + 1) * lineHeight;

                // Закрываем оригинальный текст прямоугольником цвета фона скорборда
                // Фон строк скорборда: 0x80000000 (полупрозрачный чёрный)
                // Чётные строки чуть светлее: 0x40000000
                int bgColor = (i % 2 == 0) ? 0x40000000 : 0x50000000;
                context.fill(leftX, lineY - 1, rightX, lineY + lineHeight - 1, 0xFF000000);
                context.fill(leftX + 1, lineY - 1, rightX - 1, lineY + lineHeight - 1, bgColor);

                // Рисуем замену
                context.drawTextWithShadow(tr, replacement, leftX + 3, lineY, 0xFFFFFF);
            }
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

        // Никнейм — строка без пробелов и двоеточий
        if (!s.isEmpty() && !s.contains(":") && !s.contains(" ")) {
            if (!cfg.fakeNickname.isEmpty()) {
                return Text.literal("§f" + cfg.fakeNickname);
            }
        }
        return null;
    }
}
