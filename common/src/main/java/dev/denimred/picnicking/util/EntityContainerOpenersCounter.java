package dev.denimred.picnicking.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class EntityContainerOpenersCounter<E extends Entity & Container> {
    public final E entity;
    private int openersCount;

    public EntityContainerOpenersCounter(E entity) {
        this.entity = entity;
    }

    protected abstract void onOpen(@Nullable Player player);

    protected abstract void onClose(@Nullable Player player);

    protected abstract void openerCountChanged(int oldCount, int newCount);

    protected boolean isOwnContainer(Player player) {
        return player.containerMenu instanceof SimpleContainerMenu menu && menu.container == entity;
    }

    public void incrementOpeners(Player player) {
        int oldCount = openersCount++;
        if (oldCount == 0) {
            onOpen(player);
            entity.gameEvent(GameEvent.CONTAINER_OPEN, player);
        }
        openerCountChanged(oldCount, openersCount);
    }

    public void decrementOpeners(Player player) {
        int oldCount = openersCount--;
        if (openersCount == 0) {
            onClose(player);
            entity.gameEvent(GameEvent.CONTAINER_CLOSE, player);
        }
        openerCountChanged(oldCount, openersCount);
    }

    private int countNearbyOpeners() {
        AABB area = entity.getBoundingBox().inflate(5.0);
        List<Player> players = entity.level.getEntitiesOfClass(Player.class, area, this::isOwnContainer);
        return players.size();
    }

    public void recheckOpeners() {
        int nearbyCount = countNearbyOpeners();
        int knownCount = openersCount;
        if (knownCount != nearbyCount) {
            boolean hasNearby = nearbyCount != 0;
            boolean hasKnown = knownCount != 0;
            if (hasNearby && !hasKnown) {
                onOpen(null);
                entity.gameEvent(GameEvent.CONTAINER_OPEN, (Entity) null);
            } else if (!hasNearby) {
                onClose(null);
                entity.gameEvent(GameEvent.CONTAINER_CLOSE, (Entity) null);
            }
            openersCount = nearbyCount;
        }
        openerCountChanged(knownCount, nearbyCount);
    }

    public int getOpenersCount() {
        return openersCount;
    }
}
