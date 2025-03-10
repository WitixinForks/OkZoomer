package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import net.minecraft.client.player.AbstractClientPlayer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
	@ModifyExpressionValue(
		method = "getFieldOfViewModifier",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isScoping()Z")
	)
	private boolean replaceSpyglassPlayerMovement(boolean isScoping) {
		if (switch (OkZoomerConfigManager.CONFIG.features.spyglassMode.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isScoping;
	}
}
