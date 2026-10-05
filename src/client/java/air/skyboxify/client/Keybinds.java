package air.skyboxify.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    private static final KeyBinding.Category category = new KeyBinding.Category(Identifier.of("skyboxify"));
    public static KeyBinding createScreen;

    public static void register() {
        createScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.skyboxify.airgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSPACE,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (createScreen.wasPressed()) {
                //Screenshotter.shoot3D();
                Screenshotter.shoot();
            }
        });
    }
}
