package slimeknights.tconstruct.smeltery.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.tinkering.AbstractFluidContainerTransferProvider;
import slimeknights.tconstruct.library.fluid.transfer.EmptyFluidContainerTransfer;
import slimeknights.tconstruct.library.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.tconstruct.library.recipe.FluidValues;

public class FluidContainerTransferProvider extends AbstractFluidContainerTransferProvider {
  public FluidContainerTransferProvider(DataGenerator generator) {
    super(generator, TConstruct.MOD_ID);
  }

  @Override
  protected void addTransfers() {
    addFillEmpty("honey_bottle_",  Items.HONEY_BOTTLE,  Items.GLASS_BOTTLE, TinkerFluids.honey.get(),        TinkerFluids.honey.getForgeTag(),        FluidValues.BOTTLE);
    addFillEmpty("beetroot_soup_", Items.BEETROOT_SOUP, Items.BOWL,         TinkerFluids.beetrootSoup.get(), TinkerFluids.beetrootSoup.getForgeTag(), FluidValues.BOWL);
    addFillEmpty("mushroom_stew_", Items.MUSHROOM_STEW, Items.BOWL,         TinkerFluids.mushroomStew.get(), TinkerFluids.mushroomStew.getForgeTag(), FluidValues.BOWL);
    addFillEmpty("rabbit_stew_",   Items.RABBIT_STEW,   Items.BOWL,         TinkerFluids.rabbitStew.get(),   TinkerFluids.rabbitStew.getForgeTag(),   FluidValues.BOWL);
    // water bottles are 1/3 of a bucket, to prevent water dupes we round up on fill and down on empty
    ItemStack waterBottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    assert waterBottle.getTag() != null;
    addTransfer("water_bottle_empty", new EmptyFluidContainerTransfer(
      PartialNBTIngredient.of(waterBottle.getTag(), Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION),
      ItemOutput.fromItem(Items.GLASS_BOTTLE),
      new FluidStack(Fluids.WATER, FluidValues.BOTTLE)));
    addTransfer("water_bottle_fill",  new FillFluidContainerTransfer(
      Ingredient.of(Items.GLASS_BOTTLE),
      ItemOutput.fromStack(waterBottle),
      FluidIngredient.of(FluidTags.WATER, FluidValues.BOTTLE * 2)));
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Fluid Container Transfer";
  }
}
