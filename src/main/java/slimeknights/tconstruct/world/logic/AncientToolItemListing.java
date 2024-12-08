package slimeknights.tconstruct.world.logic;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Listing for wandering traders to randomly sell an ancient tool
 */
public enum AncientToolItemListing implements ItemListing {
  INSTANCE;

  @Nullable
  @Override
  public MerchantOffer getOffer(Entity trader, RandomSource random) {
    // step 1: select ancient tool
    Optional<Holder<Item>> selected = Registry.ITEM.getTag(TinkerTags.Items.TRADER_TOOLS).flatMap(t -> t.getRandomElement(random));
    if (selected.isPresent() && selected.get().value() instanceof IModifiable toolItem) {
      // step 2: select materials
      ToolStack tool = ToolBuildHandler.buildToolRandomMaterials(toolItem, random);
      // step 3: calculate cost based on tier
      float tier = 0;
      MaterialNBT materials = tool.getMaterials();
      if (materials.size() == 0) {
        // if no materials, just choose a baseline tier of 2
        tier = 2;
      } else {
        for (MaterialVariant material : materials) {
          tier += material.get().getTier();
        }
        tier /= materials.size();
      }
      // formula is a cost of 6-8 emeralds per tier, meaning cost ranges from 6 (min tier 1) to 32 (max tier 4)
      int cost = Math.round(tier * 6) + random.nextInt(Math.round(2 * tier) + 1);
      return new MerchantOffer(new ItemStack(Items.EMERALD, cost), tool.createStack(), 1, 15, 1);
    }
    return null;
  }
}
