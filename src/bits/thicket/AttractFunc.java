package bits.thicket;

import cogmac.math3d.Tol;

/**
 * @author decamp
 */
interface AttractFunc {
    
    void init( LayoutParams params, Graph graph );
    
    void apply( Edge e );

    

    static final class Log2 implements AttractFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = 1f / params.mScale;
        }
        
        public void apply( Edge e ) {
            Vert a    = e.mA;
            Vert b    = e.mB;
            float dx  = a.mX - b.mX;
            float dy  = a.mY - b.mY;
            double dd = Math.sqrt( dx * dx + dy * dy );
            float mag = (float)( e.mWeight * mCoeff * Math.log( 1.0 + dd ) / ( dd + Tol.FSQRT_ABS_ERR ) );
            
            a.mForceX -= mag * dx;
            a.mForceY -= mag * dy;
            b.mForceX += mag * dx;
            b.mForceY += mag * dy;
        }
    }
    
    
    static final class Log3 implements AttractFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = 1f / params.mScale;
        }
        
        public void apply( Edge e ) {
            Vert a    = e.mA;
            Vert b    = e.mB;
            float dx  = a.mX - b.mX;
            float dy  = a.mY - b.mY;
            float dz  = a.mZ - b.mZ;
            double dd = Math.sqrt( dx * dx + dy * dy + dz * dz );
            float mag = (float)( e.mWeight * mCoeff * Math.log( 1.0 + dd ) / ( dd + Tol.FSQRT_ABS_ERR ) ); 
            
            a.mForceX -= mag * dx;
            a.mForceY -= mag * dy;
            a.mForceZ -= mag * dz;
            b.mForceX += mag * dx;
            b.mForceY += mag * dy;
            b.mForceZ += mag * dz;
        }
    }
    
    
    static final class Linear2 implements AttractFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = 1f / params.mScale;
        }
        
        public void apply( Edge e ) {
            Vert a    = e.mA;
            Vert b    = e.mB;
            float dx  = a.mX - b.mX;
            float dy  = a.mY - b.mY;
            final float mag = e.mWeight * mCoeff; 
            
            a.mForceX -= mag * dx;
            a.mForceY -= mag * dy;
            b.mForceX += mag * dx;
            b.mForceY += mag * dy;
        }
    }
    
    
    static final class Linear3 implements AttractFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = 1f / params.mScale;
        }
        
        public void apply( Edge e ) {
            Vert a    = e.mA;
            Vert b    = e.mB;
            float dx  = a.mX - b.mX;
            float dy  = a.mY - b.mY;
            float dz  = a.mZ - b.mZ;
            float mag = e.mWeight * mCoeff;
            
            a.mForceX -= mag * dx;
            a.mForceY -= mag * dy;
            a.mForceZ -= mag * dz;
            b.mForceX += mag * dx;
            b.mForceY += mag * dy;
            b.mForceZ += mag * dz;
        }
    }

    
    static final class Square2 implements AttractFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = 1f / ( params.mScale * params.mScale );
        }
        
        public void apply( Edge e ) {
            Vert a    = e.mA;
            Vert b    = e.mB;
            float dx  = a.mX - b.mX;
            float dy  = a.mY - b.mY;
            final float mag = e.mWeight * mCoeff * (float)Math.sqrt( dx * dx + dy * dy );
            
            a.mForceX -= mag * dx;
            a.mForceY -= mag * dy;
            b.mForceX += mag * dx;
            b.mForceY += mag * dy;
        }
    }
    
    
    static final class Square3 implements AttractFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = 1f / ( params.mScale * params.mScale );
        }
        
        public void apply( Edge e ) {
            Vert a    = e.mA;
            Vert b    = e.mB;
            float dx  = a.mX - b.mX;
            float dy  = a.mY - b.mY;
            float dz  = a.mZ - b.mZ;
            float mag = e.mWeight * mCoeff * (float)Math.sqrt( dx * dx + dy * dy + dz * dz );
            
            a.mForceX -= mag * dx;
            a.mForceY -= mag * dy;
            a.mForceZ -= mag * dz;
            b.mForceX += mag * dx;
            b.mForceY += mag * dy;
            b.mForceZ += mag * dz;
        }
    }
    
        
}
