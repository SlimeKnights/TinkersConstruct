package tconstruct.library.weaponry;

import tconstruct.library.TConstructRegistry;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.client.CrosshairType;
import tconstruct.util.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.TextureHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Weapons that utilize ammo that uses the ammo system to shoot projectiles.
 * Bows,...
 */
public abstract class ProjectileWeapon extends ToolCore implements IAccuracy, IWindup {
    public ProjectileWeapon(int baseDamage, String name) {
        super(baseDamage);

        this.setCreativeTab(TConstructRegistry.weaponryTab);
    }

    @SideOnly(Side.CLIENT)
    public CrosshairType getCrosshairType() {
        return CrosshairType.SQUARE;
    }

    @Override
    public boolean zoomOnWindup(ItemStack itemStack) {
        return false;
    }

    @Override
    public float getZoom(ItemStack itemStack) {
        return 1.0f;
    }

    /**
     * Searches the player for ammo to use
     * @param player the player
     * @return the itemstack found to be ammo
     */
    public abstract ItemStack searchForAmmo(EntityPlayer player, ItemStack weapon);

    /**
     * Creates the projectile to be fired.
     * @param ammo The ammo used
     * @param world world.
     * @param player player.
     * @param speed the speed calculated for the projectile
     * @param accuracy the accuracy calculated for the projectile
     * @return A banana.
     */
    protected abstract Entity createProjectile(ItemStack ammo, World world, EntityPlayer player, float speed, float accuracy);


    /* Accuracy */
    public abstract float minAccuracy(ItemStack itemStack);
    public abstract float maxAccuracy(ItemStack itemStack);

    public float getAccuracy(ItemStack itemStack, EntityPlayer player)
    {
        return getAccuracy(itemStack, getMaxItemUseDuration(itemStack) -  player.getItemInUseCount());
    }

    public float getAccuracy(ItemStack itemStack, int time)
    {
        float dif = minAccuracy(itemStack) - maxAccuracy(itemStack);

        return minAccuracy(itemStack) - dif * getWindupProgress(itemStack, time);
    }

    /* Windup */
    public int getWindupTime(ItemStack itemStack) {
        NBTTagCompound toolTag = itemStack.getTagCompound().getCompoundTag("InfiTool");
        return (int)((float)toolTag.getInteger("DrawSpeed")*windupModifier(itemStack));
    }

    protected float windupModifier(ItemStack itemStack) { return 1.0f; }

    public float getWindupProgress(ItemStack itemStack, EntityPlayer player)
    {
        // what are you doing!
        if(player.inventory.getCurrentItem() != itemStack)
            return 0f;

        // are we using it?
        if(player.getItemInUse() == null)
            return 0f;

        return getWindupProgress(itemStack, getMaxItemUseDuration(itemStack) -  player.getItemInUseCount());
    }

    public float getWindupProgress(ItemStack itemStack, int timeInUse)
    {
        float time = (float) timeInUse;
        float windup = getWindupTime(itemStack);
        if(time > windup)
            return 1.0f;

        return time/windup;
    }

    public float getProjectileSpeed(ItemStack itemStack)
    {
        NBTTagCompound toolTag = itemStack.getTagCompound().getCompoundTag("InfiTool");
        return toolTag.getFloat("FlightSpeed") * projectileSpeedModifier(itemStack);
    }

    protected float projectileSpeedModifier(ItemStack itemStack) { return 1.0f; }

    /* Bow usage */
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        if(getWindupTime(stack) == 0.0f)
            return stack;

        // only if ammo is present
        if(searchForAmmo(player, stack) != null)
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing (ItemStack weapon, World world, EntityPlayer player, int useRemaining)
    {
        int time = this.getMaxItemUseDuration(weapon) - useRemaining;

        // we abuse the arrowLooseEvent for all projectiles
        ArrowLooseEvent event = new ArrowLooseEvent(player, weapon, time);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        time = event.charge;

        // find ammo
        ItemStack ammo = searchForAmmo(player, weapon);

        // no ammo found. :(
        if(ammo == null)
            return;

        NBTTagCompound toolTag = weapon.getTagCompound().getCompoundTag("InfiTool");
        float projectileSpeed = getProjectileSpeed(weapon);
        float windup = getWindupProgress(weapon, time);
        float accuracy = getAccuracy(weapon, time);

        // needs a minimum windup
        if(windup < this.getMinWindupProgress(weapon))
            return;

        // take windup time into account
        projectileSpeed *= windup;

        Entity projectile = createProjectile(ammo, world, player, projectileSpeed, accuracy);


        int reinforced = 0;
        if (toolTag.hasKey("Unbreaking"))
            reinforced = toolTag.getInteger("Unbreaking");

        if (random.nextInt(10) < 10 - reinforced)
            AbilityHelper.damageTool(weapon, 1, player, false);

        playFiringSound(world, player, weapon, ammo, projectileSpeed, accuracy);


        // use up ammo
        if(ammo.getItem() instanceof IAmmo)
            ((IAmmo) ammo.getItem()).consumeAmmo(1, ammo);
        else
            player.inventory.consumeInventoryItem(ammo.getItem());

        // FIREEEEEEE
        if (!world.isRemote)
            world.spawnEntityInWorld(projectile);
    }

    public abstract void playFiringSound(World world, EntityPlayer player, ItemStack weapon, ItemStack ammo, float speed, float accuracy);

    public HashMap<Integer, IIcon[]> animationHeadIcons = new HashMap<Integer, IIcon[]>();
    public HashMap<Integer, IIcon[]> animationHandleIcons = new HashMap<Integer, IIcon[]>();
    public HashMap<Integer, IIcon[]> animationAccessoryIcons = new HashMap<Integer, IIcon[]>();
    public HashMap<Integer, IIcon[]> animationExtraIcons = new HashMap<Integer, IIcon[]>();
    public HashMap<Integer, IIcon[]> animationEffectIcons = new HashMap<Integer, IIcon[]>();

    // todo: animated effects

    /**
     * return true if the current renderpass should use animations.
     * 0 == handle
     * 1 == head
     * 2 == accessory
     * 3 == extra
     */
    protected boolean animateLayer(int renderPass)
    {
        return false;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

        if(tags == null || renderPass > 10)
            return super.getIcon(stack, renderPass, player, usingItem, useRemaining);

        // are we drawing an effect?
        if(renderPass >= getPartAmount()) {
            // is the effect animated?
            String effect = "Effect" + (1 + renderPass - getPartAmount());
            if(tags.hasKey(effect)) {
                int index = tags.getInteger(effect);
                if(animationEffectIcons.get(index) != null)
                    return getCorrectAnimationIcon(animationEffectIcons, index, getWindupProgress(usingItem, getMaxItemUseDuration(usingItem) - useRemaining));
                else
                    // non-animated
                    return effectIcons.get(index);
            }
            return super.getIcon(stack, renderPass, player, usingItem, useRemaining);
        }

        // animate?
        if(!animateLayer(renderPass))
            return super.getIcon(stack, renderPass, player, usingItem, useRemaining);

        if(usingItem == null || stack != usingItem || !stack.hasTagCompound())
            return super.getIcon(stack, renderPass, player, usingItem, useRemaining);

        float progress = getWindupProgress(usingItem, getMaxItemUseDuration(usingItem) - useRemaining);
        // get the correct icon
        switch (renderPass)
        {
            case 0: return getCorrectAnimationIcon(animationHandleIcons, tags.getInteger("RenderHandle"), progress);
            case 1: return getCorrectAnimationIcon(animationHeadIcons, tags.getInteger("RenderHead"), progress);
            case 2: return getCorrectAnimationIcon(animationAccessoryIcons, tags.getInteger("RenderAccessory"), progress);
            case 3: return getCorrectAnimationIcon(animationExtraIcons, tags.getInteger("RenderExtra"), progress);
        }

        return emptyIcon;
    }

    protected IIcon getCorrectAnimationIcon(Map<Integer, IIcon[]> icons, int id, float progress)
    {
        // animation count, standard texture as reference
        float count = icons.get(-1).length - 1;
        int step = Math.round(progress * count);

        if(icons.containsKey(id))
            return icons.get(id)[step];

        // default icon
        return icons.get(-1)[step];
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);

        addAnimationIcons(headStrings, animationHeadIcons, iconRegister, getIconSuffix(0));
        addAnimationIcons(handleStrings, animationHandleIcons, iconRegister, getIconSuffix(2));
        addAnimationIcons(accessoryStrings, animationAccessoryIcons, iconRegister, getIconSuffix(3));
        addAnimationIcons(extraStrings, animationExtraIcons, iconRegister, getIconSuffix(4));

        // animated effects...
        // find out the longest animation
        int count = 0;
        if(animationHeadIcons.get(-1) != null)
            count = Math.max(count, animationHeadIcons.get(-1).length);
        if(animationHandleIcons.get(-1) != null)
            count = Math.max(count, animationHandleIcons.get(-1).length);
        if(animationAccessoryIcons.get(-1) != null)
            count = Math.max(count, animationAccessoryIcons.get(-1).length);
        if(animationExtraIcons.get(-1) != null)
            count = Math.max(count, animationExtraIcons.get(-1).length);


        for(Map.Entry<Integer, String> entry : effectStrings.entrySet())
        {
            IIcon[] anims = new IIcon[count];
            boolean empty = true;
            for(int i = 0; i < count; i++) {
                String tex = entry.getValue() + "_" + (i+1);
                if (TextureHelper.itemTextureExists(tex)) {
                    anims[i] = iconRegister.registerIcon(tex);
                    empty = false;
                }
            }
            if(!empty)
                animationEffectIcons.put(entry.getKey(), anims);
        }

        // default for effects is blank
        IIcon[] anims = new IIcon[count];
        for(int i = 0; i < count; i++)
            anims[i] = blankSprite;
        animationEffectIcons.put(-1, anims);
    }

    private void addAnimationIcons(HashMap<Integer, String> textures, HashMap<Integer, IIcon[]> icons, IIconRegister iconRegister, String standard)
    {
        icons.clear();

        // we use the standard to determine how many animations there are
        if(standard == null || standard.isEmpty())
            return;

        int count = 1;
        standard =  getDefaultTexturePath() + "/" + standard;
        while(TextureHelper.itemTextureExists(standard + "_" + count))
            count++;

        count--;

        // add the standard icons
        IIcon[] anims = new IIcon[count];
        for(int i = 0; i < count; i++)
            anims[i] = iconRegister.registerIcon(standard + "_" + (i+1));
        icons.put(-1, anims);

        // now do the same for each entry
        for(Map.Entry<Integer, String> entry : textures.entrySet())
        {
            anims = new IIcon[count];
            boolean empty = true;
            for(int i = 0; i < count; i++) {
                String tex = entry.getValue() + "_" + (i+1);
                if (TextureHelper.itemTextureExists(tex)) {
                    anims[i] = iconRegister.registerIcon(tex);
                    empty = false;
                }
            }
            if(!empty)
                icons.put(entry.getKey(), anims);
        }
    }
}
