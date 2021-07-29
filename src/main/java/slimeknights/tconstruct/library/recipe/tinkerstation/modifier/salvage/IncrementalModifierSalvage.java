package slimeknights.tconstruct.library.recipe.tinkerstation.modifier.salvage;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Recipe that gives variable salvage based on the amount of a modifier on the tool
 */
public class IncrementalModifierSalvage extends AbstractModifierSalvage {
  private final ItemOutput result;
  public IncrementalModifierSalvage(ResourceLocation id, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, ItemOutput result, int upgradeSlots, int abilitySlots) {
    super(id, toolIngredient, modifier, minLevel, maxLevel, upgradeSlots, abilitySlots);
    this.result = result;
    ModifierRecipeLookup.addSalvage(this);
  }

  @Override
  public void updateTool(IModifierToolStack tool) {
    super.updateTool(tool);
    // remove the incremental modifier tag so we don't partially remove the next level
    tool.getPersistentData().remove(getModifier().getId());
  }

  @Override
  public void acceptItems(IModifierToolStack tool, Consumer<ItemStack> stackConsumer, Random random) {
    ResourceLocation key = getModifier().getId();
    int maxValue;
    // if the tag is missing, return the needed per level (assume its being treated as non-incremental)
    if (tool.getPersistentData().contains(key, NBT.TAG_ANY_NUMERIC)) {
      maxValue = tool.getPersistentData().getInt(getModifier().getId());
    } else {
      maxValue = ModifierRecipeLookup.getNeededPerLevel(getModifier());
    }
    // add the items returned
    if (maxValue > 0) {
      int amount = random.nextInt(maxValue);
      if (amount > 0) {
        ItemStack result = this.result.get();
        stackConsumer.accept(ItemHandlerHelper.copyStackWithSize(result, amount * result.getCount()));
      }
    }
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.incrementalModifierSalvageSerializer.get();
  }

  /** Serializer instance */
  public static class Serializer extends AbstractModifierSalvage.AbstractSerializer<IncrementalModifierSalvage> {
    @Override
    protected IncrementalModifierSalvage read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, int upgradeSlots, int abilitySlots) {
      ItemOutput result = ItemOutput.fromJson(JsonHelper.getElement(json, "salvage"));
      return new IncrementalModifierSalvage(id, toolIngredient, modifier, minLevel, maxLevel, result, upgradeSlots, abilitySlots);
    }

    @Override
    protected IncrementalModifierSalvage read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, int upgradeSlots, int abilitySlots) {
      ItemOutput result = ItemOutput.read(buffer);
      return new IncrementalModifierSalvage(id, toolIngredient, modifier, minLevel, maxLevel, result, upgradeSlots, abilitySlots);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, IncrementalModifierSalvage recipe) {
      super.writeSafe(buffer, recipe);
      recipe.result.write(buffer);
    }
  }
}
