package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Arrays;
import java.util.List;

/** Casting recipe applying a potion to a tool */
public class TippingCastingRecipe extends PotionCastingRecipe {
  protected static final LoadableField<Ingredient, PotionCastingRecipe> TOOL_FIELD = IngredientLoadable.DISALLOW_EMPTY.requiredField("tools", r -> r.bottle);
  public static final RecordLoadable<TippingCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(), ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP,
    TOOL_FIELD, FLUID_FIELD, COOLING_TIME_FIELD,
    ModifierId.PARSER.requiredField("modifier", r -> r.modifier),
    TippingCastingRecipe::new);

  private final ModifierId modifier;
  public TippingCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient tool, FluidIngredient fluid, int coolingTime, ModifierId modifier) {
    super(serializer, id, group, tool, fluid, Items.AIR, coolingTime);
    this.modifier = modifier;
  }

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    // must have the modifier to cast
    ItemStack stack = inv.getStack();
    if (super.matches(inv, level) && ModifierUtil.getModifierLevel(stack, modifier) > 0) {
      // must also have a specific potion, it's what we are going to copy
      // but it can't match what is already on the stack
      CompoundTag fluidTag = inv.getFluidTag();
      return fluidTag != null && fluidTag.contains(PotionUtils.TAG_POTION, Tag.TAG_STRING)
        && !ModifierUtil.getPersistentString(stack, modifier).equals(fluidTag.getString(PotionUtils.TAG_POTION));
    }
    return false;
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    ItemStack result = inv.getStack().copy();
    CompoundTag tag = inv.getFluidTag();
    if (tag != null) {
      ToolStack.from(result).getPersistentData().putString(modifier, tag.getString(PotionUtils.TAG_POTION));
    }
    return result;
  }


  /* JEI */

  @Override
  public List<DisplayCastingRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      // create a list of tools with the modifier
      List<ItemStack> tools = Arrays.stream(bottle.getItems())
        .map(stack -> IDisplayModifierRecipe.withModifiers(IModifiableDisplay.getDisplayStack(stack), List.of(new ModifierEntry(modifier, 1))))
        .toList();
      displayRecipes = ForgeRegistries.POTIONS.getValues().stream()
        .filter(potion -> potion != Potions.EMPTY)
        .map(potion -> {
          // add the potion to the tool list
          String id = Loadables.POTION.getString(potion);
          List<ItemStack> results = tools.stream().map(stack -> {
            ToolStack tool = ToolStack.copyFrom(stack);
            tool.getPersistentData().putString(modifier, id);
            return tool.copyStack(stack);
          }).toList();
          // add the potion to the fluid
          CompoundTag fluidNBT = new CompoundTag();
          fluidNBT.putString(PotionUtils.TAG_POTION, id);
          // create the recipe
          return new DisplayCastingRecipe(getId(), getType(), tools, fluid.getFluids().stream()
            .map(fluid -> new FluidStack(fluid.getFluid(), fluid.getAmount(), fluidNBT))
            .toList(),
            results, coolingTime, true);
        }).toList();
    }
    return displayRecipes;
  }
}
