package air.skyboxify.client.mixin;

import air.skyboxify.client.Screenshotter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Unique
    private static final String SAVE = "Lnet/minecraft/client/util/ScreenshotRecorder;saveScreenshot(Ljava/io/File;Ljava/lang/String;Lnet/minecraft/client/gl/Framebuffer;ILjava/util/function/Consumer;)V";

    @ModifyArg(method = "takePanorama", at = @At(value = "INVOKE", target = SAVE), index = 1)
    private String air$panoramaFolder(String fileName) {
        return Screenshotter.panoramaFolder == null ? fileName : Screenshotter.panoramaFolder + "/" + fileName;
    }

    @ModifyArg(method = "takePanorama", at = @At(value = "INVOKE", target = SAVE), index = 3)
    private int air$panoramaDownscale(int original) {
        return Screenshotter.panoramaFolder == null ? original : Screenshotter.supersample();
    }

    @ModifyConstant(method = "takePanorama", constant = @Constant(intValue = 4096))
    private int air$panoramaSize(int original) {
        return Screenshotter.panoramaFolder == null ? original : Screenshotter.renderSize();
    }

    @Inject(method = "takePanorama", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;resize(II)V", shift = At.Shift.AFTER))
    private void air$panoramaResize(File directory, CallbackInfoReturnable<Text> cir) {
        if (Screenshotter.panoramaFolder != null) ((MinecraftClient) (Object) this).onResolutionChanged();
    }

    @Redirect(method = "takePanorama", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V"))
    private void air$panoramaWarmup(GameRenderer renderer, RenderTickCounter counter) {
        if (Screenshotter.panoramaFolder == null) renderer.renderWorld(counter);
        else Screenshotter.renderFrames(() -> renderer.renderWorld(counter));
    }
}
