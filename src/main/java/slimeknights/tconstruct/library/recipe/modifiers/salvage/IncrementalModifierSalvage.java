package slimeknights.tconstruct.library.recipe.modifiers.salvage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Recipe that gives variable salvage based on the amount of a modifier on the tool
 */
public class IncrementalModifierSalvage extends AbstractModifierSalvage {
  private final ItemOutput result;
  private final boolean fullSalvage;
  public IncrementalModifierSalvage(ResourceLocation id, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, ItemOutput result, boolean fullSalvage, @Nullable SlotCount slots) {
    this(id, toolIngredient, ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE, modifier, minLevel, maxLevel, result, fullSalvage, slots);
  }

  public IncrementalModifierSalvage(ResourceLocation id, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, ItemOutput result, boolean fullSalvage, @Nullable SlotCount slots) {
    super(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, slots);
    this.result = result;
    this.fullSalvage = fullSalvage;
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
      int amount = fullSalvage ? maxValue : random.nextInt(maxValue);
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
    protected IncrementalModifierSalvage read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      JsonElement salvageElement = JsonHelper.getElement(json, "salvage");
      ItemOutput result = ItemOutput.fromJson(salvageElement);
      boolean fullSalvage = false;
      if (salvageElement.isJsonObject()) {
        fullSalvage = JSONUtils.getBoolean(salvageElement.getAsJsonObject(), "full", false);
      }
      return new IncrementalModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, result, fullSalvage, slots);
    }

    @Override
    protected IncrementalModifierSalvage read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      ItemOutput result = ItemOutput.read(buffer);
      boolean fullSalvage = buffer.readBoolean();
      return new IncrementalModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, result, fullSalvage, slots);
    }

    @Override
    protected IncrementalModifierSalvage read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected IncrementalModifierSalvage read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, IncrementalModifierSalvage recipe) {
      super.writeSafe(buffer, recipe);
      recipe.result.write(buffer);
      buffer.writeBoolean(recipe.fullSalvage);
    }
  }
}
