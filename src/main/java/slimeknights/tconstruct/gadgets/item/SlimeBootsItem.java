package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.item.ArmorTooltipItem;
import slimeknights.tconstruct.library.TinkerNetwork;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;

public class SlimeBootsItem extends ArmorTooltipItem implements IDyeableArmorItem {

  private static final IArmorMaterial SLIME = new IArmorMaterial() {
    private final Ingredient empty_repair_material = Ingredient.fromItems(Items.AIR);

    @Override
    public int getDurability(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getEnchantability() {
      return 0;
    }

    @Override
    public SoundEvent getSoundEvent() {
      return SoundEvents.BLOCK_SLIME_BLOCK_PLACE;
    }

    @Override
    public Ingredient getRepairMaterial() {
      return this.empty_repair_material;
    }

    @Override
    public String getName() {
      return Util.resource("slime");
    }

    @Override
    public float getToughness() {
      return 0;
    }
  };

  private final SlimeBlock.SlimeType slimeType;

  public SlimeBootsItem(SlimeBlock.SlimeType slimeType) {
    super(SLIME, EquipmentSlotType.FEET, (new Properties()).group(TinkerRegistry.tabGadgets).maxStackSize(1));
    this.slimeType = slimeType;
  }

  @Override
  public boolean hasColor(ItemStack stack) {
    return true;
  }

  @Override
  public int getColor(ItemStack stack) {
    return this.slimeType.getBallColor();
  }

  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
    return HashMultimap.<String, AttributeModifier>create();
  }

  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (entity == null) {
      return;
    }

    ItemStack feet = entity.getItemStackFromSlot(EquipmentSlotType.FEET);
    if (feet.getItem() != this) {
      return;
    }

    // thing is wearing slime boots. let's get bouncyyyyy
    boolean isClient = entity.getEntityWorld().isRemote;
    if (!entity.isCrouching() && event.getDistance() > 2) {
      event.setDamageMultiplier(0);
      entity.fallDistance = 0;

      if (isClient) {
        entity.setMotion(entity.getMotion().x, entity.getMotion().y * -0.9, entity.getMotion().z);
        //entity.motionY = event.distance / 15;
        //entity.motionX = entity.posX - entity.lastTickPosX;
        //entity.motionZ = entity.posZ - entity.lastTickPosZ;
        //event.entityLiving.motionY *= -1.2;
        //event.entityLiving.motionY += 0.8;
        entity.isAirBorne = true;
        entity.onGround = false;
        double f = 0.91d + 0.04d;
        //System.out.println((entityLiving.worldObj.isRemote ? "client: " : "server: ") + entityLiving.motionX);
        // only slow down half as much when bouncing
        entity.setMotion(entity.getMotion().x / f, entity.getMotion().y, entity.getMotion().z / f);
        TinkerNetwork.instance.sendToServer(new BouncedPacket());
      }
      else {
        event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
      }

      entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
      SlimeBounceHandler.addBounceHandler(entity, entity.getMotion().y);
    }
    else if (!isClient && entity.isCrouching()) {
      event.setDamageMultiplier(0.2f);
    }
  }

  @OnlyIn(Dist.CLIENT)
  public static int getColorFromStack(ItemStack stack) {
    if (stack.getItem() == GadgetItems.slime_boots_blue) {
      return SlimeBlock.SlimeType.BLUE.getBallColor();
    }
    else if (stack.getItem() == GadgetItems.slime_boots_purple) {
      return SlimeBlock.SlimeType.PURPLE.getBallColor();
    }
    else if (stack.getItem() == GadgetItems.slime_boots_magma) {
      return SlimeBlock.SlimeType.MAGMA.getBallColor();
    }
    else if (stack.getItem() == GadgetItems.slime_boots_green) {
      return SlimeBlock.SlimeType.GREEN.getBallColor();
    }
    else if (stack.getItem() == GadgetItems.slime_boots_blood) {
      return SlimeBlock.SlimeType.BLOOD.getBallColor();
    }
    else {
      return SlimeBlock.SlimeType.GREEN.getBallColor();
    }
  }
}
