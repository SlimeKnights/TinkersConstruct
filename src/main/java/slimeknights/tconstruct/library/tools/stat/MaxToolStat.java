package slimeknights.tconstruct.library.tools.stat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.function.IntFunction;

/**
 * Tool stat that keeps the largest integer value given
 */
@AllArgsConstructor
public class MaxToolStat implements IToolStat<Integer> {
  @Getter
  private final ToolStatId name;
  private final int defaultValue;
  private final IntFunction<Component> displayName;
  @Nullable
  private final TagKey<Item> tag;

  public MaxToolStat(ToolStatId name, int defaultValue, IntFunction<Component> displayName) {
    this(name, defaultValue, displayName, null);
  }

  @Override
  public boolean supports(Item item) {
    return tag == null || RegistryHelper.contains(tag, item);
  }

  @Override
  public Integer getDefaultValue() {
    return defaultValue;
  }

  @Override
  public Integer clamp(Integer value) {
    return Mth.clamp(value, 0, Integer.MAX_VALUE);
  }

  @Override
  public TierBuilder makeBuilder() {
    return new TierBuilder();
  }

  /**
   * Sets the tier to the new tier, keeping the largest
   * @param builder  Builder
   * @param value    Value
   */
  @Override
  public void update(ModifierStatsBuilder builder, Integer value) {
    builder.<TierBuilder>updateStat(this, b -> b.tier = Math.max(b.tier, value));
  }

  @Override
  public Integer build(Object builder, Integer value) {
    return Math.max(value, ((TierBuilder)builder).tier);
  }

  @Nullable
  @Override
  public Integer read(Tag tag) {
    if (TagUtil.isNumeric(tag)) {
      return ((NumericTag)tag).getAsInt();
    }
    return null;
  }

  @Nullable
  @Override
  public Tag write(Integer value) {
    return IntTag.valueOf(value);
  }

  @Override
  public Integer deserialize(JsonElement json) {
    return GsonHelper.convertToInt(json, getName().toString());
  }

  @Override
  public JsonElement serialize(Integer value) {
    return new JsonPrimitive(value);
  }

  @Override
  public Integer fromNetwork(FriendlyByteBuf buffer) {
    return buffer.readVarInt();
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer, Integer value) {
    buffer.writeVarInt(value);
  }

  @Override
  public Component formatValue(Integer number) {
    return Component.translatable(Util.makeTranslationKey("tool_stat", getName())).append(displayName.apply(number));
  }

  @Override
  public String toString() {
    return "TierToolStat{" + name + '}';
  }

  /** Internal int storage, basically just a int wrapper */
  protected static class TierBuilder {
    private int tier = 0;
  }
}
