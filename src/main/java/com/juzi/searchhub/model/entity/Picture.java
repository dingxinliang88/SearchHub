package com.juzi.searchhub.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class Picture implements Serializable {

    private static final long serialVersionUID = 4895611014313543590L;

    private String title;
    private String url;
}
