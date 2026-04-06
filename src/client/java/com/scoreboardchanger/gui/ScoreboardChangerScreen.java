package com.scoreboardchanger.gui;

import com.scoreboardchanger.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardChangerScreen extends Screen {

    private final List<TextFieldWidget> fields = new ArrayList<>();

    // Labels and matching rank colors
    private static final String[] RANK_COLORS = {"§f", "§a", "§b", "§e", "§6", "§c", "§4", "§d", "§5", "§9"};
    private static final String[] RANK_COLOR_NAMES = {"Белый", "Зелёный", "Голубой", "Жёлтый", "Золотой", "Красный", "Тёмно-красный", "Розовый", "Фиолетовый", "Синий"};

    private TextFieldWidget nicknameField;
    private TextFieldWidget rankField;
    private TextFieldWidget coinsField;
    private TextFieldWidget tokensField;
    private TextFieldWidget skullsField;
    private TextFieldWidget killsField;
    private TextFieldWidget deathsField;
    private TextFieldWidget playtimeField;

    private int selectedColorIndex = 0;

    public ScoreboardChangerScreen() {
        super(Text.literal("Scoreboard Changer"));
    }

    @Override
    protected void init() {
        ModConfig cfg = ModConfig.getInstance();

        // Find current color index
        for (int i = 0; i < RANK_COLORS.length; i++) {
            if (RANK_COLORS[i].equals(cfg.fakeRankColor)) {
                selectedColorIndex = i;
                break;
            }
        }

        int startX = this.width / 2 - 200;
        int startY = 30;
        int fieldWidth = 160;
        int fieldHeight = 20;
        int gap = 28;

        // Nickname
        addLabel("Никнейм:", startX, startY + 4);
        nicknameField = addField(startX + 100, startY, fieldWidth, fieldHeight, cfg.fakeNickname);

        // Rank
        addLabel("Ранг:", startX, startY + gap + 4);
        rankField = addField(startX + 100, startY + gap, fieldWidth, fieldHeight, cfg.fakeRank);

        // Rank color button
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Цвет ранга: " + RANK_COLOR_NAMES[selectedColorIndex]),
                btn -> {
                    selectedColorIndex = (selectedColorIndex + 1) % RANK_COLORS.length;
                    btn.setMessage(Text.literal("Цвет ранга: " + RANK_COLOR_NAMES[selectedColorIndex]));
                })
                .dimensions(startX + 100, startY + gap * 2, fieldWidth, fieldHeight)
                .build());
        addLabel("Цвет ранга:", startX, startY + gap * 2 + 4);

        // Coins
        addLabel("Монеты:", startX, startY + gap * 3 + 4);
        coinsField = addField(startX + 100, startY + gap * 3, fieldWidth, fieldHeight, cfg.fakeCoins);

        // Tokens
        addLabel("Токены:", startX, startY + gap * 4 + 4);
        tokensField = addField(startX + 100, startY + gap * 4, fieldWidth, fieldHeight, cfg.fakeTokens);

        // Skulls
        addLabel("Черепки:", startX, startY + gap * 5 + 4);
        skullsField = addField(startX + 100, startY + gap * 5, fieldWidth, fieldHeight, cfg.fakeSkulls);

        // Kills
        addLabel("Убийства:", startX + 210, startY + 4);
        killsField = addField(startX + 310, startY, fieldWidth, fieldHeight, cfg.fakeKills);

        // Deaths
        addLabel("Смерти:", startX + 210, startY + gap + 4);
        deathsField = addField(startX + 310, startY + gap, fieldWidth, fieldHeight, cfg.fakeDeaths);

        // Playtime
        addLabel("Наиграно:", startX + 210, startY + gap * 2 + 4);
        playtimeField = addField(startX + 310, startY + gap * 2, fieldWidth, fieldHeight, cfg.fakePlaytime);

        // Enable / disable toggle
        addDrawableChild(ButtonWidget.builder(
                Text.literal(cfg.enabled ? "§aВключено" : "§cВыключено"),
                btn -> {
                    cfg.enabled = !cfg.enabled;
                    btn.setMessage(Text.literal(cfg.enabled ? "§aВключено" : "§cВыключено"));
                })
                .dimensions(this.width / 2 - 80, this.height - 60, 160, 20)
                .build());

        // Save button
        addDrawableChild(ButtonWidget.builder(Text.literal("Сохранить"), btn -> save())
                .dimensions(this.width / 2 - 80, this.height - 35, 75, 20)
                .build());

        // Close button
        addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), btn -> close())
                .dimensions(this.width / 2 + 5, this.height - 35, 75, 20)
                .build());
    }

    private void addLabel(String text, int x, int y) {
        // Labels are rendered in render()
    }

    // Store label data for rendering
    private final List<Object[]> labels = new ArrayList<>();

    private TextFieldWidget addField(int x, int y, int width, int height, String value) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, x, y, width, height, Text.empty());
        field.setText(value);
        field.setMaxLength(64);
        addDrawableChild(field);
        fields.add(field);
        return field;
    }

    private void save() {
        ModConfig cfg = ModConfig.getInstance();
        cfg.fakeNickname = nicknameField.getText();
        cfg.fakeRank = rankField.getText();
        cfg.fakeRankColor = RANK_COLORS[selectedColorIndex];
        cfg.fakeCoins = coinsField.getText();
        cfg.fakeTokens = tokensField.getText();
        cfg.fakeSkulls = skullsField.getText();
        cfg.fakeKills = killsField.getText();
        cfg.fakeDeaths = deathsField.getText();
        cfg.fakePlaytime = playtimeField.getText();
        ModConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);

        // Draw labels manually since we can't do it in init properly
        int startX = this.width / 2 - 200;
        int startY = 30;
        int gap = 28;

        context.drawTextWithShadow(textRenderer, "Никнейм:", startX, startY + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Ранг:", startX, startY + gap + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Цвет ранга:", startX, startY + gap * 2 + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Монеты:", startX, startY + gap * 3 + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Токены:", startX, startY + gap * 4 + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Черепки:", startX, startY + gap * 5 + 6, 0xAAAAAA);

        context.drawTextWithShadow(textRenderer, "Убийства:", startX + 210, startY + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Смерти:", startX + 210, startY + gap + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Наиграно:", startX + 210, startY + gap * 2 + 6, 0xAAAAAA);

        // Preview of rank color
        ModConfig cfg = ModConfig.getInstance();
        String preview = RANK_COLORS[selectedColorIndex] + rankField.getText();
        context.drawTextWithShadow(textRenderer, Text.literal(preview), startX + 270, startY + gap + 6, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }
}
