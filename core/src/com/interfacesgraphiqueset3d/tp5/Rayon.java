package com.interfacesgraphiqueset3d.tp5;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector3;

public class Rayon implements Serializable {
    public final Vector3 origin = new Vector3();
    public final Vector3 direction = new Vector3();

    public Rayon() {

    }

    public Rayon(Vector3 origin, Vector3 direction) {
        this.origin.set(origin);
        this.direction.set(direction).nor();
    }

    public Rayon cpu() {
        return new Rayon(this.origin, this.direction);
    }

    public Vector3 getEndPoint(final Vector3 out, final float distance) {
        return out.set(direction).scl(distance).add(origin);
    }

    static Vector3 tmp = new Vector3();

    public String toString() {
        return "rayon [" + origin + ":" + direction + "]";
    }

    public Rayon set(Vector3 origin, Vector3 direction) {
        this.origin.set(origin);
        this.direction.set(direction).nor();
        return this;
    }

    public Rayon set(float x, float y, float z, float dx, float dy, float dz) {
        this.origin.set(x, y, z);
        this.direction.set(dx, dy, dz).nor();
        return this;
    }
}
