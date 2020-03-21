package de.coronavirus.application.dtos.service;

import de.coronavirus.domain.model.Infected;

import java.util.List;

public interface JobDTO extends EntityDTO {

    long getId();
    String getName();
    List<Infected> getInfected();
}
