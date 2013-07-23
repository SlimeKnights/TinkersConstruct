package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.ActiveToolMod;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.HarvestTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
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

    static Material[] materials = { Material.wood, Material.vine, Material.circuits, Material.cactus, Material.pumpkin };

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
    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
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
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "harvest", "melee", "slicing" };
    }

    /* Battleaxe Specific */

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return false;
    }

    @Override
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        //player.rotationYaw += 1;
        //if (player.onGround)
        //{
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        //}
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int useCount)
    {
        //if (player.onGround)
        {
            int time = this.getMaxItemUseDuration(stack) - useCount;
            int boost = time / 100;
            if (boost > 2)
                boost = 2;
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, time * 4, boost));
            player.addPotionEffect(new PotionEffect(Potion.jump.id, time * 4, boost));
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, time * 4, 0));
            player.addPotionEffect(new PotionEffect(Potion.hunger.id, time * 2, 0));
            if (time > 5 && player.onGround)
            {
                player.addExhaustion(0.2F);
                player.setSprinting(true);

                float speed = 0.025F * time;
                if (speed > 0.925f)
                    speed = 0.925f;

                float increase = (float) (0.02 * time + 0.2);
                if (increase > 0.56f)
                    increase = 0.56f;
                player.motionY += increase;

                player.motionX = (double) (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
                player.motionZ = (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
            }
        }
    }

    public int getMaxItemUseDuration (ItemStack par1ItemStack)
    {
        return 72000;
    }

    @Override
    public boolean hitEntity (ItemStack stack, EntityLiving mob, EntityLiving player)
    {
        AbilityHelper.knockbackEntity(mob, 1.5f);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) entity;
            ItemStack usingItem = player.getItemInUse();
            if (usingItem != null && usingItem.getItem() == this)
            {
                player.movementInput.moveForward *= 5.0F;
                player.movementInput.moveStrafe *= 5.0F;
            }
        }
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        World world = player.worldObj;
        final int woodID = world.getBlockId(x, y, z);
        final Block wood = Block.blocksList[woodID];
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        final int meta = world.getBlockMetadata(x, y, z);
        for (int yPos = y + 1; yPos < y + 9; yPos++)
        {
            int blockID = world.getBlockId(x, yPos, z);
            Block block = Block.blocksList[blockID];
            if (!(tags.getBoolean("Broken")) && block != null && block.blockMaterial == Material.wood)
            {
                int localblockID = world.getBlockId(x, yPos, z);
                int localMeta = world.getBlockMetadata(x, yPos, z);
                int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());

                if (hlvl <= tags.getInteger("HarvestLevel"))
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
                            localMeta = world.getBlockMetadata(x, yPos, z);
                            if (!player.capabilities.isCreativeMode)
                            {
                                if (block.removeBlockByPlayer(world, player, x, yPos, z))
                                {
                                    block.onBlockDestroyedByPlayer(world, x, yPos, z, localMeta);
                                }
                                block.harvestBlock(world, player, x, yPos, z, localMeta);
                                block.onBlockHarvested(world, x, yPos, z, localMeta, player);
                                onBlockDestroyed(stack, world, localblockID, x, yPos, z, player);
                            }
                            else
                            {
                                world.setBlockToAir(x, yPos, z);
                            }
                        }
                    }
                }
            }
            else
                break;
        }
        if (!world.isRemote)
            world.playAuxSFX(2001, x, y, z, woodID + (meta << 12));
        return super.onBlockStartBreak(stack, x, y, z, player);
    }
}
