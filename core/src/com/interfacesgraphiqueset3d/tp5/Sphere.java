package com.interfacesgraphiqueset3d.tp5;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.NumberUtils;

public class Sphere implements Serializable {
    /** the radius of the sphere **/
    public float radius;
    /** the center of the sphere **/
    public final Vector3 center;

    /**
     * Constructs a sphere with the given center and radius
     * 
     * @param center The center
     * @param radius The radius
     */
    public Sphere(Vector3 center, float radius) {
        this.center = new Vector3(center);
        this.radius = radius;
    }

    /**
     * @param sphere the other sphere
     * @return whether this and the other sphere overlap
     */
    public boolean overlaps(Sphere sphere) {
        return center.dst2(sphere.center) < (radius + sphere.radius) * (radius + sphere.radius);
    }

    @Override
    public int hashCode() {
        final int prime = 71;
        int result = 1;
        result = prime * result + this.center.hashCode();
        result = prime * result + NumberUtils.floatToRawIntBits(this.radius);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || o.getClass() != this.getClass())
            return false;
        Sphere s = (Sphere) o;
        return this.radius == s.radius && this.center.equals(s.center);
    }

}
