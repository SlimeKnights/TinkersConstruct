package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WoolSlab2Item extends MultiItemBlock
{
    int blockID;
    Block block;

    public WoolSlab2Item(Block b)
    {
        super(b, "", "slab" ,ItemDye.dyeColorNames);
        this.setSpecialIndex(7,7);
        this.blockID = id + 256;
        this.block = Block.blocksList[id + 256];
        setMaxDamage(0);
        setHasSubtypes(true);
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
            if (world.setBlock(x, y, z, Blocks.wool.blockID, trueMeta + 8, 3))
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
