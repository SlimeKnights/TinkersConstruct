package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.projectile.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.capability.projectile.ITinkerProjectile;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

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

  private int getBeheadingLevel(DamageSource source) {
    if (!(source.getTrueSource() instanceof EntityLivingBase)) {
      return 0;
    }
    ItemStack item = CapabilityTinkerProjectile.getTinkerProjectile(source)
        .map(ITinkerProjectile::getItemStack)
        .orElse(((EntityLivingBase)source.getTrueSource()).getHeldItem(EnumHand.MAIN_HAND));

    if (item.isEmpty()) {
      return 0;
    }

    NBTTagCompound tag = TinkerUtil.getModifierTag(item, getIdentifier());
    int level = ModifierNBT.readTag(tag).level;

    if(level == 0) {
      tag = TinkerUtil.getModifierTag(item, CLEAVER_MODIFIER_ID);
      level = ModifierNBT.readTag(tag).level;
    }

    return level;
  }

  @SubscribeEvent
  public void onLivingDrops(LivingDropsEvent event) {
    // has beheading
    int level = getBeheadingLevel(event.getSource());
    if(shouldDropHead(level)) {
      ItemStack head = TinkerRegistry.getHeadDrop(event.getEntityLiving());
      if(!head.isEmpty() && !alreadyContainsDrop(event, head)) {
        EntityItem entityitem = new EntityItem(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, head);
        entityitem.setDefaultPickupDelay();
        event.getDrops().add(entityitem);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void playerDrop(LivingDeathEvent event) {
    // if keepInventory is true, players do not fire the living drops event
    EntityLivingBase entity = event.getEntityLiving();
    if(entity.world.getGameRules().getBoolean("keepInventory") && entity instanceof EntityPlayerMP) {
      int level = getBeheadingLevel(event.getSource());

      if(shouldDropHead(level)) {
        ItemStack head = TinkerRegistry.getHeadDrop(entity);
        if(!head.isEmpty()) {
          ((EntityPlayerMP) entity).dropItem(head, true);
        }
      }
    }
  }

  private boolean shouldDropHead(int level) {
    return level > 0 && level > random.nextInt(10);
  }

  private boolean alreadyContainsDrop(LivingDropsEvent event, ItemStack head) {
    // special case players: we want to add a new head drop even if they have their own head in their inventory
    if(event.getEntityLiving() instanceof EntityPlayerMP) {
      return false;
    }
    return event.getDrops().stream()
                .map(EntityItem::getItem)
                .anyMatch(drop -> ItemStack.areItemStacksEqual(drop, head));
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
