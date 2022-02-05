package slimeknights.tconstruct.library.recipe.modifiers.salvage;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Shared logic for main types of salvage recipes
 */
@RequiredArgsConstructor
public abstract class AbstractModifierSalvage implements ICustomOutputRecipe<Container> {
  @Getter
  protected final ResourceLocation id;
  /** Ingredient determining tools matched by this */
  protected final Ingredient toolIngredient;
  /** Max size of the tool for this modifier. If the tool size is smaller, the salvage bonus will be reduced */
  @Getter
  protected final int maxToolSize;
  /** Modifier represented by this recipe */
  @Getter
  protected final Modifier modifier;
  /** Minimum level of the modifier for this to be applicable */
  protected final int minLevel;
  /** Maximum level of the modifier for this to be applicable */
  protected final int maxLevel;
  /** Slots restored by this recipe, if null no slots are restored */
  @Nullable
  protected final SlotCount slots;

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

  /**
   * Adds items from this salvage to the given consumer
   * @param tool          Tool instance before the modifier was removed. If you need to change the tool, use {@link #updateTool(IToolStackView)}
   * @param stackConsumer Consumer for items
   */
  public abstract void acceptItems(IToolStackView tool, Consumer<ItemStack> stackConsumer, Random random);

  @Override
  public RecipeType<?> getType() {
    return RecipeTypes.DATA;
  }

  /** @deprecated Use {@link #matches(ItemStack, IToolStackView, int)} */
  @Deprecated
  @Override
  public boolean matches(Container inv, Level level) {
    return false;
  }

  /**
   * Serializer instance
   */
  public static abstract class AbstractSerializer<T extends AbstractModifierSalvage> extends LoggingRecipeSerializer<T> {
    /** Finishes reading the recipe from JSON */
    protected abstract T fromJson(ResourceLocation id, JsonObject json, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots);

    /** Finishes reading the recipe from the packet buffer */
    protected abstract T fromNetwork(ResourceLocation id, FriendlyByteBuf buffer, Ingredient toolIngredient, int maxToolSize, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots);

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
      Ingredient toolIngredient = Ingredient.fromJson(JsonHelper.getElement(json, "tools"));
      int maxToolSize = GsonHelper.getAsInt(json, "max_tool_size", ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE);
      Modifier modifier = ModifierEntry.deserializeModifier(json, "modifier");
      int minLevel = JsonUtils.getIntMin(json, "min_level", 1);
      int maxLevel = GsonHelper.getAsInt(json, "max_level", Integer.MAX_VALUE);
      if (maxLevel < minLevel) {
        throw new JsonSyntaxException("Max level must be greater than or equal to min level");
      }
      SlotCount slots = null;
      if (json.has("slots")) {
        slots = SlotCount.fromJson(GsonHelper.getAsJsonObject(json, "slots"));
      }
      return fromJson(id, json, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, slots);
    }

    @Nullable
    @Override
    protected T fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient toolIngredient = Ingredient.fromNetwork(buffer);
      int maxToolSize = buffer.readVarInt();
      Modifier modifier = buffer.readRegistryIdUnsafe(TinkerRegistries.MODIFIERS);
      int minLevel = buffer.readVarInt();
      int maxLevel = buffer.readVarInt();
      SlotCount slots = SlotCount.read(buffer);
      return fromNetwork(id, buffer, toolIngredient, maxToolSize, modifier, minLevel, maxLevel, slots);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, T recipe) {
      recipe.toolIngredient.toNetwork(buffer);
      buffer.writeVarInt(recipe.getMaxToolSize());
      buffer.writeRegistryIdUnsafe(TinkerRegistries.MODIFIERS, recipe.modifier);
      buffer.writeVarInt(recipe.minLevel);
      buffer.writeVarInt(recipe.maxLevel);
      SlotCount.write(recipe.slots, buffer);
    }
  }
}
