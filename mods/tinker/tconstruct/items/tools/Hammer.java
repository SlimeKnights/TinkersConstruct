package mods.tinker.tconstruct.items.tools;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.ActiveToolMod;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.HarvestTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class Hammer extends HarvestTool
{
    public Hammer(int itemID)
    {
        super(itemID, 2);
        this.setUnlocalizedName("InfiTool.Hammer");
        setupCoords();
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

    public int durabilityTypeHandle ()
    {
        return 2;
    }

    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    public int durabilityTypeExtra ()
    {
        return 1;
    }

    @Override
    protected String getHarvestType ()
    {
        return "pickaxe";
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    static Material[] materials = new Material[] { Material.rock, Material.iron, Material.ice, Material.glass, Material.piston, Material.anvil };

    @Override
    public Item getHeadItem ()
    {
        return TContent.hammerHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TContent.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.heavyPlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TContent.heavyPlate;
    }
    
    public float getDurabilityModifier ()
    {
        return 5.5f;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_hammer_head";
        case 1:
            return "_hammer_handle_broken";
        case 2:
            return "_hammer_handle";
        case 3:
            return "_hammer_front";
        case 4:
            return "_hammer_back";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_hammer_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "hammer";
    }

    @Override
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null)
        {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount())
            {
                if (renderPass == 0) // Handle
                {
                    if (tags.getBoolean("Broken"))
                        return (brokenIcons.get(tags.getInteger("RenderHandle")));
                    return handleIcons.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    return (headIcons.get(tags.getInteger("RenderHead")));
                }

                else if (renderPass == 2) // Accessory
                {
                    return (accessoryIcons.get(tags.getInteger("RenderAccessory")));
                }

                else if (renderPass == 3) // Extra
                {
                    return (extraIcons.get(tags.getInteger("RenderExtra")));
                }
            }

            else
            {
                if (renderPass == getPartAmount())
                {
                    if (tags.hasKey("Effect1"))
                        return (effectIcons.get(tags.getInteger("Effect1")));
                }

                else if (renderPass == getPartAmount() + 1)
                {
                    if (tags.hasKey("Effect2"))
                        return (effectIcons.get(tags.getInteger("Effect2")));
                }

                else if (renderPass == getPartAmount() + 2)
                {
                    if (tags.hasKey("Effect3"))
                        return (effectIcons.get(tags.getInteger("Effect3")));
                }

                else if (renderPass == getPartAmount() + 3)
                {
                    if (tags.hasKey("Effect4"))
                        return (effectIcons.get(tags.getInteger("Effect4")));
                }

                else if (renderPass == getPartAmount() + 4)
                {
                    if (tags.hasKey("Effect5"))
                        return (effectIcons.get(tags.getInteger("Effect5")));
                }

                else if (renderPass == getPartAmount() + 5)
                {
                    if (tags.hasKey("Effect6"))
                        return (effectIcons.get(tags.getInteger("Effect6")));
                }
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    ArrayList<int[]> coords = new ArrayList<int[]>();

    void setupCoords ()
    {
        coords.add(new int[] { 0, 0, 0 });
        coords.add(new int[] { 1, 0, 0 });
        coords.add(new int[] { -1, 0, 0 });
        coords.add(new int[] { 0, 1, 0 });
        coords.add(new int[] { 0, -1, 0 });
        coords.add(new int[] { 0, 0, 1 });
        coords.add(new int[] { 0, 0, -1 });
        
        coords.add(new int[] { -1, 0, 0 });
        coords.add(new int[] { -1, 0, 1 });
        coords.add(new int[] { -1, 0, -1 });
        coords.add(new int[] { -1, 1, 0 });
        coords.add(new int[] { -1, -1, 0 });
        
        coords.add(new int[] { 1, 0, 0 });
        coords.add(new int[] { 1, 0, 1 });
        coords.add(new int[] { 1, 0, -1 });
        coords.add(new int[] { 1, 1, 0 });
        coords.add(new int[] { 1, -1, 0 });
        
        coords.add(new int[] { 0, 1, 1 });
        coords.add(new int[] { 0, 1, 0 });
        coords.add(new int[] { 0, 1, -1 });
        
        coords.add(new int[] { 0, -1, 1 });
        coords.add(new int[] { 0, -1, 0 });
        coords.add(new int[] { 0, -1, -1 });
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        World world = player.worldObj;
        int blockID = 0;
        int meta = 0;
        if (!stack.hasTagCompound())
            return false;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

        if (!(tags.getBoolean("Broken")))
        {
            for (int[] coord : coords)
            {
                int xPos = x + coord[0], yPos = y + coord[1], zPos = z + coord[2];
                boolean cancelHarvest = false;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                {
                    if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player))
                        cancelHarvest = true;
                }

                if (!cancelHarvest)
                {
                    int localblockID = world.getBlockId(xPos, yPos, zPos);
                    Block block = Block.blocksList[localblockID];
                    if (block != null)
                    {
                        for (int iter = 0; iter < materials.length; iter++)
                        {
                            if (materials[iter] == block.blockMaterial)
                            {
                                meta = world.getBlockMetadata(xPos, yPos, zPos);
                                world.setBlockToAir(xPos, yPos, zPos);
                                if (!player.capabilities.isCreativeMode)
                                {
                                    block.harvestBlock(world, player, xPos, yPos, zPos, meta);
                                    onBlockDestroyed(stack, world, localblockID, xPos, yPos, zPos, player);
                                }
                                blockID = localblockID;
                            }
                        }
                    }
                }
            }
        }
        if (!world.isRemote)
            world.playAuxSFX(2001, x, y, z, blockID + (meta << 12));
        return super.onBlockStartBreak(stack, x, y, z, player);
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
    public String[] toolCategories()
    {
        return new String[] { "weapon", "harvest", "melee", "bludgeoning" };
    }
}
