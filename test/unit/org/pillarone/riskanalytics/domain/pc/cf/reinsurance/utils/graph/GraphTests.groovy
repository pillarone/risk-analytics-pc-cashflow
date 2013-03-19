package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.graph

class GraphTests extends GroovyTestCase {

    void testUsage() {
        Graph graph = new Graph()
        assertTrue 'adding whole account stoploss', graph.createNode('whole account stoploss', 'cat XL')
        assertFalse 'adding whole account stoploss (2)', graph.createNode('whole account stoploss', 'WXL 10 xs 10')
        assertTrue 'adding WXL 10 xs 10', graph.createNode('WXL 10 xs 10')
        assertTrue 'adding relation whole account stoploss (2)', graph.addRelation('whole account stoploss', 'WXL 10 xs 10')
        assertFalse 'adding whole account stoploss (3)', graph.createNode('whole account stoploss', 'WXL 80 xs 20')
        assertTrue 'adding WXL 80 xs 20', graph.createNode('WXL 80 xs 20')
        assertTrue 'adding relation whole account stoploss (3)', graph.addRelation('whole account stoploss', 'WXL 80 xs 20')
        assertTrue 'adding WXL 80 xs 20', graph.createNode('quota share')
        assertTrue 'adding surplus', graph.createNode('surplus', 'quota share')
        assertTrue 'adding WXL 80 xs 20', graph.addRelation('WXL 10 xs 10', 'surplus')
        assertTrue 'adding WXL 80 xs 20', graph.addRelation('WXL 80 xs 20', 'surplus')
        assertTrue 'adding relation', graph.addRelation('cat XL', 'surplus')

        assertEquals 'number of nodes', 6, graph.getNodes().size()
        print(graph)

        Node wholeAccountStoploss = graph.getNode('whole account stoploss')
        assertEquals "$wholeAccountStoploss parent number", 3, wholeAccountStoploss.getParents().size()
        assertEquals "$wholeAccountStoploss ancestors number", 5, wholeAccountStoploss.getAncestors().size()

        Node catXL = graph.getNode('cat XL')
        assertEquals "$catXL parent number", 1, catXL.getParents().size()
        assertEquals "$catXL ancestors number", 2, catXL.getAncestors().size()

        Node quotaShare = graph.getNode('quota share')
        assertEquals "$quotaShare parent number", 0, quotaShare.getParents().size()
        assertEquals "$quotaShare ancestors number", 0, quotaShare.getAncestors().size()
    }

    void testCirclesNotPermitted() {
        Graph graph = new Graph()
        assertTrue 'adding whole account stoploss', graph.createNode('whole account stoploss', 'cat XL')
        assertFalse 'circular relations not allowed', graph.addRelation('cat XL', 'whole account stoploss')
    }

    void testUniqueNodeNames() {
        Graph graph = new Graph()
        assertTrue 'adding whole account stoploss', graph.createNode('whole account stoploss', 'cat XL')
        assertFalse 'adding whole account stoploss', graph.createNode('whole account stoploss')
        assertFalse 'adding cat XL', graph.createNode('cat XL')
        assertEquals 'number of nodes', 2, graph.getNodes().size()
    }

    private def print(Graph graph) {
        for (Node node: graph.getNodes()) {
            println "$node ancestors ${node.getAncestors()}"
            println "$node parents ${node.getParents()}"
        }
    }
}