package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.ModifierTagHolder;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class ModMendingMoss extends ModifierTrait {

  public static final int MENDING_MOSS_LEVELS = 10;

  private static final String TAG_STORED_XP = "stored_xp";
  private static final String TAG_LAST_HEAL = "heal_timestamp";

  private static final int DELAY = 20 * 7 + 10; // every 7.5s

  public ModMendingMoss() {
    super("mending_moss", 0x43ab32, 3, 0);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    // only in the hotbar of a player
    if(!world.isRemote && entity instanceof EntityLivingBase) {
      // must be in hotbar or offhand for players
      if(entity instanceof EntityPlayer
         && !InventoryPlayer.isHotbar(itemSlot)
         && ((EntityPlayer) entity).getHeldItemOffhand() != tool) {
        return;
      }

      // needs ot be repaired and is in hotbar or offhand
      if(needsRepair(tool)) {
        if(useXp(tool, world)) {
          ToolHelper.healTool(tool, getDurabilityPerXP(tool), (EntityLivingBase) entity);
        }
      }
    }
  }

  @SubscribeEvent
  public void onPickupXp(PlayerPickupXpEvent event) {
    // try mainhand first, then offhand
    List<ItemStack> tools = Lists.newArrayList(event.getEntityPlayer().getHeldItemMainhand(),
                                               event.getEntityPlayer().getHeldItemOffhand());

    EntityXPOrb entityXPOrb = event.getOrb();

    for(ItemStack itemStack : tools) {
      if(!itemStack.isEmpty() && isMendingMossModified(itemStack)) {
        int stored = storeXp(entityXPOrb.xpValue, itemStack);
        entityXPOrb.xpValue -= stored;
      }
    }
  }

  private boolean isMendingMossModified(ItemStack itemStack) {
    return TinkerUtil.hasModifier(TagUtil.getTagSafe(itemStack), getModifierIdentifier());
  }

  private boolean needsRepair(ItemStack itemStack) {
    return !itemStack.isEmpty() && itemStack.getItemDamage() > 0 && !ToolHelper.isBroken(itemStack);
  }

  private int getDurabilityPerXP(ItemStack itemStack) {
    return 2 + ModifierTagHolder.getModifier(itemStack, getModifierIdentifier()).getTagData(Data.class).level;
  }

  // 100 * 3^(level-1)
  private int getMaxXp(int level) {
    if(level <= 1) {
      return 100;
    }

    return getMaxXp(level - 1) * 3;
  }

  private boolean canStoreXp(Data data) {
    return data.storedXp < getMaxXp(data.level);
  }

  private int storeXp(int amount, ItemStack itemStack) {
    ModifierTagHolder modtag = ModifierTagHolder.getModifier(itemStack, getModifierIdentifier());
    Data data = modtag.getTagData(Data.class);

    int change = 0;
    if(canStoreXp(data)) {
      int max = getMaxXp(data.level);
      change = Math.min(amount, max - data.storedXp);
      data.storedXp += change;
      modtag.save();
    }
    return change;
  }

  private boolean useXp(ItemStack itemStack, World world) {
    ModifierTagHolder modtag = ModifierTagHolder.getModifier(itemStack, getModifierIdentifier());
    Data data = modtag.getTagData(Data.class);

    if(data.storedXp > 0 && world.getTotalWorldTime() - data.lastHeal > DELAY) {
      data.storedXp--;
      data.lastHeal = world.getTotalWorldTime();
      modtag.save();
      return true;
    }
    return false;
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    Data data = ModifierNBT.readTag(modifierTag, Data.class);
    assert data != null;
    String loc = String.format(LOC_Extra, getIdentifier());
    return ImmutableList.of(
        Util.translateFormatted(loc, data.storedXp)
    );
  }

  public static class Data extends ModifierNBT {

    public int storedXp;
    public long lastHeal;

    @Override
    public void read(NBTTagCompound tag) {
      super.read(tag);
      storedXp = tag.getInteger(TAG_STORED_XP);
      lastHeal = tag.getLong(TAG_LAST_HEAL);
    }

    @Override
    public void write(NBTTagCompound tag) {
      super.write(tag);
      tag.setInteger(TAG_STORED_XP, storedXp);
      tag.setLong(TAG_LAST_HEAL, lastHeal);
    }
  }
}
