package tconstruct.items.tools;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tconstruct.common.TRepo;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.HarvestTool;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Battleaxe extends HarvestTool
{
    public Battleaxe()
    {
        super(4);
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
    public boolean onBlockDestroyed (ItemStack itemstack, World world, Block b, int x, int y, int z, EntityLivingBase player)
    {
        if (b != null && b.getMaterial() == Material.leaves)
            return false;

        return AbilityHelper.onBlockChanged(itemstack, world, b, x, y, z, player, random);
    }

    static Material[] materials = { Material.wood, Material.vine, Material.circuits, Material.cactus, Material.gourd };

    @Override
    public Item getHeadItem ()
    {
        return TRepo.broadAxeHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TRepo.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TRepo.broadAxeHead;
    }

    @Override
    public Item getExtraItem ()
    {
        return TRepo.toughBinding;
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

    @Override
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
        // player.rotationYaw += 1;
        // if (player.onGround)
        // {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        // }
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int useCount)
    {
        // if (player.onGround)
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

    @Override
    public int getMaxItemUseDuration (ItemStack par1ItemStack)
    {
        return 72000;
    }

    @Override
    public boolean hitEntity (ItemStack stack, EntityLivingBase mob, EntityLivingBase player)
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
        if (!stack.hasTagCompound())
            return false;

        if (player instanceof EntityPlayerMP)
        {
            EntityPlayerMP mplayer = (EntityPlayerMP)player;
            World world = player.worldObj;
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (!tags.hasKey("AOEBreaking") || !tags.getBoolean("AOEBreaking"))
            {
                tags.setBoolean("AOEBreaking", true);
                for (int yPos = y + 1; yPos < y + 9; yPos++)
                {
                    Block block = world.getBlock(x, yPos, z);
                    if (block != null && block.getMaterial() == Material.wood
                        && block.getPlayerRelativeBlockHardness(mplayer, world, x, yPos, z) > 0)
                        mplayer.theItemInWorldManager.tryHarvestBlock(x, yPos, z);
                    else
                        break;
                }
                tags.setBoolean("AOEBreaking", false);
            }
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }
}
