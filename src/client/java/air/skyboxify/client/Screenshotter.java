package air.skyboxify.client;

import air.skyboxify.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Screenshotter {
    public static String panoramaFolder = null;

    public static String getTimeName() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return f.format(cal.getTime());
    }

    public static void shoot3D() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        File dir = Config.savePath.isBlank() ? client.runDirectory : new File(Config.savePath);
        String name = getTimeName();
        File folder = new File(dir, "screenshots/" + name);
        folder.mkdirs();

        float yaw = client.player.getYaw();
        if (!Config.lookDirection) client.player.setYaw(0);

        panoramaFolder = name;
        client.takePanorama(dir);
        panoramaFolder = null;

        client.player.setYaw(yaw);

        Text link = Text.literal(name).formatted(Formatting.UNDERLINE).styled(s -> s.withClickEvent(new ClickEvent.OpenFile(folder.getAbsolutePath())));
        client.player.sendMessage(Text.translatable("screenshot.success",link),false);
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
