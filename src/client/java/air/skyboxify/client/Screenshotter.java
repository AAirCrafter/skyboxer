package air.skyboxify.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;

public class Screenshotter {
    public static void shoot3D() {

    }

    public static void shoot() {
        MinecraftClient client = MinecraftClient.getInstance();

        ScreenshotRecorder.takeScreenshot(client.getFramebuffer(), image -> {
            try (image) {
                image.writeTo(new File("screenshot.png"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        });
        if (client.player != null) client.player.sendMessage(Text.literal("Screenshot saved!"),false);
    }
}
