package gtna.networks.model;

import gtna.graph.Edges;
import gtna.graph.Graph;
import gtna.graph.NodeImpl;
import gtna.networks.Network;
import gtna.networks.NetworkImpl;
import gtna.routing.RoutingAlgorithm;
import gtna.transformation.Transformation;
import gtna.util.Timer;
import gtna.util.Util;

import java.util.ArrayList;
import java.util.Random;

/**
 * Network generator for generating fully connected sub-graphs (communities)
 * with a specified number of edges between them. The array sizes gives the size
 * of each of the sizes.length communities. The double[][] p gives the
 * probability of an edge existing between two nodes in each community where
 * p[i][j] is the probability of an edge from community i to community j
 * (entries in p[i][i] are ignored). If the flag bidirectional is true, for
 * every edge the backwards edge is created as well. Please note that it might
 * lead to duplicate entries if p[i][j] != 0 && p[j][i] != 0 && bidirectional ==
 * true. Therefore, only fill one part of the matrix to avoid such problems.
 * 
 * The flag LESS_MEMORY can be set to choose a generation procedure that uses
 * less memory but requires more computation time.
 * 
 * The parameter "order" can be used to change the position of the nodes in the
 * main node array of the generated graph object. If it is set to SORTED_ORDER,
 * the main array contains first all nodes from the first community, then the
 * nodes from the second community, and so on. In case the flag is true, the
 * first node is assigned to the first community, the second node to the second
 * and so on. If the communities do not have the same size, the smaller
 * communities are simply discarded after they received all their nodes. E.g.,
 * sizes = {2, 3} would lead to the communities with node indices {0, 2} {1, 3,
 * 4}. sizes = {3, 1, 3} would result in the node assignments {0, 3, 5} {1} {2,
 * 4, 6}. If order is set to RANDOM_ORDER, the nodes are assigned to the
 * communities at random.
 * 
 * @author benni
 * 
 */
public class Communities3 extends NetworkImpl implements Network {
	private int[] sizes;

	private double[][] p;

	private int order;

	private boolean bidirectional;

	private NodeImpl[][] communities;

	public static final boolean LESS_MEMORY = false;

	public static final int SORTED_ORDER = 1;

	public static final int ALTERNATING_ORDER = 2;

	public static final int RANDOM_ORDER = 3;

	public Communities3(int[] sizes, double[][] p, int order,
			boolean bidirectional, RoutingAlgorithm r, Transformation[] t) {
		super("COMMUNITIES3", Util.sum(sizes), new String[] {
				"COMMUNITY_SIZES", "INTER_COMMUNITY_LINKS", "ORDER",
				"BIDIRECTIONAL" }, new String[] { Util.toFolderString(sizes),
				Util.toFolderString(p), "" + order, "" + bidirectional }, r, t);
		this.sizes = sizes;
		this.p = p;
		this.order = order;
		this.bidirectional = bidirectional;
	}

	public NodeImpl[][] getCommunities() {
		return this.communities;
	}

	public Graph generate() {
		Timer timer = new Timer();
		NodeImpl[] nodes = NodeImpl.init(this.nodes());
		Random rand = new Random(System.currentTimeMillis());
		// init communities
		this.communities = new NodeImpl[this.sizes.length][];
		for (int i = 0; i < this.communities.length; i++) {
			this.communities[i] = new NodeImpl[this.sizes[i]];
		}
		// fill communities depending on order
		if (this.order == SORTED_ORDER) {
			int index = 0;
			for (int i = 0; i < this.communities.length; i++) {
				for (int j = 0; j < this.communities[i].length; j++) {
					this.communities[i][j] = nodes[index++];
				}
			}
		} else if (this.order == ALTERNATING_ORDER) {
			int[] indices = new int[this.communities.length];
			for (int i = 0; i < nodes.length;) {
				for (int j = 0; j < this.communities.length; j++) {
					if (indices[j] < this.communities[j].length) {
						this.communities[j][indices[j]++] = nodes[i++];
					}
					if (i >= nodes.length) {
						break;
					}
				}
			}
		} else if (this.order == RANDOM_ORDER) {
			ArrayList<NodeImpl> list = new ArrayList<NodeImpl>(nodes.length);
			for (int i = 0; i < nodes.length; i++) {
				list.add(nodes[i]);
			}
			for (int i = 0; i < this.communities.length; i++) {
				for (int j = 0; j < this.communities[i].length; j++) {
					int index = rand.nextInt(list.size());
					this.communities[i][j] = list.get(index);
					list.remove(index);
				}
			}
		}
		for (int i = 0; i < this.communities.length; i++) {
			for (int j = 0; j < this.communities[i].length; j++) {
				System.out.println(i + " => " + this.communities[i][j].index()
						+ " @ " + this.communities[i].length);
			}
		}
		// create inter-community links
		Edges edges = new Edges(nodes, 0);
		for (int i = 0; i < this.p.length; i++) {
			for (int j = 0; j < this.p[i].length; j++) {
				if (i == j) {
					continue;
				}
				NodeImpl[] from = this.communities[i];
				NodeImpl[] to = this.communities[j];
				this
						.addInterCommunityEdges(from, to, this.p[i][j], rand,
								edges);
			}
		}
		edges.fill();
		// create internal links
		for (int i = 0; i < this.communities.length; i++) {
			for (int j = 0; j < this.communities[i].length; j++) {
				NodeImpl n = this.communities[i][j];
				NodeImpl[] inOld = n.in();
				NodeImpl[] outOld = n.out();
				NodeImpl[] inNew = new NodeImpl[inOld.length
						+ this.communities[i].length - 1];
				NodeImpl[] outNew = new NodeImpl[outOld.length
						+ this.communities[i].length - 1];
				int inIndex = 0;
				int outIndex = 0;
				for (int k = 0; k < inOld.length; k++) {
					inNew[inIndex++] = inOld[k];
				}
				for (int k = 0; k < outOld.length; k++) {
					outNew[outIndex++] = outOld[k];
				}
				for (int k = 0; k < this.communities[i].length; k++) {
					if (j != k) {
						inNew[inIndex++] = this.communities[i][k];
						outNew[outIndex++] = this.communities[i][k];
					}
				}
				n.init(inNew, outNew);
			}
		}

		timer.end();
		return new Graph(this.description(), nodes, timer);
	}

	private void addInterCommunityEdges(NodeImpl[] from, NodeImpl[] to,
			double p, Random rand, Edges edges) {
		for (int i = 0; i < from.length; i++) {
			for (int j = 0; j < to.length; j++) {
				if (rand.nextDouble() <= p) {
					if (LESS_MEMORY) {
						from[i].addOut(to[j]);
						to[j].addIn(from[i]);
						if (this.bidirectional) {
							from[i].addIn(to[j]);
							to[j].addOut(from[i]);
						}
					} else {
						edges.add(from[i], to[j]);
						if (this.bidirectional) {
							edges.add(to[j], from[i]);
						}
					}
				}
			}
		}
	}
}
