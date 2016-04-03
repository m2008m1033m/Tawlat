package com.tawlat.models.serializables;

import java.io.Serializable;
import java.util.Date;

public class AvailabilitySearchRequest implements Serializable {
    public boolean isAllLocations;
    public boolean isAllCuisines;
    public String dow;
    public String tod;
    public String lid;
    public String cid;
    public int people;
    public Date date;
    public String cityId;
}
