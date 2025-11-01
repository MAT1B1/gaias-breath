package com.matibi.gaiasbreath.client.modmenu.config;

import com.matibi.gaiasbreath.GaiasBreath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.*;

public class GaiasBreathConfigScreen extends Screen {
    private final Screen parent;
    private final GaiasBreathConfig config;
    private final GaiasBreathConfig defaults = new GaiasBreathConfig();

    private final Map<String, List<String>> categoryMap = new LinkedHashMap<>();
    private final Map<String, TextFieldWidget> fields = new LinkedHashMap<>();
    private String activeCategory = "World Growth";

    public GaiasBreathConfigScreen(Screen parent) {
        super(Text.literal("Gaia's Breath Configuration"));
        this.parent = parent;
        this.config = GaiasBreath.CONFIG;
    }

    @Override
    protected void init() {
        fields.clear();
        categoryMap.clear();

        // === Catégories ===
        categoryMap.put("World Growth", List.of(
                "GROWTH_MAX_CHUNK_PER_TICK", "GROWTH_BLOCKS_PER_CHUNK",
                "SHORT_GRASS_GROWTH_CHANCE", "SHORT_TO_TALL_CHANCE",
                "FLOWER_SPREAD_CHANCE", "SAPLING_SPREAD_CHANCE",
                "MUSHROOM_SPREAD_CHANCE", "BUSH_SPREAD_CHANCE",
                "RAIN_GROWTH_CHANCE"
        ));
        categoryMap.put("Moss Generation", List.of(
                "STONE_TO_MOSSY_CHANCE", "MOSSY_SPREAD_CHANCE",
                "MOSSY_TO_MOSS_CHANCE", "MOSS_MAX_CHUNK_PER_TICK",
                "MOSS_BLOCKS_PER_CHUNK", "Y_RANGE"
        ));
        categoryMap.put("Path Generation", List.of(
                "STEP_TO_COARSE", "STEP_TO_PATH",
                "RECOVERY_RATE", "DECAY_INTERVAL"
        ));
        categoryMap.put("Misc", List.of("CHARCOAL_DROP_CHANCE"));

        // === Mise à l’échelle dynamique ===
        int buttonW = Math.min(width / 5, 160);
        int buttonH = 20;
        int spacing = Math.max(height / 40, 22);

        // Calcul de la colonne gauche : légèrement à gauche du centre
        int leftX = (int) (width * 0.05);
        int totalHeight = (categoryMap.size() + 2) * spacing + 10; // 4 catégories + save + cancel

        // === Boutons catégories ===
        int catY = (height - totalHeight) / 2;
        for (String cat : categoryMap.keySet()) {
            boolean active = cat.equals(activeCategory);
            addDrawableChild(ButtonWidget.builder(
                            Text.literal(cat).formatted(active ? Formatting.YELLOW : Formatting.WHITE),
                            b -> switchCategory(cat))
                    .dimensions(leftX, catY, buttonW, buttonH)
                    .build());
            catY += spacing;
        }

        // Espace entre catégories et boutons d’action
        catY += 8;

        // === Boutons Save / Cancel ===
        addDrawableChild(ButtonWidget.builder(Text.literal("Save").formatted(Formatting.GREEN), b -> {
            saveValues();
            config.save();
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(leftX, catY, buttonW, buttonH).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel").formatted(Formatting.RED), b ->
                MinecraftClient.getInstance().setScreen(parent)
        ).dimensions(leftX, catY + spacing, buttonW, buttonH).build());

        // === Champs adaptatifs à droite ===
        createFields();
    }

    private void switchCategory(String newCat) {
        activeCategory = newCat;
        clearAndInit();
    }

    private void createFields() {
        int startY = 50;
        int spacing = 24;
        int fieldX = width / 2 + 60;
        int buttonX = fieldX + 90;

        for (String fieldName : categoryMap.get(activeCategory)) {
            try {
                Field field = GaiasBreathConfig.class.getField(fieldName);
                Object value = field.get(config);

                TextFieldWidget fieldWidget = new TextFieldWidget(textRenderer, fieldX, startY, 80, 18, Text.literal(fieldName));
                fieldWidget.setText(String.valueOf(value));
                fields.put(fieldName, fieldWidget);
                addDrawableChild(fieldWidget);

                addDrawableChild(ButtonWidget.builder(Text.literal("Default").formatted(Formatting.GRAY), b -> {
                    try {
                        Object defValue = field.get(defaults);
                        fieldWidget.setText(String.valueOf(defValue));
                    } catch (Exception ignored) {}
                }).dimensions(buttonX, startY, 60, 18).build());

                startY += spacing;
            } catch (Exception e) {
                GaiasBreath.LOGGER.error("Error creating field for {}", fieldName, e);
            }
        }
    }

    private void saveValues() {
        for (var entry : fields.entrySet()) {
            String name = entry.getKey();
            String text = entry.getValue().getText();
            try {
                Field field = GaiasBreathConfig.class.getField(name);
                Class<?> type = field.getType();

                if (type == int.class) {
                    field.setInt(config, Integer.parseInt(text));
                } else if (type == float.class) {
                    field.setFloat(config, Float.parseFloat(text));
                }
            } catch (Exception e) {
                GaiasBreath.LOGGER.error("Invalid value for {}: {}", name, text);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, width, height, 0xAA000000, 0x66000000);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFFFF);

        int y = 50;
        for (String fieldName : categoryMap.get(activeCategory)) {
            context.drawTextWithShadow(textRenderer, prettifyName(fieldName), width / 2 - 100, y + 4, 0xC0C0C0C0);
            y += 24;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private String prettifyName(String name) {
        return name.replace("_", " ")
                .toLowerCase(Locale.ROOT).replaceFirst(".", name.substring(0, 1));
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}