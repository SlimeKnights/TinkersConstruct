package slimeknights.tconstruct.compat.minecraft.world.item.alchemy;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;

public final class PotionUtils {
  public static final String TAG_POTION = "Potion";

  private PotionUtils() {}

  public static Holder<Potion> getPotion(ItemStack stack) {
    PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    return contents.potion().orElse(Potions.WATER);
  }

  public static Holder<Potion> getPotion(CompoundTag tag) {
    return readPotion(tag);
  }

  public static ItemStack setPotion(ItemStack stack, Holder<Potion> potion) {
    stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(TAG_POTION, potion.unwrapKey().map(key -> key.location().toString()).orElse("minecraft:water")));
    return stack;
  }

  public static ItemStack setPotion(ItemStack stack, Potion potion) {
    return setPotion(stack, BuiltInRegistries.POTION.wrapAsHolder(potion));
  }

  public static List<MobEffectInstance> getMobEffects(ItemStack stack) {
    PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    List<MobEffectInstance> effects = new ArrayList<>();
    contents.getAllEffects().forEach(effects::add);
    return List.copyOf(effects);
  }

  public static List<MobEffectInstance> getAllEffects(CompoundTag tag) {
    return List.copyOf(readPotion(tag).value().getEffects());
  }

  public static int getColor(ItemStack stack) {
    PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    return contents.getColor();
  }

  public static int getColor(Holder<Potion> potion) {
    return PotionContents.getColor(potion);
  }

  public static int getColor(Potion potion) {
    return PotionContents.getColor(potion.getEffects());
  }

  public static int getColor(Iterable<MobEffectInstance> effects) {
    return PotionContents.getColor(effects);
  }

  public static void addPotionTooltip(ItemStack stack, List<Component> tooltip, float durationFactor) {
    PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    contents.addPotionTooltip(tooltip::add, durationFactor, 20.0F);
  }

  public static void addPotionTooltip(Iterable<MobEffectInstance> effects, List<Component> tooltip, float durationFactor) {
    PotionContents.addPotionTooltip(effects, tooltip::add, durationFactor, 20.0F);
  }

  private static Holder<Potion> readPotion(CompoundTag tag) {
    if (tag != null && tag.contains(TAG_POTION)) {
      ResourceLocation id = ResourceLocation.tryParse(tag.getString(TAG_POTION));
      if (id != null) {
        return BuiltInRegistries.POTION.getHolder(id).<Holder<Potion>>map(holder -> holder).orElse(Potions.WATER);
      }
    }
    return Potions.WATER;
  }
}
