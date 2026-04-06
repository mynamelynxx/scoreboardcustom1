package com.scoreboardchanger;

import com.scoreboardchanger.config.ModConfig;
import com.scoreboardchanger.gui.ScoreboardChangerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

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
    }
}
