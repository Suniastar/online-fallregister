package de.coronavirus.application.service;

import de.coronavirus.application.dtos.mapper.InfectedMapper;
import de.coronavirus.application.dtos.mapper.ToModelConverter;
import de.coronavirus.application.dtos.request.CreateInfectedRequest;
import de.coronavirus.application.dtos.request.UpdateInfectedRequest;
import de.coronavirus.application.dtos.service.DiagnosisDto;
import de.coronavirus.application.dtos.service.InfectedDto;
import de.coronavirus.domain.exception.NotFoundException;
import de.coronavirus.domain.infrastructure.repositories.InfectedRepository;
import de.coronavirus.domain.model.Accommodation;
import de.coronavirus.domain.model.Diagnosis;
import de.coronavirus.domain.model.Infected;
import de.coronavirus.domain.model.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class InfectedService {

    private final InfectedRepository infectedRepository;
    private final InfectedMapper infectedMapper;

    @Autowired
    public InfectedService(InfectedRepository infectedRepository, InfectedMapper infectedMapper) {
        this.infectedRepository = infectedRepository;
        this.infectedMapper = infectedMapper;
    }

    public List<InfectedDto> findAll() {
        return infectedRepository.findAllDtoBy();
    }

    public InfectedDto findInfected(long id) {
        return infectedRepository.findDtoById(id).orElseThrow(() -> new NotFoundException("Error: Infected Person not found!", "The infected Person could not been found!"));
    }

    public List<InfectedDto> saveAndFlush(InfectedDto ... infectedDTOS) {
        List<InfectedDto> resultInfected = new LinkedList<>();
        for(InfectedDto infectedDto : infectedDTOS) {
            resultInfected.add(infectedRepository.saveAndFlush(thisInfectedDto));
        }
        return resultInfected;
    }

    public InfectedDto createInfected(CreateInfectedRequest createRequest) {
        Infected infected = new Infected();
        Infected.Gender genderOfInfected =  ToModelConverter.stringToGender(createRequest.getGender());
        infected.setGender(genderOfInfected);
        infected.setFirstName(createRequest.getFirstName());
        infected.setLastName(createRequest.getLastName());
        infected.setDateOfBirth(createRequest.getDateOfBirth());
        infected.setJobInMedicalField(createRequest.isJobInMedicalField());
        infected.setJobInFoodFiled(createRequest.isJobInFoodField());
        infected.setJobInCommunityField(createRequest.isJobInCommunityField());

        if(createRequest.getPhoneNumbers() != null && !createRequest.getPhoneNumbers().isEmpty()) {
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            for(String number : createRequest.getPhoneNumbers()){
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setNumber(number);
                phoneNumbers.add(phoneNumber);
            }
            infected.setPhoneNumbers(phoneNumbers);
        }

        if(createRequest.getAccommodationName() != null && !createRequest.getAccommodationName().isEmpty()) {
            Accommodation accommodation = new Accommodation();
            accommodation.setName(createRequest.getAccommodationName());
            infected.setAccommodation(accommodation);
        }

        List<Diagnosis> diagnoses = new ArrayList<>();
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setDiagnosticResult(createRequest.getDiagnosisResult());
        diagnosis.setDate(createRequest.getDate());
        diagnoses.add(diagnosis);
        infected.setDiagnoses(diagnoses);

        if(createRequest.getDateOfIllness() != null) {
            infected.setDateOfIllness(createRequest.getDateOfIllness());
        }

        if(createRequest.getDateOfDeath() != null) {
            infected.setDateOfDeath(createRequest.getDateOfDeath());
        }

        if(!createRequest.getInfectionSource().isEmpty()) {
            infected.setInfectionSource(createRequest.getInfectionSource());
        }
        infected.setIntensiveCareTreatment(createRequest.isIntensiveCareTreatment());
        infectedRepository.saveAndFlush(infected);
        return infectedRepository.findDtoById(infected.getId()).orElseThrow(() -> new NotFoundException("Error: Infected person not found!","The infected Person could not been found!"));
    }

    public InfectedDto updateInfected(UpdateInfectedRequest updateRequest, long id) {
        return findInfected(id);
    }

    public void delete(long id) {
        // ToDo: Throw Exception if the infected with this id is not found in the database
        infectedRepository.deleteById(id);
    }
}
