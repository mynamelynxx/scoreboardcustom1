package com.scoreboardchanger;

import com.scoreboardchanger.config.ModConfig;
import com.scoreboardchanger.gui.ScoreboardChangerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3675;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ScoreboardChangerClient implements ClientModInitializer {

    public static class_304 openGuiKey;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new class_304(
                "key.scoreboardchanger.open_gui",
                class_3675.class_307.field_1668,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.scoreboardchanger"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.method_1436()) {
                if (client.field_1755 == null) {
                    client.method_1507(new ScoreboardChangerScreen());
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            ModConfig cfg = ModConfig.getInstance();
            if (!cfg.enabled) return;

            class_310 client = class_310.method_1551();
            if (client == null) return;

            if (cfg.debugMode) {
                renderDebugOverlay(drawContext, client, cfg);
                return;
            }

            if (client.field_1687 == null || client.field_1724 == null) return;

            class_269 scoreboard = client.field_1687.method_8428();
            class_266 objective = scoreboard.method_1189(class_8646.field_45157);
            if (objective == null) return;

            renderOverlay(drawContext, client, cfg, scoreboard, objective);
        });
    }

    private void renderDebugOverlay(class_332 context, class_310 client, ModConfig cfg) {
        class_327 tr = client.field_1772;
        int screenWidth = client.method_22683().method_4486();
        int lineHeight = tr.field_2000 + 1;

        List<class_2561> lines = buildFakeLines(cfg);
        int maxWidth = 100;
        for (class_2561 line : lines) {
            maxWidth = Math.max(maxWidth, tr.method_27525(line));
        }
        maxWidth += 6;

        int panelRight = screenWidth - 2 + cfg.offsetX;
        int panelLeft = panelRight - maxWidth;
        int startY = 10 + cfg.offsetY;

        for (int i = 0; i < lines.size(); i++) {
            int lineY = startY + lineHeight + i * lineHeight;
            context.method_51439(tr, lines.get(i), panelLeft + 3, lineY, 0xFFFFFF, false);
        }
    }

    private void renderOverlay(class_332 context, class_310 client,
                                ModConfig cfg, class_269 scoreboard, class_266 objective) {
        class_327 tr = client.field_1772;
        int screenWidth = client.method_22683().method_4486();
        int screenHeight = client.method_22683().method_4502();

        Collection<class_9011> entries = scoreboard.method_1184(objective);
        if (entries == null || entries.isEmpty()) return;

        List<class_9011> sorted = new ArrayList<>(entries);
        sorted.sort((a, b) -> Integer.compare(b.comp_2128(), a.comp_2128()));
        if (sorted.size() > 15) sorted = sorted.subList(0, 15);

        int lineHeight = tr.field_2000 + 1;
        int entryCount = sorted.size();

        int maxWidth = tr.method_27525(objective.method_1114());
        for (class_9011 entry : sorted) {
            maxWidth = Math.max(maxWidth, tr.method_1727(entry.comp_2127()));
        }
        maxWidth += 8;

        int boardRight = screenWidth - 3 + cfg.offsetX;
        int boardLeft = boardRight - maxWidth;
        int bottomY = screenHeight / 2 + entryCount * lineHeight / 3 + cfg.offsetY;

        for (int i = 0; i < sorted.size(); i++) {
            class_9011 entry = sorted.get(i);
            class_2561 replacement = getReplacement(entry.comp_2127(), cfg);
            if (replacement == null) continue;

            int lineY = bottomY - (i + 1) * lineHeight;
            context.method_51439(tr, replacement, boardLeft, lineY, 0xFFFFFF, false);
        }
    }

    private List<class_2561> buildFakeLines(ModConfig cfg) {
        List<class_2561> lines = new ArrayList<>();
        lines.add(parseColoredText("&#fc8a1a&l  ⚡ &#fc1a1a&lАнархия-105"));
        lines.add(parseColoredText("&#FC1A1A╔&#F21717═&#E81515═&#DE1212═&#D41010═&#CA0D0D═&#BF0A0A═&#B50808═&#AB0505═&#A10303═&#970000═"));
        lines.add(parseColoredText("&#fc1a1a╠╣ &#fc9700" + cfg.fakeNickname));
        lines.add(parseColoredText("&#fc1a1a╠ &#00fcfc⭐ §fРанг:" + cfg.fakeRankColor + " " + cfg.fakeRank));
        lines.add(parseColoredText("&#fc1a1a╠ &#fcfc1a$ §fМонет: &#fcfc1a" + cfg.fakeCoins));
        lines.add(parseColoredText("&#fc1a1a╠ &#00fc00⛁ §fТокенов: &#00fc00" + cfg.fakeTokens));
        lines.add(parseColoredText("&#fc1a1a╠"));
        lines.add(parseColoredText("&#fc1a1a╠╣ &#fc9700Статистика"));
        lines.add(parseColoredText("&#fc1a1a╠ §fУбийств: &#fcfc32" + cfg.fakeKills));
        lines.add(parseColoredText("&#fc1a1a╠ &#fcfce3Смертей: &#fc3200" + cfg.fakeDeaths));
        lines.add(parseColoredText("&#fc1a1a╠ &#fcfce3Наиграно: &#3297fc" + cfg.fakePlaytime));
        lines.add(parseColoredText("&#fc1a1a╠"));
        lines.add(parseColoredText("&#fc1a1a╠╣ &#fcfce3Донат &#65fc32/don"));
        lines.add(parseColoredText("&#FC1A1A╚&#F21717═&#E81515═&#DE1212═&#D41010═&#CA0D0D═&#BF0A0A═&#B50808═&#AB0505═&#A10303═&#970000═"));
        return lines;
    }

    private class_2561 getReplacement(String raw, ModConfig cfg) {
        String s = raw.replaceAll("§.", "").trim();
        if (s.contains("Ранг:"))
            return parseColoredText("§7Ранг: " + cfg.fakeRankColor + " " + cfg.fakeRank);
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
     * Преобразует строку с hex-цветами (&#RRGGBB, #RRGGBB, §x§R§R§G§G§B§B)
     * и кодами форматирования (&l, §l, &o, §o и т.д.) в объект Text с правильным форматированием.
     */
    private class_2561 parseColoredText(String raw) {
        if (raw == null || raw.isEmpty()) return class_2561.method_43470("");

        // Сначала преобразуем §x§F§F§0§0§2§E в &#FF002E
        String converted = raw;
        java.util.regex.Pattern rgbPattern = java.util.regex.Pattern.compile("§x(§[0-9A-Fa-f]){6}");
        java.util.regex.Matcher rgbMatcher = rgbPattern.matcher(converted);
        while (rgbMatcher.find()) {
            String fullMatch = rgbMatcher.group(); // например "§x§F§F§0§0§2§E"
            StringBuilder hex = new StringBuilder("&#");
            for (int i = 2; i < fullMatch.length(); i += 2) { // пропускаем "§x", берём пары "§F", "§F"...
                char digit = fullMatch.charAt(i + 1); // символ после §
                hex.append(digit);
            }
            converted = converted.replace(fullMatch, hex.toString());
        }

        // Теперь обрабатываем обычные &#RRGGBB, #RRGGBB и коды форматирования
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(&?#[0-9a-fA-F]{6})|([&§][0-9a-z])");
        java.util.regex.Matcher matcher = pattern.matcher(converted);
        class_5250 result = class_2561.method_43470("");
        int lastEnd = 0;
        class_2583 currentStyle = class_2583.field_24360;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String textPart = converted.substring(lastEnd, matcher.start());
                result.method_10852(class_2561.method_43470(textPart).method_10862(currentStyle));
            }
            String code = matcher.group();
            if (code.startsWith("&#") || code.startsWith("#")) {
                // Hex-цвет
                String hex = code.startsWith("&#") ? code.substring(2) : code.substring(1);
                try {
                    Optional<class_5251> optionalColor = class_5251.method_27719("#" + hex).result();
                    if (optionalColor.isPresent()) {
                        currentStyle = currentStyle.method_27703(optionalColor.get());
                    }
                } catch (Exception ignored) {}
            } else {
                // Код форматирования: &l, §l, &o, §o, &n, &m, &r и т.д.
                char c = code.charAt(1);
                currentStyle = applyFormatting(currentStyle, c);
            }
            lastEnd = matcher.end();
        }
        if (lastEnd < converted.length()) {
            result.method_10852(class_2561.method_43470(converted.substring(lastEnd)).method_10862(currentStyle));
        }
        return result;
    }

    /**
     * Применяет стандартный код форматирования Minecraft (цвет, жирный, курсив и т.д.)
     */
    private class_2583 applyFormatting(class_2583 style, char code) {
        switch (code) {
            case '0': return style.method_27703(class_5251.method_27717(0x000000));
            case '1': return style.method_27703(class_5251.method_27717(0x0000AA));
            case '2': return style.method_27703(class_5251.method_27717(0x00AA00));
            case '3': return style.method_27703(class_5251.method_27717(0x00AAAA));
            case '4': return style.method_27703(class_5251.method_27717(0xAA0000));
            case '5': return style.method_27703(class_5251.method_27717(0xAA00AA));
            case '6': return style.method_27703(class_5251.method_27717(0xFFAA00));
            case '7': return style.method_27703(class_5251.method_27717(0xAAAAAA));
            case '8': return style.method_27703(class_5251.method_27717(0x555555));
            case '9': return style.method_27703(class_5251.method_27717(0x5555FF));
            case 'a': return style.method_27703(class_5251.method_27717(0x55FF55));
            case 'b': return style.method_27703(class_5251.method_27717(0x55FFFF));
            case 'c': return style.method_27703(class_5251.method_27717(0xFF5555));
            case 'd': return style.method_27703(class_5251.method_27717(0xFF55FF));
            case 'e': return style.method_27703(class_5251.method_27717(0xFFFF55));
            case 'f': return style.method_27703(class_5251.method_27717(0xFFFFFF));
            case 'l': return style.method_10982(true);
            case 'o': return style.method_10978(true);
            case 'n': return style.method_30938(true);
            case 'm': return style.method_36140(true);
            case 'r': return class_2583.field_24360;
            default: return style;
        }
    }
}
