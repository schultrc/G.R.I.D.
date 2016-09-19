package edu.ucdenver.cse.GRID.MAP;


public class GRIDnodeWeightTime {
    private Double nodeEmissions;
    private Double nodeWtTotal;
    private Long nodeTmTotal;

    public void setNodeEmissions(Double inEmissions){ this.nodeEmissions = inEmissions; }
    public void setNodeWtTotal(Double inWeight){ this.nodeWtTotal = inWeight; }
    public void setNodeTmTotal(Long inTime){ this.nodeTmTotal = inTime; }

    public Double getNodeEmissions(){ return this.nodeEmissions; }
    public Double getNodeWtTotal(){ return this.nodeWtTotal; }
    public Long getNodeTmTotal(){ return this.nodeTmTotal; }
}

