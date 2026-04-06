package com.scoreboardchanger.mixin;

import com.scoreboardchanger.config.ModConfig;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Optionally patch the player list HUD (tab list) name if needed.
 * Currently not used, but can be expanded.
 */
@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    // Reserved for future use - tab list modifications
}
