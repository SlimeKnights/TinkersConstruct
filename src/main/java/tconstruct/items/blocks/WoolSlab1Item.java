package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class WoolSlab1Item extends MultiItemBlock
{
    Block block;

    public WoolSlab1Item(Block b)
    {
        super(b, "", "slab", ItemDye.field_150923_a);
        this.setSpecialIndex(7, 15);
        this.block = b;
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        Block b = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        int trueMeta = meta % 8;
        boolean flag = (b) != null;

        if ((side == 1 && flag || side == 0 && !flag) && b == this.block && trueMeta == stack.getItemDamage())
        {
            if (world.setBlock(x, y, z, Blocks.wool, trueMeta, 3))
            {
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this.block.stepSound.soundName,
                        (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
                return true;
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }
}
