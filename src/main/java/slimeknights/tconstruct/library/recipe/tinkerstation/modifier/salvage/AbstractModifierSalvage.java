package slimeknights.tconstruct.library.recipe.tinkerstation.modifier.salvage;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
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
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Shared logic for main types of salvage recipes
 */
public abstract class AbstractModifierSalvage implements ICustomOutputRecipe<IInventory> {
  @Getter
  protected final ResourceLocation id;
  /**
   * Ingredient determining tools matched by this
   */
  protected final Ingredient toolIngredient;
  /**
   * Modifier represented by this recipe
   */
  @Getter
  protected final Modifier modifier;
  /**
   * Minimum level of the modifier for this to be applicable
   */
  protected final int minLevel;
  /**
   * Maximum level of the modifier for this to be applicable
   */
  protected final int maxLevel;
  /**
   * Upgrade slots returned from this recipe
   */
  protected final int upgradeSlots;
  /**
   * Ability slots returned from this recipe
   */
  protected final int abilitySlots;

  public AbstractModifierSalvage(ResourceLocation id, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, int upgradeSlots, int abilitySlots) {
    this.id = id;
    this.toolIngredient = toolIngredient;
    this.modifier = modifier;
    this.minLevel = minLevel;
    this.maxLevel = maxLevel;
    this.upgradeSlots = upgradeSlots;
    this.abilitySlots = abilitySlots;
  }

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
    ModDataNBT persistentData = tool.getPersistentData();
    persistentData.addUpgrades(upgradeSlots);
    persistentData.addAbilities(abilitySlots);
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
    /**
     * Helper for int with a min value
     */
    private static int readIntWithMin(JsonObject json, String key, int min) {
      int value = JSONUtils.getInt(json, key, min);
      if (value < min) {
        throw new JsonSyntaxException(key + " must be at least " + min);
      }
      return value;
    }

    /**
     * Finishes reading the recipe from JSON
     * @param id             Recipe ID
     * @param json           Recipe JSON
     * @param toolIngredient Tool ingredient
     * @param modifier       Modifier
     * @param minLevel       Min modifier level
     * @param maxLevel       Max modifier level
     * @param upgradeSlots   Number of upgrade slots returned
     * @param abilitySlots   Number of ability slots returned
     * @return Recipe
     */
    protected abstract T read(ResourceLocation id, JsonObject json, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, int upgradeSlots, int abilitySlots);

    /**
     * Finishes reading the recipe from JSON
     * @param id             Recipe ID
     * @param buffer         Packet buffer
     * @param toolIngredient Tool ingredient
     * @param modifier       Modifier
     * @param minLevel       Min modifier level
     * @param maxLevel       Max modifier level
     * @param upgradeSlots   Number of upgrade slots returned
     * @param abilitySlots   Number of ability slots returned
     * @return Recipe
     */
    protected abstract T read(ResourceLocation id, PacketBuffer buffer, Ingredient toolIngredient, Modifier modifier, int minLevel, int maxLevel, int upgradeSlots, int abilitySlots);

    @Override
    public T read(ResourceLocation id, JsonObject json) {
      Ingredient toolIngredient = Ingredient.deserialize(JsonHelper.getElement(json, "tools"));
      Modifier modifier = ModifierEntry.deserializeModifier(json, "modifier");
      int minLevel = readIntWithMin(json, "min_level", 1);
      int maxLevel = JSONUtils.getInt(json, "max_level", Integer.MAX_VALUE);
      if (maxLevel < minLevel) {
        throw new JsonSyntaxException("Max level must be greater than or equal to min level");
      }
      int upgradeSlots = readIntWithMin(json, "upgrade_slots", 0);
      int abilitySlots = readIntWithMin(json, "ability_slots", 0);
      return read(id, json, toolIngredient, modifier, minLevel, maxLevel, upgradeSlots, abilitySlots);
    }
    @Nullable
    @Override
    protected T readSafe(ResourceLocation id, PacketBuffer buffer) {
      Ingredient toolIngredient = Ingredient.read(buffer);
      Modifier modifier = buffer.readRegistryIdUnsafe(TinkerRegistries.MODIFIERS);
      int minLevel = buffer.readVarInt();
      int maxLevel = buffer.readVarInt();
      int upgradeSlots = buffer.readVarInt();
      int abilitySlots = buffer.readVarInt();
      return read(id, buffer, toolIngredient, modifier, minLevel, maxLevel, upgradeSlots, abilitySlots);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, T recipe) {
      recipe.toolIngredient.write(buffer);
      buffer.writeRegistryIdUnsafe(TinkerRegistries.MODIFIERS, recipe.modifier);
      buffer.writeVarInt(recipe.minLevel);
      buffer.writeVarInt(recipe.maxLevel);
      buffer.writeVarInt(recipe.upgradeSlots);
      buffer.writeVarInt(recipe.abilitySlots);
    }
  }
}
