package slimeknights.tconstruct.library.tools.stat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.utils.HarvestTiers;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Objects;

/** Tool stat for comparing tool tiers */
@SuppressWarnings("ClassCanBeRecord")
@Getter @RequiredArgsConstructor
public class ToolTierStat implements IToolStat<Tier> {
  /** Name of this tool stat */
  private final ToolStatId name;

  @Override
  public boolean supports(Item item) {
    return RegistryHelper.contains(TinkerTags.Items.HARVEST, item);
  }

  @Override
  public Tier getDefaultValue() {
    return HarvestTiers.minTier();
  }

  @Override
  public Object makeBuilder() {
    return new TierBuilder(getDefaultValue());
  }

  @Override
  public Tier build(Object builder, Tier value) {
    return HarvestTiers.max(((TierBuilder) builder).value, value);
  }

  /**
   * Sets the tier to the new tier, keeping the largest
   * @param builder  Builder instance
   * @param value    Amount to add
   */
  @Override
  public void update(ModifierStatsBuilder builder, Tier value) {
    builder.<TierBuilder>updateStat(this, b -> b.value = HarvestTiers.max(b.value, value));
  }

  @Nullable
  @Override
  public Tier read(Tag tag) {
    if (tag.getId() == Tag.TAG_STRING) {
      ResourceLocation tierId = ResourceLocation.tryParse(tag.getAsString());
      if (tierId != null) {
        return TierSortingRegistry.byName(tierId);
      }
    }
    return null;
  }

  @Override
  public Tag write(Tier value) {
    ResourceLocation id = TierSortingRegistry.getName(value);
    if (id != null) {
      return StringTag.valueOf(id.toString());
    }
    return null;
  }

  @Override
  public Tier deserialize(JsonElement json) {
    ResourceLocation id = JsonHelper.convertToResourceLocation(json, getName().toString());
    Tier tier = TierSortingRegistry.byName(id);
    if (tier != null) {
      return tier;
    }
    throw new JsonSyntaxException("Unknown tool tier " + id);
  }

  @Override
  public JsonElement serialize(Tier value) {
    return new JsonPrimitive(Objects.requireNonNull(TierSortingRegistry.getName(value)).toString());
  }

  @Override
  public Tier fromNetwork(FriendlyByteBuf buffer) {
    ResourceLocation id = buffer.readResourceLocation();
    Tier tier = TierSortingRegistry.byName(id);
    if (tier != null) {
      return tier;
    }
    throw new DecoderException("Unknown tool tier " + id);
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer, Tier value) {
    buffer.writeResourceLocation(Objects.requireNonNull(TierSortingRegistry.getName(value)));
  }

  @Override
  public Component formatValue(Tier value) {
    return new TranslatableComponent(Util.makeTranslationKey("tool_stat", getName())).append(HarvestTiers.getName(value));
  }

  @Override
  public String toString() {
    return "ToolTierStat{" + name + '}';
  }

  /** Builder for a tier object */
  @AllArgsConstructor
  private static class TierBuilder {
    private Tier value;
  }
}
