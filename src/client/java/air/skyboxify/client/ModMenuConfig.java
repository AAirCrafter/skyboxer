package air.skyboxify.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static air.skyboxify.Config.*;

public class ModMenuConfig implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("SkyBoxer ModMenuConfig"));
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(
                entryBuilder.startIntSlider(Text.literal("Quality"), quality, 360, 3000)
                        .setDefaultValue(1080)
                        .setSaveConsumer(value -> quality = value)
                        .build()
        );

        general.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Use viewing direction"), lookDirection)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> lookDirection = value)
                        .build()
        );

        general.addEntry(
                entryBuilder.startTextField(Text.literal("Save-Location"),savePath)
                        .setDefaultValue("")
                        .setSaveConsumer(value -> savePath = value)
                        .build()
        );

        return builder.build();
    }
}