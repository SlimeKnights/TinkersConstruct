package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MultiBrickFancy extends TConstructBlock
{
    static String blockTextures[] = { "fancybrick_obsidian", "fancybrick_sandstone", "fancybrick_netherrack", "fancybrick_stone_refined", "fancybrick_iron", "fancybrick_gold", "fancybrick_lapis",
            "fancybrick_diamond", "fancybrick_redstone", "fancybrick_bone", "fancybrick_slime", "fancybrick_blueslime", "fancybrick_endstone", "fancybrick_obsidian_ingot", "fancybrick_stone",
            "road_stone" };

    public MultiBrickFancy(int id)
    {
        super(id, Material.rock, 3f, blockTextures);
    }

    @Override
    public float getBlockHardness (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
        case 0:
            return Block.obsidian.getBlockHardness(world, x, y, z);
        case 1:
            return Block.sandStone.getBlockHardness(world, x, y, z);
        case 2:
            return Block.netherrack.getBlockHardness(world, x, y, z);
        case 4:
            return Block.blockIron.getBlockHardness(world, x, y, z);
        case 5:
            return Block.blockGold.getBlockHardness(world, x, y, z);
        case 6:
            return Block.blockLapis.getBlockHardness(world, x, y, z);
        case 7:
            return Block.blockDiamond.getBlockHardness(world, x, y, z);
        case 8:
            return Block.blockRedstone.getBlockHardness(world, x, y, z);
        case 9:
            return 1.0F;
        case 10:
            return 1.5F;
        case 11:
            return 1.5F;
        case 12:
            return Block.whiteStone.getBlockHardness(world, x, y, z);
        case 13:
            return Block.obsidian.getBlockHardness(world, x, y, z);
        case 3:
        case 14:
        case 15:
            return Block.stone.getBlockHardness(world, x, y, z);
        default:
            return blockHardness;
        }
    }

    public float getExplosionResistance (Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
        case 0:
            return Block.obsidian.getExplosionResistance(entity);
        case 1:
            return Block.sandStone.getExplosionResistance(entity);
        case 2:
            return Block.netherrack.getExplosionResistance(entity);
        case 4:
            return Block.blockIron.getExplosionResistance(entity);
        case 5:
            return Block.blockGold.getExplosionResistance(entity);
        case 6:
            return Block.blockLapis.getExplosionResistance(entity);
        case 7:
            return Block.blockDiamond.getExplosionResistance(entity);
        case 8:
            return Block.blockRedstone.getExplosionResistance(entity);
        case 9:
            return 1.0F;
        case 10:
            return 1.5F;
        case 11:
            return 1.5F;
        case 12:
            return Block.whiteStone.getExplosionResistance(entity);
        case 13:
            return Block.obsidian.getExplosionResistance(entity);
        case 3:
        case 14:
        case 15:
            return Block.stone.getExplosionResistance(entity);
        default:
            return getExplosionResistance(entity);
        }
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 10 || meta == 11)
        {
            if (entity.motionY < 0)
                entity.motionY *= -1.2F;
            entity.fallDistance = 0;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 10 || meta == 11)
            return AxisAlignedBB.getBoundingBox(x, y, z, (double) x + 1.0D, (double) y + 0.625D, (double) z + 1.0D);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /*@Override
    public int getRenderType ()
    {
        return BrickRender.model;
    }*/

    @Override
    public boolean isBlockNormalCube (World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z) != 8;
    }

    @Override
    public boolean canProvidePower ()
    {
        return false;
    }

    @Override
    public int isProvidingWeakPower (IBlockAccess world, int x, int y, int z, int side)
    {
        if (world.getBlockMetadata(x, y, z) == 8)
            return 4;
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:bricks/" + textureNames[i]);
        }
    }
}
