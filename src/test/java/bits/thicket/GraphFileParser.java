/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.io.*;
import java.util.*;
import java.util.regex.*;


/**
 * @author decamp
 */
public class GraphFileParser {

    public static Graph parse( File file ) throws IOException {
        BufferedReader br = new BufferedReader( new FileReader( file ) );
        Pattern linePat = Pattern.compile( "^\\s*+\\d[\\s\\d]*+$" );
        Pattern numPat  = Pattern.compile( "\\d++" );
        
        List<Vert> verts = null;
        int vertNo = 0;
        
        // Find start of input.
        for( String k = br.readLine(); k != null; k = br.readLine() ) {
            if( !linePat.matcher( k ).find() ) {
                continue;
            }

            Matcher m = numPat.matcher( k );
            m.find();
            
            vertNo = Integer.parseInt( m.group( 0 ) );
            verts = new ArrayList<Vert>( vertNo );
            break;
        }
        
        for( int i = 0; i < vertNo; i++ ) {
            verts.add( new Vert() );
        }
        
        for( int i = 0; i < vertNo; i++ ) {
            Vert vi = verts.get( i );
            String k = br.readLine();
            
            Matcher m = numPat.matcher( k );
            while( m.find() ) {
                int j = Integer.parseInt( m.group( 0 ) ) - 1;
                if( j > i ) {
                    continue;
                }
                
                Vert vj = verts.get( j );
                Edge e = new Edge( vi, vj, 1f );
                vi.addEdge( e );
                vj.addEdge( e );
            }
        }
        
        br.close();
        return new Graph( verts );
    }

}
