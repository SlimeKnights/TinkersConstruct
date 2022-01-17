package slimeknights.tconstruct.library.recipe.modifiers.salvage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
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
  public IncrementalModifierSalvage(ResourceLocation id, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, ItemOutput result, boolean fullSalvage, @Nullable SlotCount slots) {
    super(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, slots);
    this.result = result;
    this.fullSalvage = fullSalvage;
    ModifierRecipeLookup.addSalvage(this);
  }

  @Override
  public void updateTool(IToolStackView tool) {
    super.updateTool(tool);
    // remove the incremental modifier tag so we don't partially remove the next level
    tool.getPersistentData().remove(getModifier().getId());
  }

  @Override
  public void acceptItems(IToolStackView tool, Consumer<ItemStack> stackConsumer, Random random) {
    ResourceLocation key = getModifier().getId();
    int maxValue;
    // if the tag is missing, return the needed per level (assume its being treated as non-incremental)
    if (tool.getPersistentData().contains(key, Tag.TAG_ANY_NUMERIC)) {
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
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.incrementalModifierSalvageSerializer.get();
  }

  /** Serializer instance */
  public static class Serializer extends AbstractModifierSalvage.AbstractSerializer<IncrementalModifierSalvage> {
    @Override
    protected IncrementalModifierSalvage fromJson(ResourceLocation id, JsonObject json, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      JsonElement salvageElement = JsonHelper.getElement(json, "salvage");
      ItemOutput result = ItemOutput.fromJson(salvageElement);
      boolean fullSalvage = false;
      if (salvageElement.isJsonObject()) {
        fullSalvage = GsonHelper.getAsBoolean(salvageElement.getAsJsonObject(), "full", false);
      }
      return new IncrementalModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, result, fullSalvage, slots);
    }

    @Override
    protected IncrementalModifierSalvage fromNetwork(ResourceLocation id, FriendlyByteBuf buffer, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
      ItemOutput result = ItemOutput.read(buffer);
      boolean fullSalvage = buffer.readBoolean();
      return new IncrementalModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, result, fullSalvage, slots);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, IncrementalModifierSalvage recipe) {
      super.toNetworkSafe(buffer, recipe);
      recipe.result.write(buffer);
      buffer.writeBoolean(recipe.fullSalvage);
    }
  }
}
