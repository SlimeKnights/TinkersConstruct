package tconstruct.blocks.logic;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class TerraformerLogic extends TileEntity
{
    Random random = new Random();
    boolean init;
    int type;

    @Override
    public void updateEntity ()
    {
        if (!init)
        {
            init = true;
            type = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        }
        if (worldObj.isRemote)
        {
            switch (type)
            {
            case 0: //Freezer
                for (int i = 0; i < 32; i++)
                    worldObj.spawnParticle("snowshovel", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                break;
            case 1: //Fumer
                for (int i = 0; i < 5; i++)
                    worldObj.spawnParticle("lava", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                for (int i = 0; i < 3; i++)
                    worldObj.spawnParticle("explode", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                for (int i = 0; i < 5; i++)
                    worldObj.spawnParticle("smoke", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                break;
            case 2: //Waver
                for (int i = 0; i < 50; i++)
                    worldObj.spawnParticle("splash", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                break;
            case 3: //Leecher
                for (int i = 0; i < 16; i++)
                    worldObj.spawnParticle("enchantmenttable", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, -1, 0);
                break;
            case 4: //Grower
                break;
            case 5: //Nether
                for (int i = 0; i < 40; i++)
                    worldObj.spawnParticle("depthsuspend", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                for (int i = 0; i < 3; i++)
                    worldObj.spawnParticle("largesmoke", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                break;
            case 6: //Lighter
                break;
            case 7: //Crystal
                for (int i = 0; i < 8; i++)
                    worldObj.spawnParticle("fireworksSpark", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                break;
            case 8: //Ender
                for (int i = 0; i < 16; i++)
                    worldObj.spawnParticle("portal", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                for (int i = 0; i < 6; i++)
                    worldObj.spawnParticle("witchMagic", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                break;
            case 9: //Void
                for (int i = 0; i < 16; i++)
                    worldObj.spawnParticle("portal", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                for (int i = 0; i < 8; i++)
                    worldObj.spawnParticle("dripWater", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                for (int i = 0; i < 8; i++)
                    worldObj.spawnParticle("dripLava", xCoord + random.nextFloat() * 25 - 12, yCoord + random.nextFloat() * 10, zCoord + random.nextFloat() * 25 - 12, 0, 0, 0);
                break;
            case 10: //Desert
                break;
            }

            /* Good combos:
             * smoke + flame, 20+5
             * portal + witchMagic, 16+6
             * mobSpellAmbient + spell + mobSpell, 20+3+3
             * lava + explode + smoke, 10+6+10
             * happyVillager + deathsuspend + enchantmenttable, 16+64+20
             * 
             * Good values: 
             * fireworksSpark, 16
             * depthsuspend, 40
             * enchantmenttable, 16, x5
             * explode, 110
             * splash, 50
             * snowshovel, 32
             * heart, 16
             */
        }
        else
        {
            if (worldObj.getTotalWorldTime() % 5 == 0)
            {
                switch (type)
                {
                case 0:
                    snow();
                    break;
                }
            }
        }
    }

    void snow ()
    {
        int x = xCoord + random.nextInt(25) - 12, y = yCoord + random.nextInt(6) - 1, z = zCoord + random.nextInt(25) - 12;
        Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
        if (block == Block.waterStill || block == Block.waterMoving)
        {
            worldObj.setBlock(x, y, z, Block.ice.blockID);
        }
        else if (block == Block.snow)
        {
            int meta = worldObj.getBlockMetadata(x, y, z);
            if (meta < 7)
                worldObj.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
        }
        else if (block == null || block.isBlockReplaceable(worldObj, x, y, z))
        {
            if (worldObj.doesBlockHaveSolidTopSurface(x, y - 1, z))
            {
                worldObj.setBlock(x, y, z, Block.snow.blockID);
            }
        }
    }
}
