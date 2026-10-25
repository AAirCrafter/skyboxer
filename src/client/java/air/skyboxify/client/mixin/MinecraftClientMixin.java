package air.skyboxify.client.mixin;

import air.skyboxify.client.Screenshotter;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @ModifyArg(method = "takePanorama", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ScreenshotRecorder;saveScreenshot(Ljava/io/File;Ljava/lang/String;Lnet/minecraft/client/gl/Framebuffer;ILjava/util/function/Consumer;)V"),index = 1)
    private String air$panoramaFolder(String fileName) {
        return Screenshotter.panoramaFolder == null ? fileName : Screenshotter.panoramaFolder + "/" + fileName;
    }
}
