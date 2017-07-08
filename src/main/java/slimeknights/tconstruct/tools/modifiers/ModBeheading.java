package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

// todo: make some kind of class->head registry that can be expanded via IMC for the lookup
public class ModBeheading extends ToolModifier {

  private static String BEHEADING_ID = "beheading";
  private static String CLEAVER_MODIFIER_ID = BEHEADING_ID + "_cleaver";
  private static int BEHEADING_COLOR = 0x10574b;

  public static ModBeheading CLEAVER_BEHEADING_MOD = new ModBeheadingCleaver();

  public ModBeheading() {
    this("beheading");

    addAspects(ModifierAspect.freeModifier);

    MinecraftForge.EVENT_BUS.register(this);
  }

  ModBeheading(String traitBeheading) {
    super(traitBeheading, BEHEADING_COLOR);

    addAspects(new ModifierAspect.LevelAspect(this, 10), new ModifierAspect.DataAspect(this));
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // remove the cleaver beheading if present and add it to the beheading modifier
    NBTTagCompound tag = TinkerUtil.getModifierTag(rootCompound, CLEAVER_MODIFIER_ID);
    if(!tag.hasNoTags()) {
      // update level if it hasn't been done before
      if(!modifierTag.getBoolean("absorbedCleaver")) {
        ModifierNBT data = ModifierNBT.readTag(modifierTag);
        data.level += ModifierNBT.readTag(tag).level;
        data.write(modifierTag);
        modifierTag.setBoolean("absorbedCleaver", true);
      }

      // remove other tag
      NBTTagList tagList = TagUtil.getModifiersTagList(rootCompound);
      int index = TinkerUtil.getIndexInCompoundList(tagList, CLEAVER_MODIFIER_ID);
      tagList.removeTag(index);

      TagUtil.setModifiersTagList(rootCompound, tagList);
    }
  }

  @SubscribeEvent
  public void onLivingDrops(LivingDropsEvent event) {
    if(event.getSource().getTrueSource() instanceof EntityPlayer) {
      ItemStack item = ((EntityPlayer) event.getSource().getTrueSource()).getHeldItem(EnumHand.MAIN_HAND);
      NBTTagCompound tag = TinkerUtil.getModifierTag(item, getIdentifier());
      int level = ModifierNBT.readTag(tag).level;

      if(level == 0) {
        tag = TinkerUtil.getModifierTag(item, CLEAVER_MODIFIER_ID);
        level = ModifierNBT.readTag(tag).level;
      }

      // has beheading
      if(level > 0) {
        ItemStack head = getHeadDrop(event.getEntityLiving());
        if(head != null && level > random.nextInt(10)) {
          EntityItem entityitem = new EntityItem(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, head);
          entityitem.setDefaultPickupDelay();
          event.getDrops().add(entityitem);
        }
      }
    }
  }

  private ItemStack getHeadDrop(EntityLivingBase entity) {
    // meta 0: skeleton
    if(entity instanceof EntitySkeleton) {
      return new ItemStack(Items.SKULL, 1, 0);
    }
    // meta 1: wither skelly
    else if(entity instanceof EntityWitherSkeleton) {
      return new ItemStack(Items.SKULL, 1, 1);
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
      ItemStack head = new ItemStack(Items.SKULL, 1, 3);
      NBTTagCompound nametag = new NBTTagCompound();
      nametag.setString("SkullOwner", entity.getDisplayName().getFormattedText());
      head.setTagCompound(nametag);
      return head;
    }

    // no head
    return null;
  }

  private static class ModBeheadingCleaver extends ModBeheading {

    public ModBeheadingCleaver() {
      super(CLEAVER_MODIFIER_ID);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
      // do nothing
    }

    @Override
    public String getLocalizedDesc() {
      return Util.translate(LOC_Desc, BEHEADING_ID);
    }

    @Override
    public String getLocalizedName() {
      return Util.translate(LOC_Name, BEHEADING_ID);
    }
  }
}
