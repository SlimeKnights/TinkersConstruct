package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import slimeknights.tconstruct.compat.minecraft.world.item.alchemy.PotionUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/** Common logic for subtype interpreter between the fluid and item form of our potion. Based on a JEI class with the same name */
public interface PotionSubtypeInterpreter<T> extends IIngredientSubtypeInterpreter<T> {
  @Nullable
  CompoundTag getTag(T ingredient);

  @Override
  default String apply(T ingredient, UidContext context) {
    CompoundTag tag = getTag(ingredient);
    if (tag == null) {
      return IIngredientSubtypeInterpreter.NONE;
    }
    Holder<Potion> potionType = PotionUtils.getPotion(tag);
    String potionTypeString = Potion.getName(Optional.of(potionType), "");
    StringBuilder stringBuilder = new StringBuilder(potionTypeString);
    List<MobEffectInstance> effects = PotionUtils.getAllEffects(tag);
    for (MobEffectInstance effect : effects) {
      stringBuilder.append(";").append(effect);
    }
    return stringBuilder.toString();
  }
}
