package bits.thicket;

import java.util.*;


/**
 * @author decamp
 */
public class WattsStrogatzGenerator {
    
    public static Graph generate( Random rand, int dim, int vertNo, int edgesPerVert, double fractionRandEdges ) {
        if( rand == null ) {
            rand = new Random();
        }
        
        List<Vert> verts = new ArrayList<Vert>( vertNo );
        
        // Creating a regular ring lattice
        for (int i = 0; i < vertNo; i++ ) {
            Vert v = new Vert();
            verts.add( v );
            
            double ang = i * 2.0 * Math.PI / vertNo;
            v.mX = (float)( 20.0 * Math.cos( ang ) );
            v.mY = (float)( 20.0 * Math.sin( ang ) );
        }
        
        for( int i = 0; i < vertNo; i++ ) {
            Vert va = verts.get( i ); 
            
            for( int j = 1; j <= edgesPerVert / 2; j++ ) {
                Vert vb;
                
                if( rand.nextDouble() >= fractionRandEdges ) {
                    vb = verts.get( ( i + j ) % vertNo );
                } else {
                    vb = verts.get( rand.nextInt( vertNo ) );
                }
                
                while( va == vb || va.isConnectedTo( vb ) ) {
                    vb = verts.get( rand.nextInt( vertNo ) );
                }
                
                Edge edge = new Edge( va, vb, 1f );
                edge.mA.addEdge( edge );
                edge.mB.addEdge( edge );
            }
        }
        
        return new Graph( verts );
    }

}
