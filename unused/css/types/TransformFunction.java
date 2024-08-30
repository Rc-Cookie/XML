package de.rccookie.css.types;

import de.rccookie.math.constFloat3x3;
import de.rccookie.math.constFloat4x4;
import de.rccookie.math.float3x3;
import de.rccookie.math.float4x4;
import org.jetbrains.annotations.Nullable;

public interface TransformFunction {

    constFloat4x4 getMatrix();

    default constFloat3x3 get2DMatrix() {
        constFloat4x4 mat = getMatrix();
        return new float3x3(mat.a00(), mat.a01(), mat.a03(),
                            mat.a10(), mat.a11(), mat.a13(),
                            0,0,0);
    }

    interface Matrix extends TransformFunction {
        boolean is2D();
    }

    interface Rotate extends Matrix {

        double getAxisX();

        double getAxisY();

        double getAxisZ();

        Angle getAngle();
    }

    interface Perspective extends TransformFunction {
        @Nullable
        Length getZ();

        @Override
        default constFloat4x4 getMatrix() {
            Length z = getZ();
            if(z == null) return constFloat4x4.zero;
            return new float4x4(1, 0, 0, 0,
                                0, 1, 0, 0,
                                0, 0, 1, 0,
                                0, 0, (float) (1 / z.getValue()), 1);
        }
    }

    interface Scale extends Matrix {

        double getX();

        double getY();

        double getZ();

        @Override
        default constFloat4x4 getMatrix() {
            return new float4x4((float) getX(), 0, 0, 0,
                                0, (float) getY(), 0, 0,
                                0, 0, (float) getZ(), 0,
                                0, 0, 0, 1);
        }
    }
}
