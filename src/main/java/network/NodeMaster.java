package network;

public class NodeMaster {

    private int numLayers;
    private int[] dimensions;
    private Node[][] nodes; //nodes[layer][node]

    public void init(int[] dimensions){
        numLayers = dimensions.length;
        this.dimensions = dimensions;

        //Initialize nodes
        nodes = new Node[numLayers][];
        for(int layer = 0; layer < numLayers; layer++){
            nodes[layer] = new Node[dimensions[layer]];
            for(int node = 0; node < dimensions[layer]; node++){
                nodes[layer][node] = new Node();
            }
        }
    }

    public void setLayer(int layer, float[] values){
        assert(values.length == nodes[layer].length);
        for(int node = 0; node < dimensions[layer]; node++){
            setValue(layer, node, values[node]);
        }
    }

    public void setValue(int layer, int node, float value){
        nodes[layer][node].setValue(value);
    }

    public float getValue(int layer, int node){
        return nodes[layer][node].getValue();
    }
}
