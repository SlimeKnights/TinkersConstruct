package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitFlammable extends AbstractTrait {

  public TraitFlammable() {
    super("flammable", 0xffffff);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onPlayerHurt(LivingHurtEvent livingHurtEvent) {
    if(livingHurtEvent.getEntityLiving() instanceof EntityPlayer && !livingHurtEvent.getEntityLiving().getEntityWorld().isRemote) {
      EntityPlayer entityPlayer = (EntityPlayer) livingHurtEvent.getEntityLiving();
      Entity attacker = livingHurtEvent.getSource().getTrueSource();

      if(attacker instanceof EntityLiving && !attacker.isDead) {
        ItemStack tool = ToolHelper.playerIsHoldingItemWith(entityPlayer, this::isToolWithTrait);
        if(!tool.isEmpty() && !ToolHelper.isBroken(tool)) {
          attacker.setFire(3);
        }
      }
    }
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    // block fire damage
    if(event.getSource().isFireDamage()) {
      event.setCanceled(true);
      ToolHelper.damageTool(tool, 3, player);
    }
  }
}
