package slimeknights.tconstruct.tools.recipe.severing;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Map;

public class SheepShearingRecipe extends SeveringRecipe {
  private static final Map<DyeColor,ItemLike> WOOL_BY_COLOR = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
    map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
    map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
    map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
    map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
    map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
    map.put(DyeColor.LIME, Blocks.LIME_WOOL);
    map.put(DyeColor.PINK, Blocks.PINK_WOOL);
    map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
    map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
    map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
    map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
    map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
    map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
    map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
    map.put(DyeColor.RED, Blocks.RED_WOOL);
    map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
  });

  public SheepShearingRecipe(ResourceLocation id) {
    super(id, EntityIngredient.of(EntityType.SHEEP), ItemOutput.fromItem(Blocks.WHITE_WOOL, 2));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.sheepShearing.get();
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof Sheep sheep) {
      if (!sheep.isSheared() && !sheep.isBaby()) {
        return new ItemStack(WOOL_BY_COLOR.get(sheep.getColor()), 2);
      }
    }
    return ItemStack.EMPTY;
  }
}
