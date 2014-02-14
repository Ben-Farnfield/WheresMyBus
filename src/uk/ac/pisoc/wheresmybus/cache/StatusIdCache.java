package uk.ac.pisoc.wheresmybus.cache;

public class StatusIdCache {

    private int index = 0;
    private int lastIndex;
    private long[] statusIDs;

    public StatusIdCache( int numIDs ) {
        statusIDs = new long[numIDs];
        lastIndex = numIDs - 1;
    }

    public void add( long statusID ) {
        statusIDs[index] = statusID;
        nextIndex();
    }

    public boolean contains( long testID ) {
        for ( int i=0; i<=lastIndex; i++ ) {
            if ( statusIDs[i] == testID ) return true;
        }
        return false;
    }

    private void nextIndex() {
        if ( index == lastIndex ) {
            index = 0;
        } else {
            index++;
        }
    }
}
