package gtna.networks.p2p;

import gtna.graph.Edges;
import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.graph.NodeImpl;
import gtna.networks.Network;
import gtna.networks.NetworkImpl;
import gtna.networks.model.DeBruijn;
import gtna.routing.RoutingAlgorithm;
import gtna.transformation.Transformation;
import gtna.util.Config;
import gtna.util.Timer;

import java.util.HashMap;
import java.util.Random;

/**
 * Implements a network generator for the P2P overlay of ODRI, the Optimal
 * Diameter Routing Infrastructure, which is based on DeBruijn graph. It was
 * described in the paper"Graph-theoretic analysis of structured peer-to-peer systems: routing distances and fault resilience"
 * by Loguinov et al. in 2003.
 * 
 * The parameters are the base and dimensions of the underlying DeBruijn graph
 * as well as the length of the random walk used to obtain a better load
 * distribution amongst all nodes in the network.
 * 
 * @author benni
 * 
 */
public class ODRI extends NetworkImpl implements Network {
	private int BASE;

	private int DIMENSIONS;

	private int WALK_LENGTH;

	public static ODRI[] get(int[] n, int b, int d, int wl,
			RoutingAlgorithm ra, Transformation[] t) {
		ODRI[] nw = new ODRI[n.length];
		for (int i = 0; i < n.length; i++) {
			nw[i] = new ODRI(n[i], b, d, wl, ra, t);
		}
		return nw;
	}

	public static ODRI[] get(int n, int b, int d, int[] wl,
			RoutingAlgorithm ra, Transformation[] t) {
		ODRI[] nw = new ODRI[wl.length];
		for (int i = 0; i < wl.length; i++) {
			nw[i] = new ODRI(n, b, d, wl[i], ra, t);
		}
		return nw;
	}

	public ODRI(int nodes, int BASE, int DIMENSIONS, int WALK_LENGTH,
			RoutingAlgorithm ra, Transformation[] t) {
		super("ODRI", nodes,
				new String[] { "BASE", "DIMENSIONS", "WALK_LENGTH" },
				new String[] { "" + BASE, "" + DIMENSIONS, "" + WALK_LENGTH },
				ra, t);
		this.DIMENSIONS = DIMENSIONS;
		this.BASE = BASE;
		this.WALK_LENGTH = WALK_LENGTH;
	}

	public Graph generate() {
		Timer timer = new Timer();
		Random rand = new Random(System.currentTimeMillis());

		int numberOfDBNodes = DeBruijn
				.numberOfNodes(this.BASE, this.DIMENSIONS);
		DBNode[] dbNodes = new DBNode[numberOfDBNodes];
		for (int i = 0; i < dbNodes.length; i++) {
			dbNodes[i] = new DBNode(i);
		}
		Edges dbEdges = new Edges(dbNodes, dbNodes.length * this.BASE);
		for (int i = 0; i < dbNodes.length; i++) {
			int shiftedId = (i * this.BASE) % dbNodes.length;
			for (int j = 0; j < this.BASE; j++) {
				if (i != shiftedId) {
					dbEdges.add(dbNodes[i], dbNodes[shiftedId]);
				}
				shiftedId++;
			}
		}
		dbEdges.fill();

		ODRINode[] nodes = new ODRINode[this.nodes()];
		nodes[0] = new ODRINode(0, dbNodes);
		for (int i = 1; i < nodes.length; i++) {
			while (true) {
				ODRINode bootstrap = nodes[rand.nextInt(i)];
				if (i > 100) {
					bootstrap = this.getBootstrap(rand, nodes, i);
				}
				if (bootstrap.dbNodes.length >= 2) {
					DBNode[] handOver = bootstrap.handOver();
					nodes[i] = new ODRINode(i, handOver);
					// int width = bootstrap.end - bootstrap.start;
					// if ((width % 2) == 0) {
					// int start = bootstrap.start + width / 2;
					// int end = bootstrap.end;
					// bootstrap.end = start - 1;
					// nodes[i] = new ODRINode(i, start, end);
					// } else {
					// int start = bootstrap.start + (width + 1) / 2;
					// int end = bootstrap.end;
					// bootstrap.end = start - 1;
					// nodes[i] = new ODRINode(i, start, end);
					// }
					break;
				}
			}
		}

		Edges edges = new Edges(nodes, dbNodes.length * this.BASE);
		for (int i = 0; i < dbNodes.length; i++) {
			Node[] out = dbNodes[i].out();
			for (int j = 0; j < out.length; j++) {
				edges.add(dbNodes[i].odri, ((DBNode) out[j]).odri);
			}
		}
		edges.fill();

		timer.end();
		Graph g = new Graph(this.description(), nodes, timer);
		return g;
	}

	private ODRINode getBootstrap(Random rand, ODRINode[] nodes, int index) {
		int k = Config.getInt("ODRI_SAMPLES");
		ODRINode bootstrap = nodes[rand.nextInt(index)];
		return getMax(bootstrap, this.WALK_LENGTH, k);
	}

	private ODRINode getMax(ODRINode node, int d, int k) {
		if (d == 0) {
			return node;
		}
		ODRINode max = node;
		HashMap<Integer, ODRINode> seen = new HashMap<Integer, ODRINode>();
		seen.put(node.index(), node);
		for (int i = 0; i < node.dbNodes.length; i++) {
			Node[] out = node.dbNodes[i].out();
			for (int j = 0; j < out.length; j++) {
				ODRINode dest = ((DBNode) out[j]).odri;
				if (!seen.containsKey(dest.index())) {
					dest = getMax(dest, d - 1, k);
					seen.put(dest.index(), dest);
					if (dest.dbNodes.length > max.dbNodes.length) {
						max = dest;
					}
				}
				if (seen.size() >= k) {
					return max;
				}
			}
		}
		return max;
	}

	private class ODRINode extends NodeImpl {
		DBNode[] dbNodes;

		private ODRINode(int index, DBNode[] dbNodes) {
			super(index);
			this.dbNodes = dbNodes;
			this.update();
		}

		private DBNode[] handOver() {
			DBNode[] handOver = new DBNode[this.dbNodes.length / 2];
			DBNode[] temp = new DBNode[this.dbNodes.length - handOver.length];
			for (int i = 0; i < handOver.length; i++) {
				handOver[i] = this.dbNodes[i];
			}
			for (int i = 0; i < temp.length; i++) {
				temp[i] = this.dbNodes[handOver.length + i];
			}
			this.dbNodes = temp;
			this.update();
			return handOver;
		}

		private void update() {
			for (int i = 0; i < this.dbNodes.length; i++) {
				this.dbNodes[i].odri = this;
			}
		}
	}

	private class DBNode extends NodeImpl {
		private ODRINode odri;

		private DBNode(int index) {
			super(index);
		}
	}
}
