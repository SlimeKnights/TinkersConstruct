package mods.tinker.tconstruct.items.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WoolSlab1Item extends ItemBlock
{
    int blockID;
    Block block;

    public WoolSlab1Item(int id)
    {
        super(id);
        this.blockID = id + 256;
        this.block = Block.blocksList[id + 256];
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 7);
        return super.getUnlocalizedName() + "." + ItemDye.dyeColorNames[15 - i] + ".slab";
    }
    
    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        int id = world.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        int trueMeta = meta % 8;
        boolean flag = (id & 8) != 0;

        if ((side == 1 && flag || side == 0 && !flag) && id == this.blockID && trueMeta == stack.getItemDamage())
        {
            if (world.setBlock(x, y, z, Block.cloth.blockID, trueMeta, 3))
            {
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this.block.stepSound.getPlaceSound(),
                        (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
                return true;
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }
}
