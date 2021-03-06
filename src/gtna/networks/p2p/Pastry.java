package gtna.networks.p2p;

import gtna.graph.Edge;
import gtna.graph.Edges;
import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.graph.NodeImpl;
import gtna.networks.Network;
import gtna.networks.NetworkImpl;
import gtna.routing.RoutingAlgorithm;
import gtna.transformation.Transformation;
import gtna.util.Timer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Implements a network generator for the P2P network Pastry, described by
 * Rowstron and Rowstron in their paper"Pastry: Scalable, distributed object location and routing for large-scale peer-to-peer systems"
 * (2001).
 * 
 * The parameters are size of the identifier space, base, and size of the
 * namespace set.
 * 
 * @author benni
 * 
 */
public class Pastry extends NetworkImpl implements Network {
	private int BITS_PER_KEY = 32;

	private int BASE = 4;

	private int NAMESPACE_SET_SIZE = -1;

	// private int NUMBER_OF_REPLICAS = -1;

	// private int NEIGHBORHOOD_SET_SIZE = -1;

	public static Pastry[] get(int[] n, int k, int b, RoutingAlgorithm ra,
			Transformation[] t) {
		Pastry[] nw = new Pastry[n.length];
		for (int i = 0; i < n.length; i++) {
			nw[i] = new Pastry(n[i], k, b, ra, t);
		}
		return nw;
	}

	public static Pastry[] get(int n, int k, int[] b, RoutingAlgorithm ra,
			Transformation[] t) {
		Pastry[] nw = new Pastry[b.length];
		for (int i = 0; i < b.length; i++) {
			nw[i] = new Pastry(n, k, b[i], ra, t);
		}
		return nw;
	}

	public static Pastry[][] get(int[] n, int k, int[] b, RoutingAlgorithm ra,
			Transformation[] t) {
		if (n.length >= b.length) {
			Pastry[][] nw = new Pastry[b.length][n.length];
			for (int i = 0; i < b.length; i++) {
				for (int j = 0; j < n.length; j++) {
					nw[i][j] = new Pastry(n[j], k, b[i], ra, t);
				}
			}
			return nw;
		} else {
			Pastry[][] nw = new Pastry[n.length][b.length];
			for (int i = 0; i < n.length; i++) {
				for (int j = 0; j < b.length; j++) {
					nw[i][j] = new Pastry(n[i], k, b[j], ra, t);
				}
			}
			return nw;
		}
	}

	public Pastry(int nodes, int BITS_PER_KEY, int BASE, RoutingAlgorithm ra,
			Transformation[] t) {
		super("PASTRY", nodes, new String[] { "BITS_PER_KEY", "BASE" },
				new String[] { "" + BITS_PER_KEY, "" + BASE }, ra, t);
		this.BITS_PER_KEY = BITS_PER_KEY;
		this.BASE = BASE;
		this.NAMESPACE_SET_SIZE = Math.min((int) Math.pow(2, BASE), this
				.nodes());
		// this.NUMBER_OF_REPLICAS = 3;
		// this.NEIGHBORHOOD_SET_SIZE = Math.min((int) Math.pow(2, BASE + 1),
		// this
		// .nodes());
	}

	public Graph generate() {
		Timer timer = new Timer();
		Random rand = new Random(System.currentTimeMillis());

		PastryNode[] nodes = new PastryNode[this.nodes()];
		PastryNode[] sortable = new PastryNode[this.nodes()];
		PastryNode[] sorted = new PastryNode[this.nodes()];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new PastryNode(randomIDUnique(rand, this.BITS_PER_KEY,
					nodes, i), randomLocation(rand), i, this);
			sortable[i] = nodes[i];
			sorted[i] = nodes[i];
		}
		sort(sorted);
		// for (int i = 0; i < nodes.length; i++) {
		// fillNeighborhoodSet(nodes[i], sortable);
		// }
		for (int i = 0; i < nodes.length; i++) {
			fillNamespaceSet(nodes[i], sortable, sorted);
		}
		for (int i = 0; i < nodes.length; i++) {
			fillRoutingTable(nodes[i], sortable);
		}

		Edges edges = new Edges(nodes, 100);
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes[i].namespaceSet.length; j++) {
				edges.add(nodes[i], nodes[i].namespaceSet[j]);
			}
			for (int j = 0; j < nodes[i].routingTable.length; j++) {
				for (int k = 0; k < nodes[i].routingTable[j].length; k++) {
					if (nodes[i].routingTable[j][k] != null) {
						edges.add(new Edge(nodes[i],
								nodes[i].routingTable[j][k]));
					}
				}
			}
		}
		edges.fill();

		timer.end();
		Graph graph = new Graph(this.description(), nodes, timer);
		return graph;
	}

	// private static void fillNeighborhoodSet(Node n, Node[] nodes) {
	// Arrays.sort(nodes, new LocationComparator(n));
	// for (int i = 0; i < n.neighborhoodSet.length && i < nodes.length - 1;
	// i++) {
	// n.neighborhoodSet[i] = nodes[i + 1];
	// }
	// }

	private static void fillNamespaceSet(PastryNode n, PastryNode[] sortable,
			PastryNode[] sorted) {
		int index = -1;
		for (int i = 0; i < sorted.length; i++) {
			if (n.index() == sorted[i].index()) {
				index = i;
				break;
			}
		}
		int start = Math.max(0, index - n.namespaceSet.length);
		int end = Math.min(sorted.length - 1, index + n.namespaceSet.length);
		PastryNode[] sub = sub(sorted, start, end);
		Arrays.sort(sub, new IDComparator(n));
		for (int i = 0; i < n.namespaceSet.length; i++) {
			n.namespaceSet[i] = sub[i + 1];
		}
	}

	private static void sort(PastryNode[] nodes) {
		PastryNode min = nodes[0];
		for (int i = 1; i < nodes.length; i++) {
			if (nodes[i].id < min.id) {
				min = nodes[i];
			}
		}
		Arrays.sort(nodes, new IDComparator(min));
	}

	private static PastryNode[] sub(PastryNode[] nodes, int start, int end) {
		PastryNode[] sub = new PastryNode[end - start + 1];
		for (int i = start; i <= end; i++) {
			sub[i - start] = nodes[i];
		}
		return sub;
	}

	private static void fillRoutingTable(PastryNode n, PastryNode[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			int level = n.level(nodes[i]);
			if (level != -1) {
				int domain = nodes[i].domains[level];
				if (n.routingTable[level][domain] == null) {
					n.routingTable[level][domain] = nodes[i];
				} else if (n.distance(nodes[i]) < n
						.distance(n.routingTable[level][domain])) {
					n.routingTable[level][domain] = nodes[i];
				}
			}
		}
	}

	private static boolean equals(boolean[] b1, boolean[] b2) {
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}

	private static boolean[] randomIDUnique(Random rand, int length,
			PastryNode[] nodes, int currentIndex) {
		boolean[] id = randomID(rand, length);
		for (int i = 0; i < currentIndex; i++) {
			if (equals(id, nodes[i].idBits)) {
				return randomIDUnique(rand, length, nodes, currentIndex);
			}
		}
		return id;
	}

	private static boolean[] randomID(Random rand, int length) {
		boolean[] id = new boolean[length];
		for (int i = 0; i < id.length; i++) {
			id[i] = rand.nextBoolean();
		}
		return id;
	}

	private static double[] randomLocation(Random rand) {
		return new double[] { rand.nextDouble(), rand.nextDouble() };
	}

	private static class PastryNode extends NodeImpl {
		private boolean[] idBits;

		private long id;

		private short[] domains;

		private double[] location;

		// private Node[] neighborhoodSet;

		private PastryNode[] namespaceSet;

		private PastryNode[][] routingTable;

		private PastryNode(boolean[] idBits, double[] location, int index,
				Pastry n) {
			super(index);
			this.location = location;
			// this.neighborhoodSet = new Node[n.NEIGHBORHOOD_SET_SIZE()];
			this.namespaceSet = new PastryNode[n.NAMESPACE_SET_SIZE];
			this.routingTable = new PastryNode[n.BITS_PER_KEY / n.BASE][(int) Math
					.pow(2, n.BASE)];
			this.idBits = idBits;
			this.id = 0;
			long pow = 1;
			for (int i = idBits.length - 1; i >= 0; i--) {
				if (idBits[i]) {
					this.id += pow;
				}
				pow *= 2;
			}
			this.domains = new short[n.BITS_PER_KEY / n.BASE];
			for (int i = 0; i < this.domains.length; i++) {
				int pow2 = 1;
				for (int j = n.BASE - 1; j >= 0; j--) {
					if (this.idBits[i * n.BASE + j]) {
						this.domains[i] += pow2;
					}
					pow2 *= 2;
				}
			}
		}

		private int level(PastryNode n) {
			for (int i = 0; i < this.domains.length; i++) {
				if (this.domains[i] != n.domains[i]) {
					return i;
				}
			}
			return -1;
		}

		private double distance(PastryNode n) {
			double x = Math.abs(this.location[0] - n.location[0]);
			double y = Math.abs(this.location[1] - n.location[1]);
			double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
			return distance;
		}

		public int route(Node n2) {
			return Short.MIN_VALUE;
		}
	}

	private static class IDComparator implements Comparator<PastryNode> {
		private PastryNode n;

		private IDComparator(PastryNode n) {
			this.n = n;
		}

		public int compare(PastryNode n1, PastryNode n2) {
			long d1 = Math.abs(this.n.id - n1.id);
			long d2 = Math.abs(this.n.id - n2.id);
			if (d1 < d2) {
				return -1;
			} else if (d1 == d2) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	// private static class LocationComparator implements Comparator<Node> {
	// private Node n;
	//
	// private LocationComparator(Node n) {
	// this.n = n;
	// }
	//
	// public int compare(Node n1, Node n2) {
	// double d1 = this.n.distance(n1);
	// double d2 = this.n.distance(n2);
	// if (d1 < d2) {
	// return -1;
	// } else if (d1 == d2) {
	// return 0;
	// } else {
	// return 1;
	// }
	// }
	// }
}
