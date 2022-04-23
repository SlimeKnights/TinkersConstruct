package slimeknights.tconstruct.library.recipe.modifiers;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

/**
 * Shared logic for main types of salvage recipes
 */
public class ModifierSalvage implements ICustomOutputRecipe<Container> {
  @Getter
  protected final ResourceLocation id;
  /** Ingredient determining tools matched by this */
  protected final Ingredient toolIngredient;
  /** Max size of the tool for this modifier. If the tool size is smaller, the salvage bonus will be reduced */
  @Getter
  protected final int maxToolSize;
  /** Modifier represented by this recipe */
  @Getter
  protected final ModifierId modifier;
  /** Minimum level of the modifier for this to be applicable */
  protected final int minLevel;
  /** Maximum level of the modifier for this to be applicable */
  protected final int maxLevel;
  /** Slots restored by this recipe, if null no slots are restored */
  @Nullable
  protected final SlotCount slots;

  public ModifierSalvage(ResourceLocation id, Ingredient toolIngredient, int maxToolSize, ModifierId modifier, int minLevel, int maxLevel, @Nullable SlotCount slots) {
    this.id = id;
    this.toolIngredient = toolIngredient;
    this.maxToolSize = maxToolSize;
    this.modifier = modifier;
    this.minLevel = minLevel;
    this.maxLevel = maxLevel;
    this.slots = slots;
    ModifierRecipeLookup.addSalvage(this);
  }

  /**
   * Checks if the given tool stack and level are applicable for this salvage
   * @param stack         Tool item stack
   * @param tool          Tool stack instance, for potential extensions
   * @param originalLevel Level to check
   * @return True if this salvage is applicable
   */
  @SuppressWarnings("unused")
  public boolean matches(ItemStack stack, IToolStackView tool, int originalLevel) {
    return originalLevel >= minLevel && originalLevel <= maxLevel && toolIngredient.test(stack);
  }

  /**
   * Updates the tool data in light of removing this modifier
   * @param tool  Tool instance
   */
  public void updateTool(IToolStackView tool) {
    if (slots != null) {
      tool.getPersistentData().addSlots(slots.getType(), slots.getCount());
    }
  }

  @Override
  public RecipeType<?> getType() {
    return TinkerRecipeTypes.DATA.get();
  }

  /** @deprecated Use {@link #matches(ItemStack, IToolStackView, int)} */
  @Deprecated
  @Override
  public boolean matches(Container inv, Level level) {
    return false;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSalvageSerializer.get();
  }

  /**
   * Serializer instance
   */
  public static class Serializer extends LoggingRecipeSerializer<ModifierSalvage> {
    @Override
    public ModifierSalvage fromJson(ResourceLocation id, JsonObject json) {
      Ingredient toolIngredient = Ingredient.fromJson(JsonHelper.getElement(json, "tools"));
      int maxToolSize = GsonHelper.getAsInt(json, "max_tool_size", ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE);
      ModifierId modifier = ModifierId.getFromJson(json, "modifier");
      int minLevel = JsonUtils.getIntMin(json, "min_level", 1);
      int maxLevel = GsonHelper.getAsInt(json, "max_level", Integer.MAX_VALUE);
      if (maxLevel < minLevel) {
        throw new JsonSyntaxException("Max level must be greater than or equal to min level");
      }
      SlotCount slots = null;
      if (json.has("slots")) {
        slots = SlotCount.fromJson(GsonHelper.getAsJsonObject(json, "slots"));
      }
      return new ModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, slots);
    }

    @Nullable
    @Override
    protected ModifierSalvage fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient toolIngredient = Ingredient.fromNetwork(buffer);
      int maxToolSize = buffer.readVarInt();
      ModifierId modifier = ModifierId.fromNetwork(buffer);
      int minLevel = buffer.readVarInt();
      int maxLevel = buffer.readVarInt();
      SlotCount slots = SlotCount.read(buffer);
      return new ModifierSalvage(id, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, slots);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, ModifierSalvage recipe) {
      recipe.toolIngredient.toNetwork(buffer);
      buffer.writeVarInt(recipe.getMaxToolSize());
      recipe.modifier.toNetwork(buffer);
      buffer.writeVarInt(recipe.minLevel);
      buffer.writeVarInt(recipe.maxLevel);
      SlotCount.write(recipe.slots, buffer);
    }
  }
}
