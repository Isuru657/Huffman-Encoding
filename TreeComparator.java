import java.util.Comparator;

/**
 * Tree comparator: used to implement a priority queue when building the Tree that uses Huffman nodes
 * @author Isuru Abeysekara
 *
 */

public class TreeComparator implements Comparator<HuffmanNode> {

    /**
     *
     * @param n1: The first Huffman node
     * @param n2: The second Huffman node
     * @return: An integer result based on the comparison of the two nodes
     */
    @Override
    public int compare(HuffmanNode n1, HuffmanNode n2){
        if (n1.getFreq()<n2.getFreq()){
            return -1;
        }
        else if (n1.getFreq()==n2.getFreq()){
            return 0;
        }
        else{
            return 1;
        }
    }
}

