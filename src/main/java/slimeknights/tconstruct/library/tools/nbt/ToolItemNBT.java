package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Represents the item which the NBT is on.
 * This info is used so we don't have to save redundant information that's present on the item when
 * working on the NBT (during load/reconstruction, e.g.)
 */
public class ToolItemNBT {

  /**
   * "Missing" item, item. Basically a dummy that represents nothing, but prevents crashes.
   * Should never actually be accessed under normal circumstances
   */
  final static ToolItemNBT EMPTY = new ToolItemNBT(new ToolCore(
    new Item.Properties(),
    new ToolDefinition(new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(), ImmutableList.of(), ImmutableSet.of())
  ) {
    @Override
    public boolean isEffective(BlockState state) {
      return false;
    }

    @Override
    public void getTooltip(ItemStack stack, List<String> tooltips) {
      tooltips.add("Something went wrong, this shouldn't exist. Probably broken data or a bug.");
    }
  });

  private final ToolCore toolItem;

  public ToolItemNBT(ToolCore toolItem) {
    this.toolItem = toolItem;
  }

  public ToolCore getToolItem() {
    return toolItem;
  }

  public static ToolItemNBT readFromNBT(@Nullable INBT nbt) {
    if (nbt == null || nbt.getId() != Constants.NBT.TAG_STRING) {
      return EMPTY;
    }

    ResourceLocation itemRegistryName = new ResourceLocation(nbt.getString());
    Item item = ForgeRegistries.ITEMS.getValue(itemRegistryName);
    if (item instanceof ToolCore) {
      return new ToolItemNBT((ToolCore) item);
    }

    return EMPTY;
  }

  public StringNBT serializeToNBT() {
    // we should not need ofNullable, but it's safer in this case since it might've been deseralized somehow
    //noinspection ConstantConditions
    return Optional.ofNullable(toolItem)
      .map(Item::getRegistryName)
      .map(ResourceLocation::toString)
      .map(StringNBT::valueOf)
      .orElse(StringNBT.valueOf(""));
  }

}
