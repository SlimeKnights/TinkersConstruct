package tconstruct.weaponry.weapons;

import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.weaponry.entity.BoltEntity;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.library.weaponry.ProjectileWeapon;
import tconstruct.util.Reference;
import tconstruct.library.weaponry.IAmmo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.tools.TinkerTools;

import java.util.List;

public class Crossbow extends ProjectileWeapon {
    public Crossbow() {
        super(0, "Crossbow");
        this.setMaxDamage(100);
    }

    public boolean isLoaded(ItemStack itemStack)
    {
        if(itemStack.hasTagCompound())
            return isLoaded(itemStack.getTagCompound().getCompoundTag("InfiTool"));

        return false;
    }

    public boolean isLoaded(NBTTagCompound tags)
    {
        return tags.getBoolean("Loaded");
    }

    public ItemStack getLoadedAmmo(ItemStack itemStack)
    {
        NBTTagCompound tags = itemStack.getTagCompound().getCompoundTag("InfiTool");
        if(!isLoaded(tags))
            return null;

        return ItemStack.loadItemStackFromNBT(tags.getCompoundTag("LoadedItem"));
    }

    @Override
    public int getWindupTime(ItemStack itemStack) {
        NBTTagCompound toolTag = itemStack.getTagCompound().getCompoundTag("InfiTool");
        return (int)((float)toolTag.getInteger("DrawSpeed") * 2.5f);
    }

    @Override
    public float getMinWindupProgress(ItemStack itemStack) {
        return 1.0f;
    }

    @Override
    public float getProjectileSpeed(ItemStack itemStack) {
        return super.getProjectileSpeed(itemStack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

        // unload on shift-rightclick
        if(player.isSneaking())
            if(unload(stack, player, tags))
                return stack;

        // loaded
        if(tags.getBoolean("Loaded"))
            fire(stack, world, player);

        // reload (automatically after firing)
        initiateReload(stack, player, tags);

        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        super.onUpdate(stack, world, entity, par4, par5);

        if(!stack.hasTagCompound())
            return;

        if(!(entity instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) entity;
        if(player.inventory.getCurrentItem() != stack)
            return;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if(tags.hasKey("Reloading"))
        {
            int timeLeft = tags.getInteger("Reloading");
            timeLeft--;
            if(timeLeft > 0)
                tags.setInteger("Reloading", timeLeft);
            else {
                tags.removeTag("Reloading");
                reload(stack, player, world, tags);
            }
        }
    }

    @Override
    public float getWindupProgress(ItemStack itemStack, EntityPlayer player) {
        NBTTagCompound tags = itemStack.getTagCompound().getCompoundTag("InfiTool");

        // loaded, full accuracy
        if(tags.getBoolean("Loaded"))
            return 1.0f;
        // not loaded, but reloading -> progress
        else if(tags.hasKey("Reloading"))
            return 1.0f - ((float)tags.getInteger("Reloading"))/((float)getWindupTime(itemStack));
        // not loaded and not reloading -> no accuracy!
        else
            return 0.0f;
    }

    public void initiateReload(ItemStack stack, EntityPlayer player, NBTTagCompound tags) {
        if(tags.getBoolean("Broken"))
            return;

        // has ammo?
        if(searchForAmmo(player, stack) != null)
            if(!tags.hasKey("Reloading"))
                // start reloading
                tags.setInteger("Reloading", getWindupTime(stack));
    }

    // called after the reloading is done. Does the actual loading
    public boolean reload(ItemStack weapon, EntityPlayer player, World world, NBTTagCompound tags)
    {
        ItemStack ammo = searchForAmmo(player, weapon);
        // no ammo present
        if(ammo == null)
            return false;

        // already loaded
        if(tags.getBoolean("Loaded"))
            return false;

        ItemStack copy = ammo.copy();
        copy.getTagCompound().getCompoundTag("InfiTool").setInteger("Ammo", 1);


        // load ammo into nbt
        NBTTagCompound ammotag = new NBTTagCompound();
        copy.writeToNBT(ammotag);
        //ammotag.getCompoundTag("tag"; // set ammo count to 1
        tags.setTag("LoadedItem", ammotag);
        tags.setBoolean("Loaded", true);

        // remove loaded item
        if(ammo.getItem() instanceof IAmmo)
            ((IAmmo) ammo.getItem()).consumeAmmo(1, ammo);
        else
            player.inventory.consumeInventoryItem(ammo.getItem());

        playReloadSound(world, player, weapon, ammo);

        return true;
    }

    // stops reloading, or unloads the ammo if it has been loaded
    public boolean unload(ItemStack weapon, EntityPlayer player, NBTTagCompound tags)
    {
        // is reloading?
        if(tags.hasKey("Reloading"))
        {
            // stop it
            tags.removeTag("Reloading");
            return true;
        }

        // loaded?
        if(tags.getBoolean("Loaded"))
        {
            // unload
            ItemStack loadedItem = ItemStack.loadItemStackFromNBT(tags.getCompoundTag("LoadedItem"));
            AmmoItem ammo = (AmmoItem) loadedItem.getItem();
            // try to pick it up
            ammo.pickupAmmo(loadedItem, searchForAmmo(player, weapon), player);

            // unloaded
            tags.setBoolean("Loaded", false);
            tags.removeTag("LoadedItem");

            return true;
        }

        // nothing to unload
        return false;
    }

    // called if loaded to SHOOOOOOOT
    public void fire(ItemStack weapon, World world, EntityPlayer player)
    {
        NBTTagCompound tags = weapon.getTagCompound().getCompoundTag("InfiTool");
        // not loaded
        if(!tags.getBoolean("Loaded"))
            return;

        // get ammo
        ItemStack ammo = ItemStack.loadItemStackFromNBT(tags.getCompoundTag("LoadedItem"));

        // unload
        tags.setBoolean("Loaded", false);
        tags.removeTag("LoadedItem");

        // safety
        if(ammo == null)
            return;

        float projectileSpeed = getProjectileSpeed(weapon);
        float windup = getWindupTime(weapon); // max windup time
        float accuracy = getAccuracy(weapon, (int)windup);

        Entity projectile = createProjectile(ammo, world, player, projectileSpeed, accuracy);

        int reinforced = 0;

        if (tags.hasKey("Unbreaking"))
            reinforced = tags.getInteger("Unbreaking");

        if (random.nextInt(10) < 10 - reinforced)
            AbilityHelper.damageTool(weapon, 1, player, false);

        playFiringSound(world, player, weapon, ammo, projectileSpeed, accuracy);

        // thwock
        if (!world.isRemote)
            world.spawnEntityInWorld(projectile);
    }

    @Override
    public ItemStack searchForAmmo(EntityPlayer player, ItemStack weapon) {
        // arrow priority: hotbar > inventory, tinker arrows > regular arrows
        ItemStack[] inventory = player.inventory.mainInventory;

        // check hotbar for tinker arrows
        for(int i = 0; i < inventory.length; i++)
        {
            ItemStack stack = inventory[i];
            if(stack == null)
                continue;
            if(!(stack.getItem() instanceof BoltAmmo))
                continue;
            if(((IAmmo) stack.getItem()).getAmmoCount(stack) <= 0)
                continue;

            return stack;
        }

        return null;
    }

    @Override
    protected Entity createProjectile(ItemStack ammo, World world, EntityPlayer player, float speed, float accuracy) {
        EntityArrow arrow;

        ItemStack reference = ammo.copy();
        reference.stackSize = 1;
        reference.getTagCompound().getCompoundTag("InfiTool").setInteger("Ammo", 1);
        arrow = new BoltEntity(world, player, speed, accuracy, reference);

        if(player.capabilities.isCreativeMode)
            arrow.canBePickedUp = 2;

        return arrow;
    }

    @Override
    public float minAccuracy(ItemStack itemStack) {
        return 0;
    }

    @Override
    public float maxAccuracy(ItemStack itemStack) {
        return 0;
    }

    public void playReloadSound(World world, EntityPlayer player, ItemStack weapon, ItemStack ammo) {
        world.playSoundAtEntity(player, Reference.resource("crossbowReload"), 1.0f, 1.0f);
    }

    @Override
    public void playFiringSound(World world, EntityPlayer player, ItemStack weapon, ItemStack ammo, float speed, float accuracy) {
        world.playSoundAtEntity(player, "random.bow", 1.0F, 0.5F);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        if(!animateLayer(renderPass) && renderPass < getPartAmount())
            return super.getIcon(stack, renderPass);

        if(!stack.hasTagCompound())
            return super.getIcon(stack, renderPass);

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if(tags == null || renderPass > 10)
            return super.getIcon(stack, renderPass);

        float progress;

        if(tags.getBoolean("Loaded"))
            progress = 1;
        else if(!tags.hasKey("Reloading"))
            return super.getIcon(stack, renderPass);
        else
            progress = getWindupProgress(stack, getWindupTime(stack) - tags.getInteger("Reloading"));

        // are we drawing an effect?
        if(renderPass >= getPartAmount()) {
            // is the effect animated?
            String effect = "Effect" + (1 + renderPass - getPartAmount());
            if(tags.hasKey(effect)) {
                int index = tags.getInteger(effect);
                if(animationEffectIcons.get(index) != null)
                    return getCorrectAnimationIcon(animationEffectIcons, index, progress);
                else
                    // non-animated
                    return effectIcons.get(index);
            }
            return super.getIcon(stack, renderPass);
        }

        // get the correct icon
        switch (renderPass)
        {
            case 0: return getCorrectAnimationIcon(animationHandleIcons, tags.getInteger("RenderHandle"), progress);
            case 1: return getCorrectAnimationIcon(animationHeadIcons, tags.getInteger("renderHead"), progress);
            case 2: return getCorrectAnimationIcon(animationAccessoryIcons, tags.getInteger("renderAccessory"), progress);
            case 3: return getCorrectAnimationIcon(animationExtraIcons, tags.getInteger("renderExtra"), progress);
        }

        return emptyIcon;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
            case 0:
                return "_crossbow_bow"; // head
            case 1:
                return ""; // broken
            case 2:
                return "_crossbow_body"; // handle
            case 3:
                return "_crossbow_string"; // accessory
            case 4:
                return "_crossbow_binding"; // extra
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_crossbow_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "crossbow";
    }

    @Override
    public int getPartAmount() {
        return 4;
    }

    @Override
    protected boolean animateLayer(int renderPass) {
        return renderPass == 1 || renderPass == 2;
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerWeaponry.partCrossbowLimb;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerWeaponry.partCrossbowBody;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerWeaponry.bowstring;
    }

    @Override
    public Item getExtraItem() {
        return TinkerTools.toughBinding;
    }

    @Override
    public String[] getTraits() {
        return new String[] {"weapon", "ranged", "crossbow", "windup"};
    }


    @Override
    public void buildTool (int id, String name, List list)
    {
        // all creative bows use regular bowstring
        ItemStack headStack = new ItemStack(getHeadItem(), 1, id);
        ItemStack handleStack = new ItemStack(getHandleItem(), 1, id);
        ItemStack accessoryStack = new ItemStack(getAccessoryItem(), 1, 0); // bowstring
        ItemStack extraStack = getPartAmount() > 3 ? new ItemStack(getExtraItem(), 1, id) : null;

        ItemStack tool = ToolBuilder.instance.buildTool(headStack, handleStack, accessoryStack, extraStack, "");
        if (tool != null)
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if(renderPass != 2 || !stack.hasTagCompound())
            return super.getColorFromItemStack(stack, renderPass);

        // bowstring: custom material -> custom coloring
        // todo: move this into the custom material itself
        int mat = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Accessory");
        switch(mat)
        {
            case 0: return 0xffffffff; // string = white
            case 1: return 0xffccccff; // macig string = light blue
            case 2: return 0xffffcccc; // flamestring = light red
        }
        return super.getColorFromItemStack(stack, renderPass);
    }
}
