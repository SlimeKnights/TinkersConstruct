package slimeknights.tconstruct.tools.recipe.severing;

import com.google.common.collect.Maps;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipe;

import java.util.Map;

public class SheepShearingRecipe extends SeveringRecipe {
  private static final Map<DyeColor,IItemProvider> WOOL_BY_COLOR = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
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
    super(id, EntityIngredient.of(EntityType.SHEEP), ItemOutput.fromStack(new ItemStack(Blocks.WHITE_WOOL, 2)));
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof SheepEntity) {
      SheepEntity sheep = (SheepEntity)entity;
      if (!sheep.getSheared() && !sheep.isChild()) {
        return new ItemStack(WOOL_BY_COLOR.get(sheep.getFleeceColor()), 2);
      }
    }
    return ItemStack.EMPTY;
  }
}
