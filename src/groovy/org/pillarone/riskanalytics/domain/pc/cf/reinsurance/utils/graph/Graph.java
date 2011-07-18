package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Graph {

    private List<Node> nodes;

    public Graph() {
        nodes = new ArrayList<Node>();
    }

    public boolean createNode(String name) {
        return createNode(name, null);
    }

    public boolean createNode(String name, String parentName) {
        if (uniqueNodeName(name)) {
            Node node = new Node(name);
            nodes.add(node);
            if (parentName != null) {
                Node parentNode = getNode(parentName);
                if (parentNode == null) {
                    parentNode = new Node(parentName);
                    nodes.add(parentNode);
                }
                node.addParent(parentNode);
            }
            return true;
        }
        return false;
    }

    public boolean addRelation(String name, String parentName) {
        Node parentNode = getNode(parentName);
        Node node = getNode(name);
        if (node != null && parentNode != null && !parentNode.isAncestor(node)) {
            node.addParent(parentNode);
            return true;
        }
        else {
            return false;
        }
    }

    private Node getNode(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    private boolean uniqueNodeName(String name) {
        return getNode(name) == null;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
