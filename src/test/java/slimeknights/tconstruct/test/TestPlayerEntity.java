package slimeknights.tconstruct.test;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class TestPlayerEntity extends PlayerEntity {

  public TestPlayerEntity(World worldIn) {
    super(worldIn, new GameProfile(UUID.randomUUID(), "test"));
  }

  @Override
  public boolean isSpectator() {
    return false;
  }

  @Override
  public boolean isCreative() {
    return false;
  }
}
