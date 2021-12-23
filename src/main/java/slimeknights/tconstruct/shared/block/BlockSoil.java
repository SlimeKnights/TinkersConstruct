package slimeknights.tconstruct.shared.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;
import java.util.Locale;

public class BlockSoil extends EnumBlock<BlockSoil.SoilTypes> {

  public static final PropertyEnum<SoilTypes> TYPE = PropertyEnum.create("type", SoilTypes.class);

  public BlockSoil() {
    super(Material.SAND, TYPE, SoilTypes.class);
    this.slipperiness = 0.8F;
    this.setHardness(3.0f);

    this.setSoundType(SoundType.SAND);

    setHarvestLevel("shovel", -1);
    setCreativeTab(TinkerRegistry.tabGeneral);
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for(SoilTypes type : SoilTypes.values()) {
      if(isTypeEnabled(type)) {
        list.add(new ItemStack(this, 1, type.getMeta()));
      }
    }
  }

  protected boolean isTypeEnabled(SoilTypes type) {
    switch(type) {
      case GROUT:
        return TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId);
      case SLIMY_MUD_BLUE:
        return TinkerCommons.matSlimeBallBlue != null;
      case SLIMY_MUD_MAGMA:
        return TinkerCommons.matSlimeBallMagma != null;
      case SLIMY_MUD_GREEN:
      case GRAVEYARD:
      case CONSECRATED:
        return true;
    }

    return false;
  }

  @Override
  public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    IBlockState state = worldIn.getBlockState(pos);
    switch(state.getValue(TYPE)) {
      case SLIMY_MUD_GREEN:
      case SLIMY_MUD_BLUE:
        processSlimyMud(entityIn);
        break;
      case GRAVEYARD:
        processGraveyardSoil(entityIn);
        break;
      case CONSECRATED:
        processConsecratedSoil(entityIn);
        break;
    }
  }

  // slow down
  protected void processSlimyMud(Entity entity) {
    entity.motionX *= 0.4;
    entity.motionZ *= 0.4;
    if(entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1));
    }
  }

  // damage and set undead entities on fire
  protected void processConsecratedSoil(Entity entity) {
    if(entity instanceof EntityLiving) {
      EntityLivingBase entityLiving = (EntityLivingBase) entity;
      if(entityLiving.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
        entityLiving.attackEntityFrom(DamageSource.MAGIC, 1);
        entityLiving.setFire(1);
      }
    }
  }

  // heal undead entities
  protected void processGraveyardSoil(Entity entity) {
    if(entity instanceof EntityLiving) {
      EntityLivingBase entityLiving = (EntityLivingBase) entity;
      if(entityLiving.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
        entityLiving.heal(1);
      }
    }
  }

  @Override
  public boolean canSustainPlant(@Nonnull IBlockState state, @Nonnull IBlockAccess world, BlockPos pos, @Nonnull EnumFacing direction, IPlantable plantable) {
    SoilTypes type = world.getBlockState(pos).getValue(TYPE);
    if(type == SoilTypes.SLIMY_MUD_GREEN || type == SoilTypes.SLIMY_MUD_BLUE) {
      // can sustain slimeplants
      return plantable.getPlantType(world, pos) == TinkerWorld.slimePlantType;
    }
    return super.canSustainPlant(state, world, pos, direction, plantable);
  }

  public enum SoilTypes implements IStringSerializable, EnumBlock.IEnumMeta {
    GROUT,
    SLIMY_MUD_GREEN,
    SLIMY_MUD_BLUE,
    GRAVEYARD,
    CONSECRATED,
    SLIMY_MUD_MAGMA;

    public final int meta;

    SoilTypes() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
