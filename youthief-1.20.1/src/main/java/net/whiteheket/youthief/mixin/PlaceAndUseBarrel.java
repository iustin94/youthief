package net.whiteheket.youthief.mixin;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageGossipType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Mixin(BarrelBlock.class)
public class PlaceAndUseBarrel {

    @Unique
    private static final BooleanProperty NATURALLY_GENERATED = BooleanProperty.of("naturally_generated");

    @Unique
    private static boolean isNaturallyGenerated(BlockState state) {
        return state != null && state.getBlock() instanceof BarrelBlock && state.get(NATURALLY_GENERATED) != null && state.get(NATURALLY_GENERATED);
    }

    @Inject( at = @At("HEAD"),method = "appendProperties")
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(NATURALLY_GENERATED);
    }
    @Inject(at = @At(value = "HEAD"),method = "onPlaced")
    public void onPlaced(@NotNull World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (world.getBlockEntity(pos) instanceof BarrelBlockEntity) {
            world.setBlockState(pos, state.with(NATURALLY_GENERATED, false));
        }
    }
    @Inject(at = @At(value= "HEAD"),method = "onUse")
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BarrelBlockEntity) {
            if (isNaturallyGenerated(state) && ! player.isSpectator()) {
                AngryVillager(world, pos.getX(), pos.getY(), pos.getZ(), player);
            }
        }
    }
    @Unique
    private void AngryVillager(World world, double x, double y, double z, Entity entity) {
        int normolrange = (int) (20 *0.5);
        int sleepingrange =(int) (20*0.25);
        int finallyrange;
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            Boolean extra = player.hasStatusEffect(StatusEffects.INVISIBILITY);
            if (!(extra && player.getInventory().getArmorStack(0).isEmpty() && player.getInventory().getArmorStack(1).isEmpty() && player.getInventory().getArmorStack(2).isEmpty() && player.getInventory().getArmorStack(3).isEmpty())) {
                final Vec3d center = new Vec3d((x + 0.5), (y + 0.5), (z + 0.5));
                List<Entity> entfound = world.getEntitiesByClass(Entity.class, new Box(center, center).expand((20 * 2) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.squaredDistanceTo(center))).collect(Collectors.toList());
                {
                    for (Entity entityiterator : entfound) {
                        if (entityiterator instanceof VillagerEntity) {
                            if (((VillagerEntity) entityiterator).isSleeping()){
                                finallyrange = sleepingrange;
                            } else{
                                finallyrange =normolrange;
                            }
                            final Vec3d newcenter =new Vec3d(entityiterator.getX()+0.5,entityiterator.getY()+0.5, entityiterator.getZ()+0.5);
                            List<Entity> playerinrange = world.getEntitiesByClass(Entity.class, new Box(newcenter, newcenter).expand((finallyrange * 2) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.squaredDistanceTo(center))).collect(Collectors.toList());
                            for (Entity playerinrange2 : playerinrange){
                                if (playerinrange2 instanceof PlayerEntity) {
                                    VillagerEntity ent = (VillagerEntity) entityiterator;
                                    ent.wakeUp();
                                    ent.getGossip().startGossip(player.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 20);
                                    world.addParticle(ParticleTypes.ANGRY_VILLAGER, ent.getX(), (ent.getY() + ent.getEyeY()), ent.getZ(), 0, 1, 0);
                                    world.playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.ENTITY_VILLAGER_HURT, SoundCategory.VOICE, 1,(float)(Math.random() + 0.5));
                                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 28800, 3, false, false));
                                }
                            }
                            player.sendMessage(Text.keybind("translation.youthief.shout"), false);
                        }
                    }
                }
            }
        }
    }
}
