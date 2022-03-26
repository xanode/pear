package toxcore.dht;

import java.util.ArrayList;

public class ClientList {

    private final int size;
    private final Node baseNode;
    private ArrayList<Node> clientList;

    ClientList(final int size, final Node baseNode) {
        this.size = size; // Should be set to 32
        this.baseNode = baseNode;
        this.clientList = new ArrayList<>();
    }

    /**
     * Add a given node in the client list.
     * @param node Node to add in the list.
     * @return True if it has been inserted in the list, false otherwise.
     */
    protected boolean add(Node node) {
        // Check if list is full
        if (this.clientList.size() >= this.size) {
            return false;
        }
        for (int i=0; i<this.clientList.size(); i++) {
            if (DHT.getClosest(this.baseNode, this.clientList.get(i), node)) {
                this.clientList.set(i, node);
                return true;
            }
        }
        this.clientList.add(node);
        return true;
    }

    /**
     * Remove a given node from the client list.
     * @param node Node to remove.
     * @return True if it has been removed, false otherwise.
     */
    protected boolean remove(Node node) {
        return this.clientList.remove(node);
    }
}
