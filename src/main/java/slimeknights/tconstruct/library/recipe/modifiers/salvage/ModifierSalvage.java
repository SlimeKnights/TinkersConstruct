package slimeknights.tconstruct.library.recipe.modifiers.salvage;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.RandomItem;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Recipe representing the salvage value for removing a modifier
 */
public class ModifierSalvage extends AbstractModifierSalvage {
  /** List of random items that may be returned by this */
  private final List<RandomItem> result;

  public ModifierSalvage(ResourceLocation id, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, List<RandomItem> result, int upgradeSlots, int abilitySlots) {
    super(id, toolIngredient, modifier, minLevel, maxLevel, upgradeSlots, abilitySlots);
    this.result = result;
    ModifierRecipeLookup.addSalvage(this);
  }

  @Override
  public void acceptItems(IModifierToolStack tool, Consumer<ItemStack> stackConsumer, Random random) {
    for (RandomItem randomItem : result) {
      ItemStack stack = randomItem.get(random);
      if (!stack.isEmpty()) {
        stackConsumer.accept(stack);
      }
    }
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSalvageSerializer.get();
  }

  /** Serializer instance */
  public static class Serializer extends AbstractModifierSalvage.AbstractSerializer<ModifierSalvage> {
    @Override
    protected ModifierSalvage read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, int upgradeSlots, int abilitySlots) {
      List<RandomItem> result = ImmutableList.of();
      if (json.has("salvage")) {
        result = JsonHelper.parseList(json, "salvage", RandomItem::fromJson);
      }
      return new ModifierSalvage(id, toolIngredient, modifier, minLevel, maxLevel, result, upgradeSlots, abilitySlots);
    }

    @Override
    protected ModifierSalvage read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, int upgradeSlots, int abilitySlots) {
      ImmutableList.Builder<RandomItem> result = ImmutableList.builder();
      for (int i = 0; i < buffer.readVarInt(); i++) {
        result.add(RandomItem.read(buffer));
      }
      return new ModifierSalvage(id, toolIngredient, modifier, minLevel, maxLevel, result.build(), upgradeSlots, abilitySlots);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, ModifierSalvage recipe) {
      super.writeSafe(buffer, recipe);
      buffer.writeVarInt(recipe.result.size());
      for (RandomItem randomItem : recipe.result) {
        randomItem.write(buffer);
      }
    }
  }
}
