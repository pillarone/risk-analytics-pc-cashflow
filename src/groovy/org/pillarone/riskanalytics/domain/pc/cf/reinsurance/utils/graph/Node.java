package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Node {

    private String name;
    /** distance 1 */
    private Set<Node> parents;

    public Node(String name) {
        this.name = name;
        parents = new HashSet<Node>();
    }

    public String getName() {
        return name;
    }

    public void addParent(Node node) {
        parents.add(node);
    }

    public Set<Node> getAncestors() {
        Set<Node> ancestors = new HashSet<Node>();
        for (Node parent : parents) {
            ancestors.add(parent);
            ancestors.addAll(parent.getAncestors());
        }
        return ancestors;
    }

    public boolean isAncestor(Node node) {
        Set<Node> ancestors = getAncestors();
        for (Node ancestor : ancestors) {
            if (node.equals(ancestor)) {
                return true;
            }
        }
        return false;
    }

    public Set<Node> getParents() {
        return parents;
    }

    @Override
    public String toString() {
        return getName();
    }
}
