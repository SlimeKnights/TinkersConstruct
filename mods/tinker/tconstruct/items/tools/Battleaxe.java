package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.ActiveToolMod;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.HarvestTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Battleaxe extends HarvestTool
{
    public Battleaxe(int itemID)
    {
        super(itemID, 4);
        this.setUnlocalizedName("InfiTool.Battleaxe");
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    @Override
    protected String getHarvestType ()
    {
        return "axe";
    }

    @Override
    public boolean onBlockDestroyed (ItemStack itemstack, World world, int bID, int x, int y, int z, EntityLiving player)
    {
        Block block = Block.blocksList[bID];
        if (block != null && block.blockMaterial == Material.leaves)
            return false;

        return AbilityHelper.onBlockChanged(itemstack, world, bID, x, y, z, player, random);
    }

    static Material[] materials = { Material.wood, Material.leaves, Material.vine, Material.circuits, Material.cactus, Material.pumpkin };

    @Override
    public Item getHeadItem ()
    {
        return TContent.broadAxeHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TContent.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.broadAxeHead;
    }

    @Override
    public Item getExtraItem ()
    {
        return TContent.toughBinding;
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    public float getDurabilityModifier ()
    {
        return 2.5f;
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
            return "_battleaxe_fronthead";
        case 1:
            return "_battleaxe_fronthead_broken";
        case 2:
            return "_battleaxe_handle";
        case 3:
            return "_battleaxe_backhead";
        case 4:
            return "_battleaxe_binding";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_battleaxe_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "battleaxe";
    }
    
    @Override
    public String[] toolCategories()
    {
        return new String[] { "weapon", "harvest", "slicing" };
    }

    /* Battleaxe Specific */

    /* Lumber axe specific */
    @Override
    public boolean hitEntity (ItemStack stack, EntityLiving mob, EntityLiving player)
    {
        AbilityHelper.knockbackEntity(mob, 1.5f);
        return true;
    }

    @Override
    public float getStrVsBlock (ItemStack stack, Block block, int meta)
    {
        if (!stack.hasTagCompound())
            return 1.0f;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.blockMaterial)
            {
                float speed = tags.getInteger("MiningSpeed");
                speed /= 200f;
                int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
                int durability = tags.getInteger("Damage");

                float shoddy = tags.getFloat("Shoddy");
                speed += shoddy * durability / 100f;

                if (hlvl <= tags.getInteger("HarvestLevel"))
                    return speed;
                return 0.1f;
            }
        }
        return super.getStrVsBlock(stack, block, meta);
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        World world = player.worldObj;
        int woodID = world.getBlockId(x, y, z);
        Block wood = Block.blocksList[woodID];
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int meta = world.getBlockMetadata(x, y, z);
        for (int yPos = y + 1; yPos < y + 9; yPos++)
        {
            int blockID = world.getBlockId(x, yPos, z);
            Block block = Block.blocksList[blockID];
            if (!(tags.getBoolean("Broken")) && block != null && block.blockMaterial == Material.wood )
            {
                boolean cancelHarvest = false;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                {
                    if (mod.beforeBlockBreak(this, stack, x, yPos, z, player))
                        cancelHarvest = true;
                }

                if (!cancelHarvest)
                {
                    if (block != null && block.blockMaterial == Material.wood)
                    {
                        meta = world.getBlockMetadata(x, yPos, z);
                        world.setBlockToAir(x, yPos, z);
                        if (!player.capabilities.isCreativeMode)
                        {
                            Block.blocksList[blockID].harvestBlock(world, player, x, yPos, z, meta);
                            onBlockDestroyed(stack, world, blockID, x, yPos, z, player);
                        }
                    }
                }
            }

        }
        if (!world.isRemote)
            world.playAuxSFX(2001, x, y, z, woodID + (meta << 12));
        return super.onBlockStartBreak(stack, x, y, z, player);
    }
}
