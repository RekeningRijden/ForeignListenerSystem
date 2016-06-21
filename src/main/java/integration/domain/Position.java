/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration.domain;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author maikel
 */
public class Position implements Serializable{
    
    private Date date;
    private Double longitude;
    private Double latitude;

    public Position() {
    }

    public Position(Date date, Double longitude, Double latitude) {
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    
    
}
