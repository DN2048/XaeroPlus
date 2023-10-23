package xaeroplus.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.common.minimap.waypoints.render.WaypointFilterParams;
import xaero.common.minimap.waypoints.render.WaypointsIngameRenderer;
import xaero.common.settings.ModSettings;
import xaeroplus.mixin.client.mc.AccessorWorldRenderer;
import xaeroplus.settings.XaeroPlusSettingRegistry;
import xaeroplus.util.ColorHelper;
import xaeroplus.util.CustomWaypointsIngameRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer.BEAM_TEXTURE;
import static net.minecraft.world.World.NETHER;
import static xaeroplus.util.Shared.customDimensionId;

@Mixin(value = WaypointsIngameRenderer.class, remap = false)
public class MixinWaypointsIngameRenderer implements CustomWaypointsIngameRenderer {
    @Shadow private List<Waypoint> sortingList;
    @Shadow private WaypointFilterParams filterParams;
    List<Waypoint> beaconWaypoints = new ArrayList<>();
    final Predicate<Waypoint> beaconViewFilter = new Predicate<Waypoint>() {
        @Override
        public boolean test(final Waypoint w) {
            boolean deathpoints = filterParams.deathpoints;
            if (!w.isDisabled()
                    && w.getVisibilityType() != 2
                    && w.getVisibilityType() != 3
                    && (w.getWaypointType() != 1 && w.getWaypointType() != 2 || deathpoints)) {
                double offX = (double)w.getX(filterParams.dimDiv) - filterParams.cameraX + 0.5;
                double offZ = (double)w.getZ(filterParams.dimDiv) - filterParams.cameraZ + 0.5;
                double distanceScale = filterParams.dimensionScaleDistance ? MinecraftClient.getInstance().world.getDimension().coordinateScale() : 1.0;
                double unscaledDistance2D = Math.sqrt(offX * offX + offZ * offZ);
                double distance2D = unscaledDistance2D * distanceScale;
                double waypointsDistance = filterParams.waypointsDistance;
                double waypointsDistanceMin = filterParams.waypointsDistanceMin;
                return w.isOneoffDestination()
                        || (
                        w.getWaypointType() == 1
                                || w.isGlobal()
                                || w.isTemporary() && filterParams.temporaryWaypointsGlobal
                                || waypointsDistance == 0.0
                                || !(distance2D > waypointsDistance)
                )
                    && (waypointsDistanceMin == 0.0 || !(unscaledDistance2D < waypointsDistanceMin));
            } else {
                return false;
            }
        }
    };

    @Inject(method = "renderWaypointsIterator", at = @At("HEAD"))
    public void injectRenderWaypoints(final MatrixStack matrixStack, final MatrixStack matrixStackOverlay, final MinimapRendererHelper helper, final Iterator<Waypoint> iter, final double d3, final double d4, final double d5, final Entity entity, final BufferBuilder bufferbuilder, final Tessellator tessellator, final double dimDiv, final double actualEntityX, final double actualEntityY, final double actualEntityZ, final double smoothEntityY, final double fov, final int screenHeight, final float cameraAngleYaw, final float cameraAnglePitch, final Vector3f lookVector, final double clampDepth, final VertexConsumerProvider.Immediate renderTypeBuffer, final VertexConsumer waypointBackgroundConsumer, final TextRenderer fontrenderer, final Matrix4f waypointsProjection, final int screenWidth, final boolean detailedDisplayAllowed, final double minDistance, final String subworldName, final CallbackInfo ci) {
        beaconWaypoints = sortingList.stream().filter(beaconViewFilter).sorted().collect(Collectors.toList());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lxaero/common/minimap/waypoints/WaypointsManager;getDimensionDivision(Ljava/lang/String;)D"))
    public double redirectDimensionDivision(final WaypointsManager waypointsManager, final String worldContainerID) {
        if (worldContainerID != null && MinecraftClient.getInstance().world != null) {
            try {
                RegistryKey<World> dim = MinecraftClient.getInstance().world.getRegistryKey();
                if (!Objects.equals(dim, customDimensionId)) {
                    double currentDimDiv = Objects.equals(dim, NETHER) ? 8.0 : 1.0;
                    String dimPart = worldContainerID.substring(worldContainerID.lastIndexOf(47) + 1);
                    RegistryKey<World> dimKey = waypointsManager.getDimensionKeyForDirectoryName(dimPart);
                    double selectedDimDiv = dimKey == NETHER ? 8.0 : 1.0;
                    return currentDimDiv / selectedDimDiv;
                }
            } catch (final Exception e) {
                // fall through
            }
        }
        return waypointsManager.getDimensionDivision(worldContainerID);
    }

    @Override
    public void renderWaypointBeacons(final XaeroMinimapSession minimapSession, final MatrixStack matrixStack, final float tickDelta) {
        if (!XaeroPlusSettingRegistry.waypointBeacons.getValue()) return;
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        double dimDiv = redirectDimensionDivision(waypointsManager, waypointsManager.getCurrentContainerID());
        beaconWaypoints.forEach(w -> renderWaypointBeacon(w, dimDiv, tickDelta, matrixStack));
        beaconWaypoints.clear();
    }

    public void renderWaypointBeacon(final Waypoint waypoint, final double dimDiv, float tickDelta, MatrixStack matrixStack) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        final Vec3d playerVec = mc.player.getPos();
        Vec3d waypointVec = new Vec3d(waypoint.getX(dimDiv), playerVec.y, waypoint.getZ(dimDiv));
        final double xzDistance = playerVec.distanceTo(waypointVec);
        if (xzDistance < (int) XaeroPlusSettingRegistry.waypointBeaconDistanceMin.getValue()) return;
        final int farScale = (int) XaeroPlusSettingRegistry.waypointBeaconScaleMin.getValue();
        final double maxRenderDistance = Math.min(mc.options.getViewDistance().getValue() << 4, farScale == 0 ? Integer.MAX_VALUE : farScale << 4);
        if (xzDistance > maxRenderDistance) {
            final Vec3d delta = waypointVec.subtract(playerVec).normalize();
            waypointVec = playerVec.add(new Vec3d(delta.x * maxRenderDistance, delta.y * maxRenderDistance, delta.z * maxRenderDistance));
        }
        final EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
        final Camera camera = entityRenderDispatcher.camera;
        final Frustum frustum = ((AccessorWorldRenderer) mc.worldRenderer).getFrustum();
        if (camera == null || frustum == null) return;
        final double viewX = camera.getPos().getX();
        final double viewZ = camera.getPos().getZ();
        final double x = waypointVec.x - viewX;
        final double z = waypointVec.z - viewZ;
        final double y = -100;
        if (!frustum.isVisible(new Box(waypointVec.x-1, -100, waypointVec.z-1, waypointVec.x+1, 500, waypointVec.z+1))) return;
        final int color = ModSettings.COLORS[waypoint.getColor()];
        final VertexConsumerProvider.Immediate entityVertexConsumers = mc.getBufferBuilders().getEntityVertexConsumers();
        final long time = mc.world.getTime();
        matrixStack.push();
        matrixStack.translate(x, y, z);
        BeaconBlockEntityRenderer.renderBeam(matrixStack, entityVertexConsumers, BEAM_TEXTURE, tickDelta, 1.0f, time, 0, 355,
                                             ColorHelper.getColorRGBA(color), 0.2f, 0.25f);
        matrixStack.pop();
    }
}
