package gtna.metrics.motifs;

import gtna.graph.Graph;
import gtna.metrics.roles.GeneralGraphMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 * @deprecated
 */
public class MotifFunctions {

	public static ArrayList<ArrayList<Integer>> combinations(int node,
			int motifType, Graph g) {
		// check the motif type
		if (motifType != 3 && motifType != 4) {
			System.out
					.println("ERROR: Motif.MotifFunctions.permutations invalid motifType: "
							+ motifType);
			return null;
		} else {
			if (motifType == 3) {
				ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
				ArrayList<Integer> combi;
				HashSet<Integer> done = new HashSet<Integer>();
				done.add(node);

				HashSet<Integer> level_one = GeneralGraphMethods
						.neighborsOfNode(node, g);
				Iterator<Integer> iter = level_one.iterator();
				while (iter.hasNext()) {
					if (iter.next() <= node) {
						iter.remove();
					}
				}

				for (Integer level1 : level_one) {
					done.add(level1);
					HashSet<Integer> level_two = GeneralGraphMethods
							.neighborsOfNode(level1, g);
					for (Integer l1 : level_one) {
						if (!done.contains(l1)) {
							combi = new ArrayList<Integer>();
							combi.add(l1);
							combi.add(level1);
							combi.add(node);
							combinations.add(combi);
						}
					}
					for (Integer l2 : level_two) {
						if (l2 > node && !level_one.contains(l2)) {
							combi = new ArrayList<Integer>();
							combi.add(l2);
							combi.add(level1);
							combi.add(node);
							combinations.add(combi);
						}
					}

				}
				return combinations;
			} else {
				ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
				ArrayList<Integer> combi;
				HashSet<Integer> done = new HashSet<Integer>();
				done.add(node);

				HashSet<Integer> level_one = GeneralGraphMethods
						.neighborsOfNode(node, g);
				Iterator<Integer> iter = level_one.iterator();
				while (iter.hasNext()) {
					if (iter.next() <= node) {
						iter.remove();
					}
				}

				for (Integer level1 : level_one) {
					HashSet<Integer> done2 = new HashSet<Integer>();
					done.add(level1);
					HashSet<Integer> level_two = GeneralGraphMethods
							.neighborsOfNode(level1, g);
					iter = level_two.iterator();
					while (iter.hasNext()) {
						Integer next = iter.next();
						if (next <= node || level_one.contains(next)) {
							iter.remove();
						}
					}
					for (Integer l1 : level_one) {
						done2.add(l1);
						if (!done.contains(l1)) {
							for (Integer ll1 : level_one) {
								if (!done2.contains(ll1) && !done.contains(ll1)) {
									combi = new ArrayList<Integer>();
									combi.add(ll1);
									combi.add(l1);
									combi.add(level1);
									combi.add(node);
									combinations.add(combi);
								}
							}

							for (Integer l2 : level_two) {
								combi = new ArrayList<Integer>();
								combi.add(l2);
								combi.add(l1);
								combi.add(level1);
								combi.add(node);
								combinations.add(combi);
							}
							HashSet<Integer> level_two2 = GeneralGraphMethods
									.neighborsOfNode(l1, g);
							for (Integer l2 : level_two2) {
								if (l2 > node && !level_one.contains(l2)
										&& !level_two.contains(l2)) {
									combi = new ArrayList<Integer>();
									combi.add(l2);
									combi.add(l1);
									combi.add(level1);
									combi.add(node);
									combinations.add(combi);
								}
							}

						}
					}
					done2 = new HashSet<Integer>();
					for (Integer l2 : level_two) {
						HashSet<Integer> level_three = GeneralGraphMethods
								.neighborsOfNode(l2, g);
						done2.add(l2);
						for (Integer ll2 : level_two) {
							if (!done2.contains(ll2)
									&& !level_one.contains(ll2)) {
								combi = new ArrayList<Integer>();
								combi.add(l2);
								combi.add(ll2);
								combi.add(level1);
								combi.add(node);
								combinations.add(combi);
							}
						}

						for (Integer l3 : level_three) {
							if (l3 > node && !level_one.contains(l3)
									&& !level_two.contains(l3)) {
								combi = new ArrayList<Integer>();
								combi.add(l3);
								combi.add(l2);
								combi.add(level1);
								combi.add(node);
								combinations.add(combi);
							}

						}
					}

				}
				return combinations;
			}
		}
	}

	// debug print method
	public static void outprintMotifs(ArrayList<Integer>[] motifs, int[] motif) {
		int numberOfMotifs = 0;
		for (int i = 0; i < motifs.length; i++) {
			numberOfMotifs += motifs[i].size();
			System.out.println("Node: " + i + " :: " + motifs[i].size()
					+ " :: " + motifs[i].toString());
		}
		double percent;
		for (int i = 0; i < motif.length; i++) {
			percent = (double) motif[i]
					/ ((double) numberOfMotifs / (double) 100);
			System.out.println("Motif " + (i + 1) + ": " + percent);
		}
		System.out.println("Number of Motifs: " + numberOfMotifs / 4 + "/"
				+ numberOfMotifs);
	}
}
