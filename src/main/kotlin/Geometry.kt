import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

object Geometry {

    fun cartesianToSpherical(xyz : Vector3D) : Vector3D {
        val unitNorm = xyz.normalize()
        return Vector3D(xyz.norm,
                        Math.acos(unitNorm.y),
                        Math.atan2(unitNorm.z,unitNorm.x))
    }

    fun erectToFlacid(spherical : Vector3D) : Vector3D {
        return Vector3D(spherical.x,
                        -spherical.y,
                        spherical.z )
    }

    fun sphericalToCartesian(spherical : Vector3D) : Vector3D {
        return Vector3D(
                spherical.x * Math.sin(spherical.y) * Math.cos(spherical.z),
                spherical.x * Math.cos(spherical.y),
                spherical.x * Math.sin(spherical.y) * Math.sin(spherical.z)
        )
    }
}