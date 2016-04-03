package com.tawlat.models.serializables;

import java.io.Serializable;
import java.util.Date;

public class AdvancedSearchRequest implements Serializable {

    public String[] locationIds;
    public String[] cuisineIds;
    public String[] goodForIds;
    public String[] prices;
    public Date timeOfDay;
    public String cityId;
    public boolean isDirectoryIncluded;
    public String venueId = "0";
    public String dayOfWeek;
    public int people;


}
