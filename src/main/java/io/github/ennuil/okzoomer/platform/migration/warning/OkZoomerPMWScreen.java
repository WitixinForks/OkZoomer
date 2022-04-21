package io.github.ennuil.okzoomer.platform.migration.warning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.SpruceSeparatorOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.github.ennuil.okzoomer.config.screen.SpruceLabelOption;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue.CvObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

// TODO - This sunset screen should be a library mod
public class OkZoomerPMWScreen extends SpruceScreen {
	private final Screen parent;
	private SpruceOptionListWidget list;

	public OkZoomerPMWScreen(Screen parent) {
		super(new TranslatableText("platform_migration_warning.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 36 - 22);

		var explainerLabel = new SpruceLabelOption("platform_migration_warning.explainer", true);
		this.list.addSingleOptionEntry(explainerLabel);

		Map<String, ModDeveloper> modDevelopers = new HashMap<>();
		FabricLoader.getInstance().getAllMods().stream().filter(mod -> mod.getMetadata().containsCustomValue("platform_migration_warning")).forEach(mod -> {
			CvObject fabricSunset = mod.getMetadata().getCustomValue("platform_migration_warning").getAsObject();
			var modText = new LiteralText(fabricSunset.get("name").getAsString())
				.styled(style -> style.withColor(0xFFFFFF));
			var migratedSinceText = new TranslatableText("platform_migration_warning.mod.migrated_since", new LiteralText(fabricSunset.get("migrated_since").getAsString()));
			var modLabel = new SpruceLabelOption("platform_migration_warning.id_" + mod.getMetadata().getId(), modText, true, migratedSinceText);
			this.list.addSingleOptionEntry(modLabel);

			String author = fabricSunset.get("author").getAsString();
			if (!modDevelopers.containsKey(author)) {
				List<Text> modList = new ArrayList<>();
				modList.add(new LiteralText(fabricSunset.get("name").getAsString()));
				modDevelopers.put(author, new ModDeveloper(author, modList));
			} else {
				modDevelopers.get(author).mods().add(new LiteralText(fabricSunset.get("name").getAsString()));
			}
		});

		var explainerLabel2 = new SpruceLabelOption("platform_migration_warning.explainer2", true);
		this.list.addSingleOptionEntry(explainerLabel2);

		for (ModDeveloper modDeveloper : modDevelopers.values()) {
			String authorKey = String.format("platform_migration_warning.%s.author", modDeveloper.author);
			String testimonialKey = String.format("platform_migration_warning.%s.testimonial", modDeveloper.author);
			Text hoverText = switch (modDeveloper.mods.size()) {
				case 0 -> null;
				case 1 -> new TranslatableText("platform_migration_warning.has_developed_1", modDeveloper.mods.get(0));
				case 2 -> new TranslatableText("platform_migration_warning.has_developed_2", modDeveloper.mods.get(0), modDeveloper.mods.get(1));
				default -> {
					MutableText mutableText = new TranslatableText("platform_migration_warning.has_developed_many");
					for (Text text : modDeveloper.mods) {
						mutableText.append(new TranslatableText("platform_migration_warning.has_developed_many_entry", text));
					}
					yield mutableText;
				}
			};

			var restrictionsSeparator = new SpruceSeparatorOption(authorKey, true, hoverText);
			var testimonialLabel = new SpruceLabelOption(testimonialKey, new TranslatableText(testimonialKey).styled(style -> style.withColor(0xDEDEDE)), true);

			this.list.addSingleOptionEntry(restrictionsSeparator);
			this.list.addSingleOptionEntry(testimonialLabel);
		}

		//this.list.setBackground(darkenedBackground);

		this.addDrawableChild(this.list);
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 154, this.height - 28), 150, 20, new TranslatableText("platform_migration_warning.open_quilt_website"),
			btn -> Util.getOperatingSystem().open("https://quiltmc.org")).asVanilla());
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 + 4, this.height - 28), 150, 20, SpruceTexts.GUI_DONE,
			btn -> this.client.setScreen(this.parent)).asVanilla());
	}

	@Override
	public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		this.client.setScreen(this.parent);
	}

	public record ModDeveloper(String author, List<Text> mods) {}
}
