package slimeknights.tconstruct.library.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import slimeknights.tconstruct.library.Util;

public class PotionSlimeBounce extends TinkerPotion {

  public PotionSlimeBounce() {
    super(Util.getResource("slimebounce"), false, true);
  }

  public PotionEffect apply(EntityLivingBase entityLivingBase) {
    return this.apply(entityLivingBase, 32767);
  }

  @Override
  public boolean isReady(int p_76397_1_, int p_76397_2_) {
    // applies every tick
    return true;
  }

  @Override
  public void performEffect(EntityLivingBase entity, int p_76394_2_) {
    // keep movement going in the air
    if(!entity.onGround) {
      // Entity living physics: EntityLivingBase.moveEntityWithHeading#1667
      // we just revert the speed decrease in x/z direction
      double f = 0.91d;
      entity.motionX /= f;
      //entity.motionY += 0.8;
      //entity.motionY /= 0.9800000190734863d;
      entity.motionZ /= f;
      entity.isAirBorne = true;
      // apparently not needed
      //if(entity instanceof EntityPlayerMP) {
        //((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
      //}
    }
  }

  @Override
  public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
    mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
    mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.slime_ball), x + 7, y + 8);
  }
}
