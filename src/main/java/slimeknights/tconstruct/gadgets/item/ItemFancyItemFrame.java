package slimeknights.tconstruct.gadgets.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.shared.TinkerCommons;

public class ItemFancyItemFrame extends ItemHangingEntity {

  public ItemFancyItemFrame() {
    super(EntityFancyItemFrame.class);

    this.setHasSubtypes(true);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    subItems.add(new ItemStack(itemIn, 1, EntityFancyItemFrame.FrameType.JEWEL.ordinal()));

    if(TinkerCommons.nuggetAlubrass != null) {
      subItems.add(new ItemStack(itemIn, 1, EntityFancyItemFrame.FrameType.ALUBRASS.ordinal()));
    }
    if(TinkerCommons.nuggetCobalt != null) {
      subItems.add(new ItemStack(itemIn, 1, EntityFancyItemFrame.FrameType.COBALT.ordinal()));
    }
    if(TinkerCommons.nuggetArdite != null) {
      subItems.add(new ItemStack(itemIn, 1, EntityFancyItemFrame.FrameType.ARDITE.ordinal()));
    }
    if(TinkerCommons.nuggetManyullyn != null) {
      subItems.add(new ItemStack(itemIn, 1, EntityFancyItemFrame.FrameType.MANYULLYN.ordinal()));
    }

    subItems.add(new ItemStack(itemIn, 1, EntityFancyItemFrame.FrameType.GOLD.ordinal()));
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    String type = EntityFancyItemFrame.FrameType.fromMeta(stack.getMetadata()).toString().toLowerCase();

    return super.getUnlocalizedName(stack) + "." + type;
  }

  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (side == EnumFacing.DOWN)
    {
      return false;
    }
    else if (side == EnumFacing.UP)
    {
      return false;
    }
    else
    {
      BlockPos blockpos = pos.offset(side);

      if (!playerIn.canPlayerEdit(blockpos, side, stack))
      {
        return false;
      }
      else
      {
        EntityHanging entityhanging = new EntityFancyItemFrame(worldIn, blockpos, side, stack.getMetadata());

        if (entityhanging.onValidSurface())
        {
          if (!worldIn.isRemote)
          {
            worldIn.spawnEntityInWorld(entityhanging);
          }

          --stack.stackSize;
        }

        return true;
      }
    }
  }
}
