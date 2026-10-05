package air.skyboxify.client;

import net.fabricmc.api.ClientModInitializer;

public class SkyboxifyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Keybinds.register();
    }
}
