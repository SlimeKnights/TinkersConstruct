package tconstruct.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Scythe extends Weapon
{
    public Scythe()
    {
        super(4);
        this.setUnlocalizedName("InfiTool.Scythe");
    }

    /*
     * @Override protected String getHarvestType() { return "sword"; }
     */

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    static Material[] materials = new Material[] { Material.web, Material.cactus, Material.plants, Material.leaves, Material.vine };// TODO find this//, Material.pumpkin,
                                                                                                                                    // Material.plants, Material.vine,
                                                                                                                                    // Material.leaves };

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.scytheBlade;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.toughBinding;
    }

    @Override
    public Item getExtraItem ()
    {
        return TinkerTools.toughRod;
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
            return "_scythe_head";
        case 1:
            return "_scythe_head_broken";
        case 2:
            return "_scythe_handle";
        case 3:
            return "_scythe_binding";
        case 4:
            return "_scythe_accessory";
        default:
            return "";
        }
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 3.0f;
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_scythe_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "scythe";
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 1;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }

    @Override
    public float getDamageModifier ()
    {
        return 0.75f;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "melee", "harvest" };
    }

    /* Scythe Specific */

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        if (player instanceof EntityPlayerMP)
        {
            EntityPlayerMP mplayer = (EntityPlayerMP) player;
            World world = player.worldObj;
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (!tags.hasKey("AOEBreaking") || !tags.getBoolean("AOEBreaking"))
            {
                tags.setBoolean("AOEBreaking", true);
                for (int xPos = x - 1; xPos <= x + 1; xPos++)
                {
                    for (int yPos = y - 1; yPos <= y + 1; yPos++)
                    {
                        for (int zPos = z - 1; zPos <= z + 1; zPos++)
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
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D).expand(1.0D, 1.0D, 1.0D);
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
        for (Object o : list)
        {
            AbilityHelper.onLeftClickEntity(stack, player, (Entity) o, this);
        }
        return true;
    }

}
