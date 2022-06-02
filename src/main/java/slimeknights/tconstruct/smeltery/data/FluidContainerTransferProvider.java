package slimeknights.tconstruct.smeltery.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.fluids.item.EmptyPotionTransfer;
import slimeknights.tconstruct.library.data.tinkering.AbstractFluidContainerTransferProvider;
import slimeknights.tconstruct.library.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.tconstruct.library.fluid.transfer.FillFluidWithNBTTransfer;
import slimeknights.tconstruct.library.recipe.FluidValues;

import javax.annotation.Nullable;

public class FluidContainerTransferProvider extends AbstractFluidContainerTransferProvider {
  public FluidContainerTransferProvider(DataGenerator generator) {
    super(generator, TConstruct.MOD_ID);
  }

  @Override
  protected void addTransfers() {
    addFillEmpty("honey_bottle_",  Items.HONEY_BOTTLE,     Items.GLASS_BOTTLE,           TinkerFluids.honey.get(),        TinkerFluids.honey.getForgeTag(),        FluidValues.BOTTLE);
    addFillEmpty("beetroot_soup_", Items.BEETROOT_SOUP,    Items.BOWL,                   TinkerFluids.beetrootSoup.get(), TinkerFluids.beetrootSoup.getForgeTag(), FluidValues.BOWL);
    addFillEmpty("mushroom_stew_", Items.MUSHROOM_STEW,    Items.BOWL,                   TinkerFluids.mushroomStew.get(), TinkerFluids.mushroomStew.getForgeTag(), FluidValues.BOWL);
    addFillEmpty("rabbit_stew_",   Items.RABBIT_STEW,      Items.BOWL,                   TinkerFluids.rabbitStew.get(),   TinkerFluids.rabbitStew.getForgeTag(),   FluidValues.BOWL);
    // potions
    addPotion("potion_",           Items.POTION,           Items.GLASS_BOTTLE,           null);
    addPotion("potion_splash_",    Items.SPLASH_POTION,    TinkerFluids.splashBottle,    TinkerTags.Items.SPLASH_BOTTLE);
    addPotion("potion_lingering_", Items.LINGERING_POTION, TinkerFluids.lingeringBottle, TinkerTags.Items.LINGERING_BOTTLE);
  }

  /** Adds generic fill and empty for a container */
  protected void addPotion(String prefix, ItemLike filled, ItemLike containerItem, @Nullable TagKey<Item> containerTag) {
    // water bottles are 1/3 of a bucket, to prevent water dupes we round up on fill and down on empty
    addTransfer(prefix + "empty",  new EmptyPotionTransfer(Ingredient.of(filled), ItemOutput.fromItem(containerItem), new FluidStack(TinkerFluids.potion.get(), FluidValues.BOTTLE)));
    Ingredient container = containerTag == null ? Ingredient.of(containerItem) : Ingredient.of(containerTag);
    addTransfer(prefix + "fill", new FillFluidWithNBTTransfer(container, ItemOutput.fromItem(filled), FluidIngredient.of(TinkerTags.Fluids.POTION, FluidValues.BOTTLE)));
    addTransfer(prefix + "water", new FillFluidContainerTransfer(
      container,
      ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(filled), Potions.WATER)),
      FluidIngredient.of(FluidTags.WATER, FluidValues.BOTTLE * 2)));
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Fluid Container Transfer";
  }
}
