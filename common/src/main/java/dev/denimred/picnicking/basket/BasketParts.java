package dev.denimred.picnicking.basket;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BasketParts {
    private final BasketEntity basket;
    public final Handles handles = new Handles();
    public final Lid frontLid = new Lid();
    public final Lid backLid = new Lid();

    public BasketParts(BasketEntity basket) {
        this.basket = basket;
    }

    public void openLidFor(Entity player) {
        // One of the lids is already opened; don't bother
        if (frontLid.isOpen() || backLid.isOpen()) return;
        float basketView = basket.getViewYRot(1.0f);
        float playerView = player.getViewYRot(1.0f);
        float difference = Mth.wrapDegrees(basketView - playerView - 90.0f);
        (difference > 0.0f ? frontLid : backLid).open();
    }

    public void closeLids() {
        frontLid.close();
        backLid.close();
    }

    public void tick() {
        handles.animate();
        frontLid.animate();
        backLid.animate();
    }

    protected static float approachAngle(float angle, float targetAngle, float speedFactor, float minStep) {
        float step = Math.max(Math.abs((targetAngle - angle) * speedFactor), minStep);
        if (angle < targetAngle) {
            angle += step;
            if (angle > targetAngle) return targetAngle;
        } else {
            angle -= step;
            if (angle < targetAngle) return targetAngle;
        }
        return angle;
    }

    public class Lid {
        // Minimum lid angle when lid is closed
        protected static final float CLOSED_ANGLE = (float) Math.toRadians(0);
        // Maximum lid angle when lid is open and handles are down
        protected static final float OPEN_ANGLE = (float) Math.toRadians(135);
        // Maximum lid angle when lid is open and handles are up
        protected static final float OPEN_ANGLE_HANDLES = (float) Math.toRadians(45);
        // The angle of the handles (last is stored for smoothing based on framerate)
        protected float angle = CLOSED_ANGLE;
        protected float angleLast = angle;
        // Used to determine the direction the lid animation should move
        protected boolean open = false;

        public void open() {
            setOpen(true);
        }

        public void close() {
            setOpen(false);
        }

        public void setOpen(boolean open) {
            if (open == this.open) return;
            this.open = open;
            basket.syncParts();
        }

        public boolean isOpen() {
            return open;
        }

        public float getAngle(float delta) {
            return Mth.lerp(delta, angleLast, angle);
        }

        public void animate() {
            angleLast = angle;
            float targetAngle = open ? (handles.isUp() ? OPEN_ANGLE_HANDLES : OPEN_ANGLE) : CLOSED_ANGLE;
            if (angle != targetAngle)
                angle = approachAngle(angle, targetAngle, handles.isUp() ? 0.5f : 0.3f, 0.1f);
        }
    }

    public class Handles {
        // NBT tag key for storing the handlesUp state
        public static final String UP_TAG = "HandlesUp";
        // Maximum handles angle when handles are down
        protected static final float DOWN_ANGLE = (float) Math.toRadians(-118.5);
        // Maximum handles angle when handles are up
        protected static final float UP_ANGLE = (float) Math.toRadians(8);
        // The angle of the handles (last is stored for smoothing based on framerate)
        protected float angle = UP_ANGLE;
        protected float angleLast = angle;
        // Used to determine the direction the handles animation should move
        protected boolean up = true;

        public void flip() {
            setUp(!up);
        }

        public void setUp(boolean up) {
            if (up == this.up) return;
            this.up = up;
            basket.syncParts();
        }

        public boolean isUp() {
            return up;
        }

        public float getAngle(float delta) {
            return Mth.lerp(delta, angleLast, angle);
        }

        protected void animate() {
            angleLast = angle;
            float targetAngle = up ? UP_ANGLE : DOWN_ANGLE;
            if (angle != targetAngle) angle = approachAngle(angle, targetAngle, 0.3f, 0.1f);
        }
    }
}
