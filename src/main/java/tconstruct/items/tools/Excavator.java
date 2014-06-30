package tconstruct.items.tools;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.HarvestTool;
import tconstruct.tools.TinkerTools;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Excavator extends HarvestTool
{
    public Excavator()
    {
        super(2);
        this.setUnlocalizedName("InfiTool.Excavator");
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    @Override
    protected String getHarvestType ()
    {
        return "shovel";
    }

    static Material[] materials = { Material.grass, Material.ground, Material.sand, Material.snow, Material.craftedSnow, Material.clay };

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.excavatorHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.largePlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TinkerTools.toughBinding;
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 2.75f;
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 10;
    }

    @Override
    public int getPartAmount ()
    {
        return 4;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_excavator_head";
        case 1:
            return "_excavator_head_broken";
        case 2:
            return "_excavator_handle";
        case 3:
            return "_excavator_binding";
        case 4:
            return "_excavator_grip";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_excavator_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "excavator";
    }

    /* Excavator Specific */

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        World world = player.worldObj;
        MovingObjectPosition mop = AbilityHelper.raytraceFromEntity(world, player, true, 5.0D);
        if (mop != null && player instanceof EntityPlayerMP)
        {
            EntityPlayerMP mplayer = (EntityPlayerMP) player;
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (!tags.hasKey("AOEBreaking") || !tags.getBoolean("AOEBreaking"))
            {
                tags.setBoolean("AOEBreaking", true);

                int xRange = 1;
                int yRange = 1;
                int zRange = 1;
                switch (mop.sideHit)
                {
                case 0:
                case 1:
                    yRange = 0;
                    break;
                case 2:
                case 3:
                    zRange = 0;
                    break;
                case 4:
                case 5:
                    xRange = 0;
                    break;
                }

                for (int xPos = x - xRange; xPos <= x + xRange; xPos++)
                {
                    for (int yPos = y - yRange; yPos <= y + yRange; yPos++)
                    {
                        for (int zPos = z - zRange; zPos <= z + zRange; zPos++)
                        {
                            Block block = world.getBlock(xPos, yPos, zPos);
                            for (Material mat : this.materials)
                            {
                                if (block != null && mat == block.getMaterial()
                                    && block.getPlayerRelativeBlockHardness(mplayer, world, x, yPos, z) > 0)
                                    mplayer.theItemInWorldManager.tryHarvestBlock(xPos, yPos, zPos);
                            }
                        }
                    }
                }
                tags.setBoolean("AOEBreaking", false);
            }
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    @Override
    public float getDigSpeed (ItemStack stack, Block block, int meta)
    {
        if (!stack.hasTagCompound())
            return 1.0f;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.getMaterial())
            {
                float mineSpeed = tags.getInteger("MiningSpeed");
                int heads = 1;
                if (tags.hasKey("MiningSpeed2"))
                {
                    mineSpeed += tags.getInteger("MiningSpeed2");
                    heads++;
                }

                if (tags.hasKey("MiningSpeedHandle"))
                {
                    mineSpeed += tags.getInteger("MiningSpeedHandle");
                    heads++;
                }

                if (tags.hasKey("MiningSpeedExtra"))
                {
                    mineSpeed += tags.getInteger("MiningSpeedExtra");
                    heads++;
                }
                float trueSpeed = mineSpeed / (heads * 300f);
                int hlvl = block.getHarvestLevel(meta);
                int durability = tags.getInteger("Damage");

                float stonebound = tags.getFloat("Shoddy");
                float bonusLog = (float) Math.log(durability / 216f + 1) * 2 * stonebound;
                trueSpeed += bonusLog;

                if (hlvl <= tags.getInteger("HarvestLevel"))
                    return trueSpeed;
                return 0.1f;
            }
        }
        return super.getDigSpeed(stack, block, meta);
    }

}
