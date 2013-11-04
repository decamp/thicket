package bits.thicket;

import java.util.Random;

/**
 * @author decamp
 */
interface RefineFunc {
    
    public void refine( LayoutParams params, Graph src, Graph dst );

    
    public static final RefineFunc NOTHING = new RefineFunc() {
        @Override
        public void refine( LayoutParams params, Graph src, Graph dst ) {
            for( Vert v = dst.mVerts; v != null; v = v.mGraphNext ) {
                v.mGraphOwner = null;
            }
        }
    };
    
    
    public static final RefineFunc EXPAND_PERTURB = new RefineFunc() {
        @Override
        public void refine( LayoutParams params, Graph src, Graph dst ) {
            final int dim     = params.mDim;
            final Random rand = ( params.mRand != null ? params.mRand : new Random() );
            float expandScale = (float)( Math.pow( dst.mVertNo, 1.0 / dim ) / Math.pow( src.mVertNo, 1.0 / dim ) );
            if( expandScale != expandScale ) {
                expandScale = 1f;
            }
            
            float noiseScale  = params.mScale * 0.001f;
            float noiseTrans  = -noiseScale * 0.5f; 
            
            if( dim == 2 ) {
                for( Vert v = dst.mVerts; v != null; v = v.mGraphNext ) {
                    if( v.mGraphOwner == null ) {
                        v.mX *= expandScale;
                        v.mY *= expandScale;
                    } else {
                        Vert u = v.mGraphOwner;
                        v.mGraphOwner = null;
                        v.mX = expandScale * u.mX + noiseScale * rand.nextFloat() + noiseTrans;
                        v.mY = expandScale * u.mY + noiseScale * rand.nextFloat() + noiseTrans;
                    }
                }
            } else {
                for( Vert v = dst.mVerts; v != null; v = v.mGraphNext ) {
                    if( v.mGraphOwner == null ) {
                        v.mX *= expandScale;
                        v.mY *= expandScale;
                        v.mZ *= expandScale;
                    } else {
                        Vert u = v.mGraphOwner;
                        v.mGraphOwner = null;
                        v.mX = expandScale * u.mX + noiseScale * rand.nextFloat() + noiseTrans;
                        v.mY = expandScale * u.mY + noiseScale * rand.nextFloat() + noiseTrans;
                        v.mZ = expandScale * u.mZ + noiseScale * rand.nextFloat() + noiseTrans;
                    }
                }
            }
        }
    };

}
