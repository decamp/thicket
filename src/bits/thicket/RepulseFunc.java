package bits.thicket;


/**
 * @author decamp
 */
interface RepulseFunc {
    
    void init( LayoutParams params, Graph graph );
    
    void applyCellForce( QuadtreeCell cell, Vert vert );
        
    void appleVertForce( Vert v, Vert target );
    
    
    static final float EPS = 0x0.000002P-60f;
    static final float REPULSE_FACTOR = 0.2f;
    

    static final class InvLog2 implements RepulseFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = -REPULSE_FACTOR * params.mScale;
        }
        
        public void applyCellForce( QuadtreeCell cell, Vert target ) {
            float dx  = cell.mX - target.mX;
            float dy  = cell.mY - target.mY;
            double dd = Math.sqrt( dx * dx + dy * dy );
            float mag = mCoeff * cell.mWeight / ( (float)( dd * Math.log( 1.0 + dd ) ) + EPS );
            
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
        }
        
        public void appleVertForce( Vert v, Vert target ) {
            float dx  = v.mX - target.mX;
            float dy  = v.mY - target.mY;
            double dd = Math.sqrt( dx * dx + dy * dy );
            float mag = mCoeff * v.mWeight / ( (float)( dd * Math.log( 1.0 + dd ) ) + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
        }
    }
    
    
    static final class InvLog3 implements RepulseFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = -REPULSE_FACTOR * params.mScale;
        }
        
        public void applyCellForce( QuadtreeCell cell, Vert target ) {
            float dx  = cell.mX - target.mX;
            float dy  = cell.mY - target.mY;
            float dz  = cell.mZ - target.mZ;
            double dd = Math.sqrt( dx * dx + dy * dy + dz * dz );
            float mag = mCoeff * cell.mWeight / ( (float)( dd * Math.log( 1.0 + dd ) ) + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
            target.mForceZ += mag * dz;
        }
        
        public void appleVertForce( Vert v, Vert target ) {
            float dx  = v.mX - target.mX;
            float dy  = v.mY - target.mY;
            float dz  = v.mZ - target.mZ;
            double dd = Math.sqrt( dx * dx + dy * dy + dz * dz );
            float mag = mCoeff * v.mWeight / ( (float)( dd * Math.log( 1.0 + dd ) ) + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
            target.mForceZ += mag * dz;
        }
    }
    
    
    static final class InvLinear2 implements RepulseFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = -REPULSE_FACTOR * params.mScale;
        }
        
        public void applyCellForce( QuadtreeCell cell, Vert target ) {
            float dx  = cell.mX - target.mX;
            float dy  = cell.mY - target.mY;
            float mag = mCoeff * cell.mWeight / ( dx * dx + dy * dy + EPS );
            
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
        }
        
        public void appleVertForce( Vert v, Vert target ) {
            float dx  = v.mX - target.mX;
            float dy  = v.mY - target.mY;
            float mag = mCoeff * v.mWeight / ( dx * dx + dy * dy + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
        }
    }
    
    
    static final class InvLinear3 implements RepulseFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = -REPULSE_FACTOR * params.mScale;
        }
        
        public void applyCellForce( QuadtreeCell cell, Vert target ) {
            float dx  = cell.mX - target.mX;
            float dy  = cell.mY - target.mY;
            float dz  = cell.mZ - target.mZ;
            float mag = mCoeff * cell.mWeight / ( (float)Math.sqrt( dx * dx + dy * dy + dz * dz ) + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
            target.mForceZ += mag * dz;
        }
        
        public void appleVertForce( Vert v, Vert target ) {
            float dx  = v.mX - target.mX;
            float dy  = v.mY - target.mY;
            float dz  = v.mZ - target.mZ;
            float mag = mCoeff * v.mWeight / ( (float)Math.sqrt( dx * dx + dy * dy + dz * dz ) + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
            target.mForceZ += mag * dz;
        }
    }
    
    
    static final class InvSquare2 implements RepulseFunc { 
        
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = -REPULSE_FACTOR * params.mScale * params.mScale;
        }
        
        public void applyCellForce( QuadtreeCell cell, Vert target  ) {
            float dx  = cell.mX - target.mX;
            float dy  = cell.mY - target.mY;
            float dd  = dx * dx + dy * dy;
            float mag = mCoeff * cell.mWeight / ( (float)Math.sqrt( dd ) * dd + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
        }
        
        public void appleVertForce( Vert v, Vert target ) {
            float dx  = v.mX - target.mX;
            float dy  = v.mY - target.mY;
            float dd  = dx * dx + dy * dy;
            float mag = mCoeff * v.mWeight / ( (float)Math.sqrt( dd ) * dd + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
        }
    }
    
    
    static final class InvSquare3 implements RepulseFunc { 
        private float mCoeff;
        
        public void init( LayoutParams params, Graph graph ) {
            mCoeff = -REPULSE_FACTOR * params.mScale * params.mScale;
        }
        
        public void applyCellForce( QuadtreeCell cell, Vert target ) {
            float dx  = cell.mX - target.mX;
            float dy  = cell.mY - target.mY;
            float dz  = cell.mZ - target.mZ;
            float dd  = dx * dx + dy * dy + dz * dz;
            float mag = mCoeff * cell.mWeight / ( (float)Math.sqrt( dd ) * dd + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
            target.mForceZ += mag * dz;
        }
        
        public void appleVertForce( Vert v, Vert target ) {
            float dx  = v.mX - target.mX;
            float dy  = v.mY - target.mY;
            float dz  = v.mZ - target.mZ;
            float dd  = dx * dx + dy * dy + dz * dz;
            float mag = mCoeff * v.mWeight / ( (float)Math.sqrt( dd ) * dd + EPS );
            target.mForceX += mag * dx;
            target.mForceY += mag * dy;
            target.mForceZ += mag * dz;
        }
    }
    
}

