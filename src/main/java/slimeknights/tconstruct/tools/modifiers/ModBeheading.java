package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.TinkerTools;

// todo: make some kind of class->head registry that can be expanded via IMC for the lookup
public class ModBeheading extends ToolModifier {

  public ModBeheading() {
    super("beheading", 0x10574b);

    addAspects(new ModifierAspect.LevelAspect(this, 10), new ModifierAspect.DataAspect(this), ModifierAspect.freeModifier);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // nothing to do
  }

  @SubscribeEvent
  public void onLivingDrops(LivingDropsEvent event) {
    if(event.getSource().getEntity() instanceof EntityPlayer) {
      ItemStack item = ((EntityPlayer) event.getSource().getEntity()).getHeldItem(EnumHand.MAIN_HAND);
      NBTTagCompound tag = TinkerUtil.getModifierTag(item, getIdentifier());
      int level = ModifierNBT.readTag(tag).level;
      // has beheading
      if(level > 0) {
        if(item.getItem() == TinkerTools.cleaver) {
          level += 2;
        }

        ItemStack head = getHeadDrop(event.getEntityLiving());
        if(head != null && level > random.nextInt(10)) {
          EntityItem entityitem = new EntityItem(event.getEntityLiving().worldObj, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, head);
          entityitem.setDefaultPickupDelay();
          event.getDrops().add(entityitem);
        }
      }
    }
  }

  private ItemStack getHeadDrop(EntityLivingBase entity) {
    // meta 0,1: skeleton and wither skelly
    if(entity instanceof EntitySkeleton) {
      return new ItemStack(Items.SKULL, 1, ((EntitySkeleton) entity).getSkeletonType());
    }
    // meta 2: zombie
    else if(entity instanceof EntityZombie) {
      return new ItemStack(Items.SKULL, 1, 2);
    }
    // meta 4: creeper
    else if(entity instanceof EntityCreeper) {
      return new ItemStack(Items.SKULL, 1, 4);
    }
    // meta 3: player
    else if(entity instanceof EntityPlayer) {
      ItemStack head = new ItemStack(Items.SKULL, 1, 4);
      NBTTagCompound nametag = new NBTTagCompound();
      nametag.setString("SkullOwner", entity.getDisplayName().getFormattedText());
      head.setTagCompound(nametag);
      return head;
    }

    // no head
    return null;
  }
}
