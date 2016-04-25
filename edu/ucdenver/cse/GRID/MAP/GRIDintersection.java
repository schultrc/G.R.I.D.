package edu.ucdenver.cse.GRID.MAP;

public class GRIDintersection {
	private Long Id = 0L;
    private String name;

    public void setId(Long val){Id = val;}
    public Long getId(){return Id;}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Id == null) ? 0 : Id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GRIDintersection other = (GRIDintersection) obj;
        if (Id == null) {
            if (other.Id != null)
                return false;
        } else if (!Id.equals(other.Id))
            return false;
        return true;
    }

    @Override
    public String toString(){return name;}
}
