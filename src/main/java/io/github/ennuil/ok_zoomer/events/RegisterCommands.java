package io.github.ennuil.ok_zoomer.events;

import com.mojang.brigadier.CommandDispatcher;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands.CommandSelection;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

public class RegisterCommands implements ClientCommandRegistrationCallback {
	@Override
	public void registerCommands(CommandDispatcher<QuiltClientCommandSource> dispatcher, CommandBuildContext buildContext, CommandSelection environment) {
		dispatcher.register(
			ClientCommandManager.literal("ok_zoomer").executes(ctx -> {
				ZoomUtils.setOpenCommandScreen(true);
				return 0;
			}
		));
	}
}
