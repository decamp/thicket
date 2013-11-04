package bits.thicket;

import java.util.Random;

/**
 * @author decamp
 */
public class LayoutParams {

    /**
     * Specifies the dimensionality of the layout. 
     * Only 2 and 3 dimensional data are supported.
     */
    public int mDim = 2;
    
    /**
     * Determines scale of layout. This value roughly corresponds to the
     * "natural edge length": given a graph of two vertices with a single
     * edge between them, all of weight one, than the edge should have 
     * this length when the layout has converged.
     */
    public float mScale = 1f;
    
    /**
     * Specifies if multilevel layout should be used. Generally, using a 
     * multilevel layout will require more time, but produce a better result.
     */
    public boolean mMultilevel = true;
    
    /**
     * Specifies how vertices are weighted. By default, the 
     * weight field of the vertex, "v.mWeight", will be used.
     */
    public VertWeightModel mVertWeightModel = VertWeightModel.PREDEFINED;

    /**
     * Random Number Generator for whatever may require it.
     */
    public Random mRand = null;
    
    
    /**************************************************************************
     * Gravity Phase - Initializes graph and draws all nodes towards center.
     **************************************************************************/
    
    /**
     * Strength of gravitational attraction. Set <code>mGravityForce &leq; 0</code> to disable.
     */
    public float mGravityForce = 0.005f;
    
    /**
     * Center of gravitational attraction.
     */
    public final float[] mGravityCenter = { 0f, 0f, 0f };
       
    
    
    /**************************************************************************
     * Attract Phase - Applies attractive forces from edges.
     **************************************************************************/

    /**
     * This field can be set to override the entire attract phase of the solver.
     * If <code>null</code>, the solver will select an implementation based
     * on the attract parameters below.
     */
    public SolverPhase mAttractPhase = null;
    
    /**
     * Attract equation to use. Default is squared-distance.
     */
    public AttractEq mAttractEq = AttractEq.SQUARE_DIST;
    
    
    /**************************************************************************
     * Repulse Phase - Applies repulsive forces each pair of Verts.
     **************************************************************************/
    
    /**
     * This field can be set to override the entire repulse phase of the solver.
     * If <code>null</code>, the solver will determine an implementation based
     * on the repulse parameters below. 
     */
    public SolverPhase mRepulsePhase = null;
    
    /**
     * Repulsion equation to use. Default is inverse distance.
     */
    public RepulseEq mRepulseEq = RepulseEq.INV_LINEAR_DIST;
    
    /**
     * If set to true, a quadtree/octree/n-tree will be used to approximate
     * the force contributions from distant points ("Barnes-Hutt Approximation").
     * This reduces the complexity ofthe N-body problem from O(N^2) to O(N log N).
     */
    public boolean mRepulseApprox = true;
    
    /**
     * Threshold for using the Barnes-Hutt approximation for N-body repulsion calculations.
     */
    public float mRepulseApproxThresh = 1.2f;
    
    /**
     * Max depth for quadtree/octree/ntree. If negative, the max depth will be adjusted
     * dynamically (recommended). 
     */
    public int mRepulseApproxMaxTreeDepth = -1;
    
    
    /**************************************************************************
     * Update Phase - Updates position of each node.
     **************************************************************************/

    /**
     * The step length used at the start of a layout. This value is expressed in 
     * proportion to <code>mScale</code>.
     */
    public float mUpdateInitialStep = 0.5f;
    
    /**
     * Specifies how the step length is geometrically incremented: <br/> 
     * <code>0.0 &lt; mUpdateIncrementStep &lt; 1.0</code>
     */
    public float mUpdateIncrementStep = 0.9f;
    
    /**
     * Specifies how small the step size of the graph will become before the
     * layout is considered to be converged. More precise layouts should use
     * a lower value. Expressed in proportion to <code>mScale</code>. <br/>
     * <code> 0.0 &lt; mUpdateTol &lt; mUpdateInitStep </code>  
     */
    public float mUpdateTol = 0.005f;
    
    /**
     * If greater than 0, this value will be used in place of <code>mUpdateTol</code>
     * for layout of all graph layouts but the final one. This allows for the final
     * level of the graph to be laid out more or less precisely than the other levels.
     */
    public float mUpdateCoarseTol = -1f;
    
    
    /**
     * Used for multilevel layouts to determine which nodes to combine
     * to simplify graph.
     */
    CoarsenStrategy mCoarsenStrategy = CoarsenStrategy.MAX_EDGE;

    /**
     * How model is refined after uncoarsening. Results of layout are undefined
     * without refinement, so this should be enabled.
     */
    RefineFunc mRefine = RefineFunc.EXPAND_PERTURB;
    
}
