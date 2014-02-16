package uk.ac.pisoc.wheresmybus.cache;

public class StatusIdCache {

    private int index = 0;
    private int numIDs;
    private long[] statusIDs;

    public StatusIdCache( int numIDs ) {
        statusIDs = new long[numIDs];
        this.numIDs = numIDs;
    }

    public void add( long statusID ) {
        statusIDs[index] = statusID;
        nextIndex();
    }

    public boolean contains( long testID ) {
        for ( int i = 0; i < numIDs; i++ ) {
            if ( statusIDs[i] == testID ) return true;
        }
        return false;
    }

    private void nextIndex() {
        index = ( index + 1 ) % numIDs;
    }
}
