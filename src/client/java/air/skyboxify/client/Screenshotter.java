package air.skyboxify.client;

import air.skyboxify.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Screenshotter {
    public static String panoramaFolder = null;
    public static final int WARMUP_FRAMES = 8;
    public static final long MAX_CHUNK_WAIT_MS = 3000;
    public static final int STABLE_FRAMES = 10;

    public static void renderFrames(Runnable frame) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.worldRenderer.getChunkRenderingDataPreparer().scheduleTerrainUpdate();
        long end = System.currentTimeMillis() + MAX_CHUNK_WAIT_MS;

        int i = 0;
        int stable = 0;
        while (System.currentTimeMillis() < end) {
            frame.run();
            i++;
            stable = client.worldRenderer.isTerrainRenderComplete() ? stable + 1 : 0;
            if (i >= WARMUP_FRAMES && stable >= STABLE_FRAMES) break;
            try { Thread.sleep(5); } catch (InterruptedException ignored) {}
        }
    }

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
        client.player.sendMessage(Text.literal("[Skyboxer] ").withColor(0x9ac2e7)
                .append(Text.translatable("screenshot.success",link).withColor(0xFFFFFF)),false);
    }

    public static void shoot() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        File dir = Config.savePath.isBlank() ? client.runDirectory : new File(Config.savePath);
        String name = getTimeName() + ".png";
        new File(dir,"screenshots").mkdirs();

        Window window = client.getWindow();
        Framebuffer framebuffer = client.getFramebuffer();
        int oW = window.getFramebufferWidth();
        int oH = window.getFramebufferHeight();

        int w = Config.quality;
        int h = Math.round(w * (float) oH / oW);
        int ss = supersample(Math.max(w,h));
        int oS = window.getScaleFactor();

        try {
            window.setFramebufferWidth(w * ss);
            window.setFramebufferHeight(h * ss);
            client.onResolutionChanged();

            if (Config.renderUI) window.setScaleFactor(Math.max(1, Math.round(oS * (float) (h * ss) / oH)));

            renderFrames(() -> {
                if (Config.renderUI) client.gameRenderer.render(RenderTickCounter.ONE, true);
                else client.gameRenderer.renderWorld(RenderTickCounter.ONE);
            });

            ScreenshotRecorder.saveScreenshot(dir, name, framebuffer, ss, message -> {});
        } finally {
            window.setFramebufferWidth(oW);
            window.setFramebufferHeight(oH);
            client.onResolutionChanged();
        }

        Text link = Text.literal(name).formatted(Formatting.UNDERLINE).styled(s -> s.withClickEvent(new ClickEvent.OpenFile(new File(dir,"screenshots/" + name))));
        client.player.sendMessage(Text.literal("[Skyboxer] ").withColor(0x9ac2e7)
                .append(Text.translatable("screenshot.success",link).withColor(0xFFFFFF)),false);
    }

    public static int supersample() {
        return supersample(Config.quality);
    }

    public static int supersample(int size) {
        return Math.max(1, Math.min(4, 8192 / size));
    }

    public static int renderSize() {
        return Config.quality * supersample();
    }
}
