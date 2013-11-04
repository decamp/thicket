package bits.thicket;

/**
 * @author decamp
 */
public interface Quadtree {
    
    /**
     * @return number of dimensions of this tree.
     */
    public int dims();
    
    /**
     * Clears tree, initializes bounds of tree, and populates tree with a
     * list of Verts.
     * 
     * @param vertList  List of verts to populate tree with.
     * @param maxDepth  Max depth of tree cells to allow. Setting a reasonable max depth is important to
     *                  ensure that two Verts that share the same position do not generate too many
     *                  or even infinite cells from being generated in an attempt to separate the points.
     * @param optCenter The center of the root cell position in the form [ x0, y0, ... ]
     *                  If <code>optCenter == null</code>, the center will be inferred from the vert list.
     * @param optSize   The size of the root cell.
     *                  If <code>optCenter == null</code>, the size of the root cell will be inferred from
     *                  the vert list. 
     */
    public void rebuild( Vert vertList, int maxDepth, float[] optCenter, float optSize );
    
    /**
     * Clears tree and initializes its dimensions.
     * 
     * @param maxDepth Max depth of tree cells to allow. Setting a reasonable max depth is important to
     *                 ensure that two Verts that share the same position do not generate too many
     *                 or even infinite cells from being generated in an attempt to separate the points.
     * @param center   The center of the root cell position in the form [ x0, y0, ... ]
     * @param size     The size of the root cell
     * 
     */
    public void init( int maxDepth, float[] center, float size );
    
    /**
     * Inserts single Vert into tree. Either <code>init()</code> or <code>rebuild()</code>
     * should be called to establish the size of the tree before <code>insert()</code> is 
     * called. 
     * 
     * @param vert  Vert to insert. 
     */
    public void insert( Vert vert );
    
    /**
     * @return the root cell of this Quadtree.
     */
    public QuadtreeCell root();

    
    public QuadtreeCell childFor( QuadtreeCell cell, Vert vert );
    
    
    public int childIndexFor( QuadtreeCell cell, Vert vert );
    
    
}
