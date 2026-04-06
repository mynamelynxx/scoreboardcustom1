package com.scoreboardchanger.mixin;

import com.scoreboardchanger.config.ModConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public class ScoreboardObjectiveRendererMixin {

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 0
            ),
            index = 1
    )
    private Text modifyFirstText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 1
            ),
            index = 1
    )
    private Text modifySecondText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 2
            ),
            index = 1
    )
    private Text modifyThirdText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 3
            ),
            index = 1
    )
    private Text modifyFourthText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 4
            ),
            index = 1
    )
    private Text modifyFifthText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 5
            ),
            index = 1
    )
    private Text modifySixthText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 6
            ),
            index = 1
    )
    private Text modifySeventhText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 7
            ),
            index = 1
    )
    private Text modifyEighthText(Text original) {
        return replaceText(original);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
                    ordinal = 8
            ),
            index = 1
    )
    private Text modifyNinthText(Text original) {
        return replaceText(original);
    }

    private Text replaceText(Text original) {
        ModConfig cfg = ModConfig.getInstance();
        if (!cfg.enabled) return original;

        String raw = original.getString();

        if (containsStripped(raw, "Ранг:"))       return Text.literal("§7Ранг: " + cfg.fakeRankColor + cfg.fakeRank);
        if (containsStripped(raw, "Монет:"))      return Text.literal("§7Монет: §6" + cfg.fakeCoins);
        if (containsStripped(raw, "Токенов:"))    return Text.literal("§7Токенов: §b" + cfg.fakeTokens);
        if (containsStripped(raw, "Черепков:"))   return Text.literal("§7Черепков: §d" + cfg.fakeSkulls);
        if (containsStripped(raw, "Убийств:"))    return Text.literal("§7Убийств: §a" + cfg.fakeKills);
        if (containsStripped(raw, "Смертей:"))    return Text.literal("§7Смертей: §c" + cfg.fakeDeaths);
        if (containsStripped(raw, "Наиграно:"))   return Text.literal("§7Наиграно: §e" + cfg.fakePlaytime);

        // Nickname: single word, no spaces, no colons — top line of scoreboard
        String stripped = raw.replaceAll("§.", "").trim();
        if (!stripped.isEmpty() && !stripped.contains(":") && !stripped.contains(" ")
                && (stripped.equals(cfg.fakeNickname) || stripped.length() <= 20)) {
            // Only replace if it looks like a nickname (no spaces, no colons)
            // Be careful not to replace mode name like "Анархия - 505"
            if (stripped.equals(cfg.fakeNickname)) {
                return Text.literal(cfg.fakeNickname);
            }
        }

        return original;
    }

    private boolean containsStripped(String text, String keyword) {
        return text.replaceAll("§.", "").contains(keyword);
    }
}
