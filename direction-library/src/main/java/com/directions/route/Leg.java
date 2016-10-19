package com.directions.route;

import java.util.List;

/**
 * Created by Pyxis on 2016. 10. 20..
 */
public class Leg {
    private List<Segment> segmentList;

    public List<Segment> getSegmentList() {
        return segmentList;
    }

    public void setSegmentList(List<Segment> segmentList) {
        this.segmentList = segmentList;
    }
}
