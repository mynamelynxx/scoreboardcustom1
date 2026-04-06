package com.scoreboardchanger.gui;

import com.scoreboardchanger.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardChangerScreen extends Screen {

    private static final String[] RANK_COLORS    = {"§f","§a","§b","§e","§6","§c","§4","§d","§5","§9"};
    private static final String[] RANK_COLOR_NAMES = {"Белый","Зелёный","Голубой","Жёлтый","Золотой","Красный","Тёмно-красный","Розовый","Фиолетовый","Синий"};

    private TextFieldWidget nicknameField, rankField, coinsField, tokensField,
            skullsField, killsField, deathsField, playtimeField;

    private int selectedColorIndex = 0;
    private ButtonWidget colorBtn;

    // Offset sliders
    private OffsetSlider sliderX, sliderY;

    public ScoreboardChangerScreen() {
        super(Text.literal("Scoreboard Changer"));
    }

    @Override
    protected void init() {
        ModConfig cfg = ModConfig.getInstance();

        for (int i = 0; i < RANK_COLORS.length; i++) {
            if (RANK_COLORS[i].equals(cfg.fakeRankColor)) { selectedColorIndex = i; break; }
        }

        int col1X = this.width / 2 - 220;
        int col2X = this.width / 2 + 10;
        int startY = 25;
        int gap = 26;
        int fW = 150;
        int fH = 18;

        // === Column 1: player info ===
        nicknameField = addField(col1X + 90, startY,       fW, fH, cfg.fakeNickname);
        rankField     = addField(col1X + 90, startY+gap,   fW, fH, cfg.fakeRank);

        colorBtn = addDrawableChild(ButtonWidget.builder(
                Text.literal("Цвет: " + RANK_COLOR_NAMES[selectedColorIndex]),
                btn -> {
                    selectedColorIndex = (selectedColorIndex + 1) % RANK_COLORS.length;
                    btn.setMessage(Text.literal("Цвет: " + RANK_COLOR_NAMES[selectedColorIndex]));
                })
                .dimensions(col1X + 90, startY + gap*2, fW, fH).build());

        coinsField  = addField(col1X + 90, startY+gap*3, fW, fH, cfg.fakeCoins);
        tokensField = addField(col1X + 90, startY+gap*4, fW, fH, cfg.fakeTokens);
        skullsField = addField(col1X + 90, startY+gap*5, fW, fH, cfg.fakeSkulls);

        // === Column 2: stats ===
        killsField    = addField(col2X + 90, startY,       fW, fH, cfg.fakeKills);
        deathsField   = addField(col2X + 90, startY+gap,   fW, fH, cfg.fakeDeaths);
        playtimeField = addField(col2X + 90, startY+gap*2, fW, fH, cfg.fakePlaytime);

        // === Offset sliders ===
        int sliderY = startY + gap * 7;
        sliderX = new OffsetSlider(col1X, sliderY,     200, 18, "Смещение X", cfg.offsetX, -300, 300);
        sliderY = new OffsetSlider(col1X, sliderY + gap, 200, 18, "Смещение Y", cfg.offsetY, -300, 300);
        addDrawableChild(sliderX);
        addDrawableChild(sliderY);

        // === Buttons ===
        int btnY = this.height - 55;

        // Enable/disable
        addDrawableChild(ButtonWidget.builder(
                Text.literal(cfg.enabled ? "§aВключено" : "§cВыключено"),
                btn -> {
                    cfg.enabled = !cfg.enabled;
                    btn.setMessage(Text.literal(cfg.enabled ? "§aВключено" : "§cВыключено"));
                })
                .dimensions(this.width / 2 - 155, btnY, 100, 20).build());

        // Debug mode
        addDrawableChild(ButtonWidget.builder(
                Text.literal(cfg.debugMode ? "§aДебаг: ВКЛ" : "§7Дебаг: ВЫКЛ"),
                btn -> {
                    cfg.debugMode = !cfg.debugMode;
                    btn.setMessage(Text.literal(cfg.debugMode ? "§aДебаг: ВКЛ" : "§7Дебаг: ВЫКЛ"));
                })
                .dimensions(this.width / 2 - 50, btnY, 100, 20).build());

        // Save
        addDrawableChild(ButtonWidget.builder(Text.literal("Сохранить"), btn -> save())
                .dimensions(this.width / 2 + 55, btnY, 100, 20).build());

        // Close
        addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), btn -> close())
                .dimensions(this.width / 2 + 55, btnY + 25, 100, 20).build());
    }

    private TextFieldWidget addField(int x, int y, int w, int h, String value) {
        TextFieldWidget f = new TextFieldWidget(this.textRenderer, x, y, w, h, Text.empty());
        f.setText(value);
        f.setMaxLength(64);
        addDrawableChild(f);
        return f;
    }

    private void save() {
        ModConfig cfg = ModConfig.getInstance();
        cfg.fakeNickname  = nicknameField.getText();
        cfg.fakeRank      = rankField.getText();
        cfg.fakeRankColor = RANK_COLORS[selectedColorIndex];
        cfg.fakeCoins     = coinsField.getText();
        cfg.fakeTokens    = tokensField.getText();
        cfg.fakeSkulls    = skullsField.getText();
        cfg.fakeKills     = killsField.getText();
        cfg.fakeDeaths    = deathsField.getText();
        cfg.fakePlaytime  = playtimeField.getText();
        cfg.offsetX       = sliderX.getIntValue();
        cfg.offsetY       = sliderY.getIntValue();
        ModConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);

        int col1X = this.width / 2 - 220;
        int col2X = this.width / 2 + 10;
        int startY = 25;
        int gap = 26;

        // Column 1 labels
        context.drawTextWithShadow(textRenderer, "Никнейм:",    col1X, startY+5,       0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Ранг:",       col1X, startY+gap+5,   0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Цвет ранга:", col1X, startY+gap*2+5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Монеты:",     col1X, startY+gap*3+5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Токены:",     col1X, startY+gap*4+5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Черепки:",    col1X, startY+gap*5+5, 0xAAAAAA);

        // Column 2 labels
        context.drawTextWithShadow(textRenderer, "Убийства:",   col2X, startY+5,       0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Смерти:",     col2X, startY+gap+5,   0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Наиграно:",   col2X, startY+gap*2+5, 0xAAAAAA);

        // Rank preview
        ModConfig cfg = ModConfig.getInstance();
        String preview = RANK_COLORS[selectedColorIndex] + rankField.getText();
        context.drawTextWithShadow(textRenderer, Text.literal(preview),
                col2X, startY+gap*3+5, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, "Предпросмотр ранга:", col2X, startY+gap*3-5, 0x888888);

        // Hint for debug
        context.drawTextWithShadow(textRenderer,
                "§7Дебаг показывает overlay без сервера. Подстрой ползунки под скорборд.",
                col1X, this.height - 15, 0x888888);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() { this.client.setScreen(null); }

    // ---- Inner slider class ----
    private static class OffsetSlider extends SliderWidget {
        private final String label;
        private final int min, max;

        public OffsetSlider(int x, int y, int width, int height, String label, int current, int min, int max) {
            super(x, y, width, height, Text.empty(), (double)(current - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            updateMessage();
        }

        public int getIntValue() {
            return min + (int)Math.round(value * (max - min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + getIntValue()));
        }

        @Override
        protected void applyValue() {}
    }
}
