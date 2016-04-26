package edu.ucdenver.cse.GRID.MAP;

public class GRIDintersection {
	private String Id = "";
	private double x;
	private double y;

	
	
    public GRIDintersection(String id, double x, double y) {
		super();
		Id = id;
		this.x = x;
		this.y = y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getId(){return Id;}

	@Override
	public String toString() {
		return "GRIDintersection [Id=" + Id + ", x=" + x + ", y=" + y + "]";
	}
}
