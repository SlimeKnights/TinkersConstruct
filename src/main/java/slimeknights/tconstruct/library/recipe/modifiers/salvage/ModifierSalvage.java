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
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Recipe representing the salvage value for removing a modifier
 */
public class ModifierSalvage extends AbstractModifierSalvage {
  /** List of random items that may be returned by this */
  private final List<RandomItem> result;

  public ModifierSalvage(ResourceLocation id, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, List<RandomItem> result, @Nullable SlotCount slots) {
    this(id, toolIngredient, ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE, modifier, minLevel, maxLevel, result, slots);
  }

  public ModifierSalvage(ResourceLocation id, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, List<RandomItem> result, @Nullable SlotCount slots) {
    super(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, slots);
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
    protected ModifierSalvage read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      List<RandomItem> result = ImmutableList.of();
      if (json.has("salvage")) {
        result = JsonHelper.parseList(json, "salvage", RandomItem::fromJson);
      }
      return new ModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, result, slots);
    }

    @Override
    protected ModifierSalvage read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      ImmutableList.Builder<RandomItem> result = ImmutableList.builder();
      int count = buffer.readVarInt();
      for (int i = 0; i < count; i++) {
        result.add(RandomItem.read(buffer));
      }
      return new ModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, result.build(), slots);
    }

    @Override
    protected ModifierSalvage read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected ModifierSalvage read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      throw new UnsupportedOperationException();
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
