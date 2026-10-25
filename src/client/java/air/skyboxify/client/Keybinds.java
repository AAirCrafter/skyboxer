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
    public static KeyBinding panorama;

    public static void register() {
        createScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.skyboxify.screenshot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSPACE,
                category
        ));

        panorama = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.skyboxify.3dscreenshot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_APOSTROPHE,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (createScreen.wasPressed()) {
                Screenshotter.shoot();
            }

            while (panorama.wasPressed()) {
                Screenshotter.shoot3D();
            }
        });
    }
}
