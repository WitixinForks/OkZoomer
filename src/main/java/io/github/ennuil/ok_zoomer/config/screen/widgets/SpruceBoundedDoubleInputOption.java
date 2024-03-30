package io.github.ennuil.ok_zoomer.config.screen.widgets;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceNamedTextFieldWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpruceBoundedDoubleInputOption extends SpruceOption {
	private final Supplier<Double> getter;
	private final Consumer<Double> setter;
	private final Component tooltip;
	private final Double minimum;
	private final Double maximum;

	public SpruceBoundedDoubleInputOption(String key, Double minimum, Double maximum, Supplier<Double> getter, Consumer<Double> setter, @Nullable Component tooltip) {
		super(key);
		this.minimum = minimum;
		this.maximum = maximum;
		this.getter = getter;
		this.setter = setter;
		this.tooltip = tooltip;
	}

	@Override
	public SpruceWidget createWidget(Position position, int width) {
		var textField = new SpruceTextFieldWidget(position, width, 20, this.getPrefix());
		textField.setText(String.valueOf(this.get()));
		textField.setTextPredicate(SpruceTextFieldWidget.DOUBLE_INPUT_PREDICATE);
		textField.setRenderTextProvider((displayedText, offset) -> {
			textField.setTooltip(Component.empty());
			var tooltipText = Component.empty().append(this.tooltip);
			Style tooltipStyle = Style.EMPTY;

			try {
				double value = Double.parseDouble(textField.getText());
				var bound = boundCheck(value);

				if (bound.isPresent()) {
					tooltipStyle = tooltipStyle.withColor(ChatFormatting.RED);
					if (!bound.get()) {
						boolean aboveZero = minimum == Double.MIN_NORMAL;
						tooltipText.append("\n");
						tooltipText.append(minimum == Double.MIN_VALUE
							? Component.translatable("config.ok_zoomer.widget.bounded_double.below_legal").setStyle(tooltipStyle)
							: Component.translatable("config.ok_zoomer.widget.bounded_double.below_range",
								aboveZero ? Component.translatable("config.ok_zoomer.widget.bounded_double.above_zero") : minimum.toString()).setStyle(tooltipStyle)
						);
					} else {
						tooltipText.append("\n");
						tooltipText.append(maximum == Double.MAX_VALUE
							? Component.translatable("config.ok_zoomer.widget.bounded_double.above_legal").setStyle(tooltipStyle)
							: Component.translatable("config.ok_zoomer.widget.bounded_double.above_range", maximum.toString()).setStyle(tooltipStyle)
						);
					}
				}
				textField.setTooltip(tooltipText);
				return FormattedCharSequence.forward(displayedText, tooltipStyle);
			} catch (NumberFormatException e) {
				return FormattedCharSequence.forward(displayedText, Style.EMPTY.withColor(ChatFormatting.RED));
			}
		});
		textField.setChangedListener(input -> {
			try {
				this.set(Double.parseDouble(input));
			} catch (NumberFormatException e) {
				this.set(null);
			}
		});
		this.setTooltip(this.tooltip);
		return new SpruceNamedTextFieldWidget(textField);
	}

	public void set(Double value) {
		this.setter.accept(value);
	}

	public Double get() {
		return this.getter.get();
	}

	private Optional<Boolean> boundCheck(double value) {
		if (value < minimum) {
			return Optional.of(false);
		} else if (value > maximum) {
			return Optional.of(true);
		}

		return Optional.empty();
	}
}
