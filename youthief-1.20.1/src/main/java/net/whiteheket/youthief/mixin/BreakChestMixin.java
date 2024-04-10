package net.whiteheket.youthief.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageGossipType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;



@Mixin(Block.class)
public class BreakChestMixin {
    @Unique
    private static final BooleanProperty NATURALLY_GENERATED = BooleanProperty.of("naturally_generated");

    @Unique
    private static boolean isNaturallyGenerated(BlockState state) {
        return state != null && state.get(NATURALLY_GENERATED) != null && state.get(NATURALLY_GENERATED);
    }

    @Inject(at = @At(value= "HEAD"),method = "onBreak")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci){
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof BarrelBlockEntity) {
            if (isNaturallyGenerated(state) && ! player.isSpectator()){
                AngryVillager(world, pos.getX(), pos.getY(), pos.getZ(), player);
            }
        }
    }

    @Unique
    private void AngryVillager(World world, double x, double y, double z, Entity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            Boolean extra = player.hasStatusEffect(StatusEffects.INVISIBILITY);
            if (!(extra && player.getInventory().getArmorStack(0).isEmpty() && player.getInventory().getArmorStack(1).isEmpty() && player.getInventory().getArmorStack(2).isEmpty() && player.getInventory().getArmorStack(3).isEmpty())) {
                final Vec3d center = new Vec3d((x + 0.5), (y + 0.5), (z + 0.5));
                List<Entity> entfound = world.getEntitiesByClass(Entity.class, new Box(center, center).expand((20 * 2) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.squaredDistanceTo(center))).collect(Collectors.toList());
                {

                    for (Entity entityiterator : entfound) {
                        if (entityiterator instanceof VillagerEntity) {
                            VillagerEntity ent = (VillagerEntity) entityiterator;
                            ent.getGossip().startGossip(player.getUuid(), VillageGossipType.MAJOR_NEGATIVE,40);
                            world.addParticle(ParticleTypes.ANGRY_VILLAGER, ent.getX(), (ent.getY() + ent.getEyeY()), ent.getZ(), 0, 1, 0);
                            world.playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.ENTITY_VILLAGER_HURT, SoundCategory.VOICE, 3, 2);
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 28800, 5,false,false));
                            ent.wakeUp();
                        }
                    }
                }
            }
            player.sendMessage(Text.keybind("translation.youthief.cry"), false);
        }
    }
}
