package com.scoreboardchanger.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("scoreboardchanger.json");

    private static ModConfig INSTANCE = new ModConfig();

    // Toggle: enable or disable the fake scoreboard
    public boolean enabled = false;

    // Player info
    public String fakeNickname = "yavklynxx";
    public String fakeRank = "Игрок";
    public String fakeRankColor = "§a"; // green by default

    // Currency
    public String fakeCoins = "0";
    public String fakeTokens = "0";
    public String fakeSkulls = "0";

    // Stats
    public String fakeKills = "0";
    public String fakeDeaths = "0";
    public String fakePlaytime = "0ч";

    public static ModConfig getInstance() {
        return INSTANCE;
    }

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) INSTANCE = loaded;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(INSTANCE, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
