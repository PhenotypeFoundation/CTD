/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.model;

import java.io.Serializable;

/**
 *
 * @author kerkh010
 */
public class Protocol implements Serializable{

    private Integer id;
    private String name;
    private String description;

    public Protocol(){
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    
}
