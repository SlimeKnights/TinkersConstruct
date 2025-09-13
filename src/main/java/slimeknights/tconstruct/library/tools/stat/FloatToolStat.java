package slimeknights.tconstruct.library.tools.stat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;

/**
 * Tool stat representing a float value, used for most numbers
 */
public class FloatToolStat implements INumericToolStat<Float> {
  /** Name of this tool stat */
  @Getter
  private final ToolStatId name;
  /** Color for this stat type */
  @Getter
  private final TextColor color;
  /** Gets the default value for this stat */
  private final float defaultValue;
  /** Min value for this stat */
  @Getter
  private final float minValue;
  /** Max value for this stat */
  @Getter
  private final float maxValue;
  private final IJsonPredicate<Item> items;

  public FloatToolStat(ToolStatId name, int color, float defaultValue, float minValue, float maxValue, IJsonPredicate<Item> items) {
    this.name = name;
    this.color = TextColor.fromRgb(color);
    this.defaultValue = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.items = items;
  }

  public FloatToolStat(ToolStatId name, int color, float defaultValue, float minValue, float maxValue, @Nullable TagKey<Item> tag) {
    this(name, color, defaultValue, minValue, maxValue, tag == null ? ItemPredicate.ANY : ItemPredicate.tag(tag));
  }

  public FloatToolStat(ToolStatId name, int color, float defaultValue, float minValue, float maxValue) {
    this(name, color, defaultValue, minValue, maxValue, ItemPredicate.ANY);
  }

  @Override
  public boolean supports(Item item) {
    return items.matches(item);
  }

  @Override
  public Float getDefaultValue() {
    return defaultValue;
  }

  @Override
  public Float clamp(Float value) {
    return Mth.clamp(value, getMinValue(), getMaxValue());
  }

  @Override
  public FloatBuilder makeBuilder() {
    return new FloatBuilder(defaultValue);
  }

  @Override
  public void update(ModifierStatsBuilder builder, Float value) {
    builder.<FloatBuilder>updateStat(this, b -> {
      b.add += value;
      b.base = 0;
    });
  }

  @Override
  public void add(ModifierStatsBuilder builder, double value) {
    builder.<FloatBuilder>updateStat(this, b -> b.add += value);
  }

  @Override
  public void percent(ModifierStatsBuilder builder, double factor) {
    builder.<FloatBuilder>updateStat(this, b -> b.percent += factor);
  }

  @Override
  public void multiply(ModifierStatsBuilder builder, double factor) {
    builder.<FloatBuilder>updateStat(this, b -> b.multiply *= factor);
  }

  @Override
  public void multiplyAll(ModifierStatsBuilder builder, double factor) {
    builder.<FloatBuilder>updateStat(this, b -> b.multiply *= factor);
    builder.multiplier(this, factor);
  }

  @Override
  public Float build(ModifierStatsBuilder parent, Object builderObj) {
    FloatBuilder builder = (FloatBuilder)builderObj;
    return (builder.base + builder.add) * (1 + builder.percent) * builder.multiply;
  }

  @Nullable
  @Override
  public Float read(Tag tag) {
    if (TagUtil.isNumeric(tag)) {
      return ((NumericTag) tag).getAsFloat();
    }
    return null;
  }

  @Override
  public Tag write(Float value) {
    return FloatTag.valueOf(value);
  }

  @Override
  public Float deserialize(JsonElement json) {
    return GsonHelper.convertToFloat(json, getName().toString());
  }

  @Override
  public JsonElement serialize(Float value) {
    return new JsonPrimitive(value);
  }

  @Override
  public Float fromNetwork(FriendlyByteBuf buffer) {
    return buffer.readFloat();
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer, Float value) {
    buffer.writeFloat(value);
  }

  /** Generic friendly way to format the value */
  @Override
  public Component formatValue(float value) {
    return IToolStat.formatNumber(getTranslationKey(), getColor(), value);
  }

  @Override
  public String toString() {
    return "FloatToolStat{" + name + '}';
  }

  /** Internal builder to store the add and multiply value */
  protected static class FloatBuilder {
    /** Base value of the stat, may get zeroed out */
    private float base;
    /** Value summed with the base, applies first */
    private float add = 0;
    /** Percent multiplier, applies second */
    private float percent = 0;
    /** Value multiplied by the sum, applies second */
    private float multiply = 1;

    public FloatBuilder(float base) {
      this.base = base;
    }
  }
}
