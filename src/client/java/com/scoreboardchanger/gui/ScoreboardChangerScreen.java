package com.scoreboardchanger.gui;

import com.scoreboardchanger.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ScoreboardChangerScreen extends Screen {

    // Строковые коды цветов (включая RGB через §x)
    private static final String[] RANK_COLORS = {
        "§f", "§a", "§b", "§e", "§6",
        "§x§F§F§0§0§2§E",  // ваш точный красный #ff002e
        "§4", "§d", "§5", "§9"
    };
    private static final String[] RANK_COLOR_NAMES = {
        "Белый", "Зелёный", "Голубой", "Жёлтый", "Золотой",
        "Красный (RGB)", "Тёмно-красный", "Розовый", "Фиолетовый", "Синий"
    };

    private TextFieldWidget nicknameField, rankField, coinsField, tokensField,
            skullsField, killsField, deathsField, playtimeField;

    private int selectedColorIndex = 0;
    private ButtonWidget colorBtn;
    private ButtonWidget enableBtn, debugBtn;
    private OffsetSlider sliderX, sliderY;

    public ScoreboardChangerScreen() {
        super(Text.literal("Scoreboard Changer"));
    }

    @Override
    protected void init() {
        ModConfig cfg = ModConfig.getInstance();

        // Определяем индекс по сохранённой строке цвета
        String savedColor = cfg.fakeRankColor;
        for (int i = 0; i < RANK_COLORS.length; i++) {
            if (RANK_COLORS[i].equals(savedColor)) {
                selectedColorIndex = i;
                break;
            }
        }

        int col1X = this.width / 2 - 220;
        int col2X = this.width / 2 + 10;
        int y0 = 25, gap = 26, fW = 150, fH = 18;

        nicknameField = addField(col1X + 90, y0, fW, fH, cfg.fakeNickname);
        rankField = addField(col1X + 90, y0 + gap, fW, fH, cfg.fakeRank);

        colorBtn = addDrawableChild(ButtonWidget.builder(
                Text.literal("Цвет: " + RANK_COLOR_NAMES[selectedColorIndex]),
                btn -> {
                    selectedColorIndex = (selectedColorIndex + 1) % RANK_COLORS.length;
                    btn.setMessage(Text.literal("Цвет: " + RANK_COLOR_NAMES[selectedColorIndex]));
                }).dimensions(col1X + 90, y0 + gap * 2, fW, fH).build());

        coinsField = addField(col1X + 90, y0 + gap * 3, fW, fH, cfg.fakeCoins);
        tokensField = addField(col1X + 90, y0 + gap * 4, fW, fH, cfg.fakeTokens);
        skullsField = addField(col1X + 90, y0 + gap * 5, fW, fH, cfg.fakeSkulls);

        killsField = addField(col2X + 90, y0, fW, fH, cfg.fakeKills);
        deathsField = addField(col2X + 90, y0 + gap, fW, fH, cfg.fakeDeaths);
        playtimeField = addField(col2X + 90, y0 + gap * 2, fW, fH, cfg.fakePlaytime);

        sliderX = new OffsetSlider(col1X, y0 + gap * 7, 200, 18, "Смещение X", cfg.offsetX, -300, 300);
        sliderY = new OffsetSlider(col1X, y0 + gap * 8, 200, 18, "Смещение Y", cfg.offsetY, -300, 300);
        addDrawableChild(sliderX);
        addDrawableChild(sliderY);

        int btnY = this.height - 55;
        enableBtn = addDrawableChild(ButtonWidget.builder(
                Text.literal(cfg.enabled ? "§aВключено" : "§cВыключено"),
                btn -> {
                    cfg.enabled = !cfg.enabled;
                    btn.setMessage(Text.literal(cfg.enabled ? "§aВключено" : "§cВыключено"));
                }).dimensions(this.width / 2 - 155, btnY, 100, 20).build());

        debugBtn = addDrawableChild(ButtonWidget.builder(
                Text.literal(cfg.debugMode ? "§aДебаг: ВКЛ" : "§7Дебаг: ВЫКЛ"),
                btn -> {
                    cfg.debugMode = !cfg.debugMode;
                    btn.setMessage(Text.literal(cfg.debugMode ? "§aДебаг: ВКЛ" : "§7Дебаг: ВЫКЛ"));
                }).dimensions(this.width / 2 - 50, btnY, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Сохранить"), btn -> save())
                .dimensions(this.width / 2 + 55, btnY, 100, 20).build());
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
        cfg.fakeNickname = nicknameField.getText();
        cfg.fakeRank = rankField.getText();
        cfg.fakeRankColor = RANK_COLORS[selectedColorIndex]; // сохраняем строку
        cfg.fakeCoins = coinsField.getText();
        cfg.fakeTokens = tokensField.getText();
        cfg.fakeSkulls = skullsField.getText();
        cfg.fakeKills = killsField.getText();
        cfg.fakeDeaths = deathsField.getText();
        cfg.fakePlaytime = playtimeField.getText();
        cfg.offsetX = sliderX.getIntValue();
        cfg.offsetY = sliderY.getIntValue();
        ModConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);

        int col1X = this.width / 2 - 220;
        int col2X = this.width / 2 + 10;
        int y0 = 25, gap = 26;

        context.drawTextWithShadow(textRenderer, "Никнейм:", col1X, y0 + 5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Ранг:", col1X, y0 + gap + 5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Цвет ранга:", col1X, y0 + gap * 2 + 5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Монеты:", col1X, y0 + gap * 3 + 5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Токены:", col1X, y0 + gap * 4 + 5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Черепки:", col1X, y0 + gap * 5 + 5, 0xAAAAAA);

        context.drawTextWithShadow(textRenderer, "Убийства:", col2X, y0 + 5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Смерти:", col2X, y0 + gap + 5, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Наиграно:", col2X, y0 + gap * 2 + 5, 0xAAAAAA);

        // ===== Превью ранга с поддержкой §x =====
        String colorCode = RANK_COLORS[selectedColorIndex];
        String rankText = rankField.getText();
        Text coloredRank;

        if (colorCode.startsWith("§x")) {
            // Преобразуем "§x§F§F§0§0§2§E" → "FF002E"
            String hex = colorCode.replace("§", "").substring(1);
            try {
                int rgb = Integer.parseInt(hex, 16);
                coloredRank = Text.literal(rankText).styled(style -> style.withColor(rgb));
            } catch (NumberFormatException e) {
                coloredRank = Text.literal(rankText);
            }
        } else {
            // Обычные цвета §a, §b и т.д.
            coloredRank = Text.literal(colorCode + rankText);
        }

        context.drawTextWithShadow(textRenderer, "Превью:", col2X, y0 + gap * 4, 0x888888);
        context.drawTextWithShadow(textRenderer, coloredRank, col2X, y0 + gap * 5, 0xFFFFFF);
        // =====================================

        context.drawTextWithShadow(textRenderer,
                "§7Дебаг: показывает overlay без сервера. Ползунки — подстрой под скорборд.",
                col1X, this.height - 12, 0x888888);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() { this.client.setScreen(null); }

    public static class OffsetSlider extends SliderWidget {
        private final String label;
        private final int min, max;

        public OffsetSlider(int x, int y, int width, int height, String label, int current, int min, int max) {
            super(x, y, width, height, Text.empty(), (double) (current - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            updateMessage();
        }

        public int getIntValue() {
            return min + (int) Math.round(value * (max - min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + getIntValue()));
        }

        @Override
        protected void applyValue() {}
    }
}
