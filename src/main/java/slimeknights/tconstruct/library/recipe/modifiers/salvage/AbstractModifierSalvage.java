package slimeknights.tconstruct.library.recipe.modifiers.salvage;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Shared logic for main types of salvage recipes
 */
@RequiredArgsConstructor
public abstract class AbstractModifierSalvage implements ICustomOutputRecipe<IInventory> {
  @Getter
  protected final ResourceLocation id;
  /** Ingredient determining tools matched by this */
  protected final Ingredient toolIngredient;
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
  public boolean matches(ItemStack stack, IModifierToolStack tool, int originalLevel) {
    return originalLevel >= minLevel && originalLevel <= maxLevel && toolIngredient.test(stack);
  }

  /**
   * Updates the tool data in light of removing this modifier
   * @param tool  Tool instance
   */
  public void updateTool(IModifierToolStack tool) {
    if (slots != null) {
      tool.getPersistentData().addSlots(slots.getType(), slots.getCount());
    }
  }

  /**
   * Adds items from this salvage to the given consumer
   * @param tool          Tool instance before the modifier was removed. If you need to change the tool, use {@link #updateTool(IModifierToolStack)}
   * @param stackConsumer Consumer for items
   */
  public abstract void acceptItems(IModifierToolStack tool, Consumer<ItemStack> stackConsumer, Random random);

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.DATA;
  }

  /** @deprecated Use {@link #matches(ItemStack, IModifierToolStack, int)} */
  @Deprecated
  @Override
  public boolean matches(IInventory inv, World worldIn) {
    return false;
  }

  /**
   * Serializer instance
   */
  public static abstract class AbstractSerializer<T extends AbstractModifierSalvage> extends LoggingRecipeSerializer<T> {
    /** Finishes reading the recipe from JSON */
    protected abstract T read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots);

    /** Finishes reading the recipe from the packet buffer */
    protected abstract T read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, @Nullable SlotCount slots);

    @Override
    public T read(ResourceLocation id, JsonObject json) {
      Ingredient toolIngredient = Ingredient.deserialize(JsonHelper.getElement(json, "tools"));
      Modifier modifier = ModifierEntry.deserializeModifier(json, "modifier");
      int minLevel = JsonUtils.getIntMin(json, "min_level", 1);
      int maxLevel = JSONUtils.getInt(json, "max_level", Integer.MAX_VALUE);
      if (maxLevel < minLevel) {
        throw new JsonSyntaxException("Max level must be greater than or equal to min level");
      }
      SlotCount slots = null;
      if (json.has("slots")) {
        slots = SlotCount.fromJson(JSONUtils.getJsonObject(json, "slots"));
      }
      return read(id, json, toolIngredient, modifier, minLevel, maxLevel, slots);
    }

    @Nullable
    @Override
    protected T readSafe(ResourceLocation id, PacketBuffer buffer) {
      Ingredient toolIngredient = Ingredient.read(buffer);
      Modifier modifier = buffer.readRegistryIdUnsafe(TinkerRegistries.MODIFIERS);
      int minLevel = buffer.readVarInt();
      int maxLevel = buffer.readVarInt();
      SlotCount slots = SlotCount.read(buffer);
      return read(id, buffer, toolIngredient, modifier, minLevel, maxLevel, slots);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, T recipe) {
      recipe.toolIngredient.write(buffer);
      buffer.writeRegistryIdUnsafe(TinkerRegistries.MODIFIERS, recipe.modifier);
      buffer.writeVarInt(recipe.minLevel);
      buffer.writeVarInt(recipe.maxLevel);
      SlotCount.write(recipe.slots, buffer);
    }
  }
}
