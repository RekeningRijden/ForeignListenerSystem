/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration.domain;

/**
 *
 * @author maikel
 */
public class DeleteStolenRequest {
    private String carIdentifier;

    public DeleteStolenRequest() {
    }

    public String getCarIdentifier() {
        return carIdentifier;
    }

    public void setCarIdentifier(String carIdentifier) {
        this.carIdentifier = carIdentifier;
    }
}
