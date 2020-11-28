package artifacts.common.item.trinket;

import artifacts.Artifacts;
import artifacts.client.render.model.trinket.CloudInABottleModel;
import artifacts.mixin.extensions.LivingEntityExtensions;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class CloudInABottleItem extends TrinketArtifactItem {

    private static final Identifier TEXTURE = new Identifier(Artifacts.MODID, "textures/entity/trinket/cloud_in_a_bottle.png");
    public static final Identifier C2S_DOUBLE_JUMPED_ID = new Identifier(Artifacts.MODID, "c2s_double_jumped");
    private Object model;

    public CloudInABottleItem() {
        super(new Settings());
        ServerSidePacketRegistry.INSTANCE.register(CloudInABottleItem.C2S_DOUBLE_JUMPED_ID, CloudInABottleItem::handleDoubleJumpPacket);
    }

    private static void handleDoubleJumpPacket(PacketContext context, PacketByteBuf clientPassedData) {
        context.getTaskQueue().execute(() -> {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            ((LivingEntityExtensions) player).artifacts$doubleJump();

            // This part is server-side only
            for (int i = 0; i < 20; ++i) {
                double motionX = player.getRandom().nextGaussian() * 0.02;
                double motionY = player.getRandom().nextGaussian() * 0.02 + 0.20;
                double motionZ = player.getRandom().nextGaussian() * 0.02;
                player.getServerWorld().spawnParticles(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), 1, motionX, motionY, motionZ, 0.15);
            }
        });
    }

    @Override
    protected SoundEvent getEquipSound() {
        return SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH;
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected CloudInABottleModel getModel() {
        if (model == null) {
            model = new CloudInABottleModel();
        }
        return (CloudInABottleModel) model;
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        return group.equals(SlotGroups.LEGS) && slot.equals(Slots.BELT);
    }
}
