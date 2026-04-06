package com.scoreboardchanger.mixin;

import com.scoreboardchanger.config.ModConfig;
import net.minecraft.client.gui.hud.GameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameHud.class)
public class ScoreboardObjectiveRendererMixin {

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
        if (raw.equals("yavklynxx") || raw.equals(cfg.fakeNickname)) {
            return Text.literal(cfg.fakeNickname);
        }
        if (containsIgnoreFormatting(raw, "Ранг:")) {
            return Text.literal("§7Ранг: " + cfg.fakeRankColor + cfg.fakeRank);
        }
        if (containsIgnoreFormatting(raw, "Монет:")) {
            return Text.literal("§7Монет: §6" + cfg.fakeCoins);
        }
        if (containsIgnoreFormatting(raw, "Токенов:")) {
            return Text.literal("§7Токенов: §b" + cfg.fakeTokens);
        }
        if (containsIgnoreFormatting(raw, "Черепков:")) {
            return Text.literal("§7Черепков: §d" + cfg.fakeSkulls);
        }
        if (containsIgnoreFormatting(raw, "Убийств:")) {
            return Text.literal("§7Убийств: §a" + cfg.fakeKills);
        }
        if (containsIgnoreFormatting(raw, "Смертей:")) {
            return Text.literal("§7Смертей: §c" + cfg.fakeDeaths);
        }
        if (containsIgnoreFormatting(raw, "Наиграно:")) {
            return Text.literal("§7Наиграно: §e" + cfg.fakePlaytime);
        }
        return Text.literal(raw);
    }

    private boolean containsIgnoreFormatting(String text, String keyword) {
        String stripped = text.replaceAll("§.", "");
        return stripped.contains(keyword);
    }
}
