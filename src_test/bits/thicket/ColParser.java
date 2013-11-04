package bits.thicket;

import java.io.*;
import java.util.*;
import java.util.regex.*;


/**
 * @author decamp
 */
public class ColParser {

    
    public static Graph parse( File file ) throws IOException {
        return parse( file, 2 );
    }

    
    public static Graph parse( File file, int dim ) throws IOException {
        BufferedReader br = new BufferedReader( new FileReader( file ) );
        Pattern edgePat   = Pattern.compile( "(\\d++) (\\d++)" );
        Pattern numPat    = Pattern.compile( "[-+]?[0-9\\.]++" );
        Pattern coordPat  = Pattern.compile( "(\\d++) ([\\d\\.]++) ([\\d\\.]++)" );
        
        Map<Integer,Vert> map = new HashMap<Integer,Vert>();
        Graph ret = new Graph();
        
        for( String k = br.readLine(); k != null; k = br.readLine() ) {
            if( k.length() == 0 ) {
                continue;
            }
            
            if( k.charAt( 0 ) == 'x' ) {
                Matcher m = coordPat.matcher( k );
                if( !m.find() ) {
                    continue;
                }
                
                Integer ind = Integer.parseInt( m.group( 1 ) );
                Vert vert = map.get( ind );
                
                if( vert == null ) {
                    vert = new Vert();
                    map.put( ind, vert );
                    ret.addVert( vert );
                }
                
                vert.mX = Float.parseFloat( m.group( 2 ) );
                vert.mY = Float.parseFloat( m.group( 3 ) );
                //vert.mZ = 0f;
               
            } else if( k.charAt( 0 ) == 'e' ) {
                Matcher m = numPat.matcher( k );
                                
                Integer aInd;
                Integer bInd;
                float weight = 1f;
                
                if( !m.find() ) {
                    continue;
                }
                aInd = Integer.parseInt( m.group( 0 ) );
                
                if( !m.find() ) {
                    continue;
                }
                bInd = Integer.parseInt( m.group( 0 ) );
                
                if( aInd.intValue() == bInd.intValue() ) {
                    continue;
                }
                
                if( m.find() ) {
                    weight = Float.parseFloat( m.group( 0 ) );
                }
                
                Vert aVert = map.get( aInd );
                if( aVert == null ) {
                    aVert = new Vert();
                    map.put( aInd, aVert );
                    ret.addVert( aVert );
                }
                Vert bVert = map.get( bInd );
                if( bVert == null ) {
                    bVert = new Vert();
                    map.put( bInd, bVert );
                    ret.addVert( bVert );
                }

                Edge e = new Edge( aVert, bVert, weight );
                aVert.addEdge( e );
                bVert.addEdge( e );
                ret.addEdge( e );
            }
        }
        
        br.close();
        return ret;   
    }

}
