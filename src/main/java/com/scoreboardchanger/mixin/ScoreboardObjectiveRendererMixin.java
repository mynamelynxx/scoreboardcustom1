package com.scoreboardchanger.mixin;

import com.scoreboardchanger.config.ModConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This mixin intercepts individual score entry texts rendered on the scoreboard sidebar.
 * It replaces the text if it matches one of the patterns we want to fake.
 */
@Mixin(InGameHud.class)
public class ScoreboardObjectiveRendererMixin {

    /**
     * Intercepts each line text rendered in the scoreboard sidebar
     * and replaces it with our fake value if the mod is enabled and the line matches.
     */
    @ModifyVariable(
            method = "renderScoreboardSidebar",
            at = @At("STORE"),
            ordinal = 0
    )
    private Text modifyScoreboardLineText(Text original) {
        ModConfig cfg = ModConfig.getInstance();
        if (!cfg.enabled) return original;

        String raw = original.getString();
        return replaceIfMatch(raw, cfg);
    }

    private Text replaceIfMatch(String raw, ModConfig cfg) {
        // Nickname line - usually just the player name at the top
        if (raw.equals("yavklynxx") || raw.equals(cfg.fakeNickname)) {
            // We can't change the original server nickname this way easily;
            // this line is handled by PlayerListHudMixin for the tab list.
            // For scoreboard display name line we return the fake name.
            return Text.literal(cfg.fakeNickname);
        }

        // Rank line: "Ранг: Игрок" or similar
        if (containsIgnoreFormatting(raw, "Ранг:")) {
            return Text.literal("§7Ранг: " + cfg.fakeRankColor + cfg.fakeRank);
        }

        // Coins: "Монет: 0"
        if (containsIgnoreFormatting(raw, "Монет:")) {
            return Text.literal("§7Монет: §6" + cfg.fakeCoins);
        }

        // Tokens: "Токенов: 0"
        if (containsIgnoreFormatting(raw, "Токенов:")) {
            return Text.literal("§7Токенов: §b" + cfg.fakeTokens);
        }

        // Skulls: "Черепков: 0"
        if (containsIgnoreFormatting(raw, "Черепков:")) {
            return Text.literal("§7Черепков: §d" + cfg.fakeSkulls);
        }

        // Kills: "Убийств: 0"
        if (containsIgnoreFormatting(raw, "Убийств:")) {
            return Text.literal("§7Убийств: §a" + cfg.fakeKills);
        }

        // Deaths: "Смертей: 0"
        if (containsIgnoreFormatting(raw, "Смертей:")) {
            return Text.literal("§7Смертей: §c" + cfg.fakeDeaths);
        }

        // Playtime: "Наиграно: 0ч"
        if (containsIgnoreFormatting(raw, "Наиграно:")) {
            return Text.literal("§7Наиграно: §e" + cfg.fakePlaytime);
        }

        return Text.literal(raw);
    }

    /**
     * Strip Minecraft formatting codes (§X) before comparing
     */
    private boolean containsIgnoreFormatting(String text, String keyword) {
        String stripped = text.replaceAll("§.", "");
        return stripped.contains(keyword);
    }
}
