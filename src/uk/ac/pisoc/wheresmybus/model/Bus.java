package uk.ac.pisoc.wheresmybus.model;

public class Bus {

    private String number;
    private String time;

    public void setNumber( String number ) { this.number = number; }
    public String getNumber( ) { return number; }

    public void setTime( String time ) { this.time = time; }
    public String getTime( ) { return time; }

    @Override
    public String toString( ) {
        return "Bus [number=" + number + ", time=" + time + "]";
    }
}
