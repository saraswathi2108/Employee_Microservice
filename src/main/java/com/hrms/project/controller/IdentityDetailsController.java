package com.hrms.project.controller;

import com.hrms.project.entity.*;
import com.hrms.project.dto.*;
import com.hrms.project.handlers.UnauthorizedAccessException;
import com.hrms.project.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")

public class IdentityDetailsController {

    @Autowired
    private AadhaarServiceImpl aadhaarServiceImpl;

    @Autowired
    private PanServiceImpl panServiceImpl;

    @Autowired
    private DrivingLicenseServiceImpl drivingLicenseServiceImpl;

    @Autowired
    private PassportDetailsServiceImpl passportDetailsImpl;

    @Autowired
    private DegreeServiceImpl degreeServiceImpl;


    @Autowired
    private VoterIdServiceImpl  voterIdServiceImpl;

    @Autowired
    private WorkExperienceServiceImpl workExperienceServiceImpl;


    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PostMapping("/aadhaar/{employeeId}")
    public ResponseEntity<AadhaarCardDetails> save(@PathVariable String employeeId,
                                                   @RequestPart(value="aadhaarImage",required = true) MultipartFile aadhaarImage,
                                                   @Valid @RequestPart(value="aadhaar") AadhaarDTO aadhaarCardDetails)
            throws IOException {

        return new ResponseEntity<>(aadhaarServiceImpl.createAadhaar(employeeId,aadhaarImage,aadhaarCardDetails), HttpStatus.CREATED);
    }


    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{employeeId}/aadhaar")
    public ResponseEntity<AadhaarDTO>getAadhaar(@PathVariable String employeeId){
        AadhaarDTO aadhaarDTO=aadhaarServiceImpl.getAadhaarByEmployeeId(employeeId);
        return new ResponseEntity<>(aadhaarDTO, HttpStatus.OK);

    }
    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{employeeId}/aadhaar")
    public ResponseEntity<AadhaarCardDetails>updateAadhaar(@PathVariable String employeeId,
                                                           @RequestPart(value="aadhaarImage", required = false) MultipartFile aadhaarImage,
                                                           @Valid @RequestPart(value="aadhaar") AadhaarDTO aadhaarDTO) throws IOException{
        AadhaarCardDetails dto = aadhaarServiceImpl.updateAadhaar(employeeId,aadhaarImage,aadhaarDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{employeeId}/aadhaar")
    public ResponseEntity<AadhaarCardDetails>deleteAadhaar(@PathVariable String employeeId){
        AadhaarCardDetails aadhaarCardDetails=aadhaarServiceImpl.deleteAadharByEmployeeId(employeeId);
        return new ResponseEntity<>(aadhaarCardDetails,HttpStatus.OK);
    }


    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PostMapping("/{employeeId}/pan")
    public ResponseEntity<PanDetails> savePan(
            @PathVariable String employeeId,
            @RequestPart("panImage") MultipartFile panImage,
            @Valid @RequestPart(value = "panDetails") PanDTO panDTO) throws IOException {

        return new ResponseEntity<>(panServiceImpl.createPan(employeeId,panImage,panDTO),HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{employeeId}/pan")
    public ResponseEntity<PanDTO> getPanDetails(@PathVariable String employeeId) {

        return new ResponseEntity<>(panServiceImpl.getPanDetails(employeeId), HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{employeeId}/pan")
    public ResponseEntity<PanDetails> updatePanDetails(@PathVariable String employeeId,
                                                       @RequestPart(value="panImage", required = false) MultipartFile panImage,
                                                       @Valid  @RequestPart PanDTO panDTO) throws IOException {
        return new ResponseEntity<>(panServiceImpl.UpdatePanDetails(employeeId, panImage, panDTO), HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{employeeId}/pan")
    public ResponseEntity<PanDetails>deletePanDetails(@PathVariable String employeeId){
        PanDetails panDetails= panServiceImpl.deletePanByEmployeeId(employeeId);
        return new ResponseEntity<>(panDetails,HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PostMapping("/driving/license/{employeeId}")
    public ResponseEntity<DrivingLicense> saveLicense(@PathVariable String employeeId,
                                                      @RequestPart(value="licenseImage") MultipartFile licenseImage,
                                                      @Valid @RequestPart(value="drivingLicense") DrivingLicenseDTO drivingLicenseDTO) throws IOException {
        return new ResponseEntity<>(drivingLicenseServiceImpl.createDrivingLicense(employeeId, licenseImage, drivingLicenseDTO), HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{employeeId}/driving")
    public ResponseEntity<DrivingLicense> updateDrivingLicense(@PathVariable String employeeId,
                                                               @RequestPart("licenseImage") MultipartFile licenseImage,
                                                               @Valid   @RequestPart(value="drivingLicense") DrivingLicenseDTO drivingLicenseDTO) throws IOException {
        return new ResponseEntity<>(drivingLicenseServiceImpl.updatedrivingDetails(employeeId,licenseImage,drivingLicenseDTO),HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{employeeId}/driving")

    public ResponseEntity<DrivingLicenseDTO> getDrivingLicense(@PathVariable String employeeId){

        return new ResponseEntity<>(drivingLicenseServiceImpl.getDrivingDetails(employeeId),HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{employeeId}/driving")
    public  ResponseEntity<DrivingLicense>deleteDriving(@PathVariable String employeeId){
        return new ResponseEntity<>(drivingLicenseServiceImpl.deleteByEmployeeId(employeeId),HttpStatus.OK);
    }


    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PostMapping("/passport/details/{employeeId}")
    public ResponseEntity<PassportDetails> savePassportDetails(@PathVariable String employeeId,
                                                               @RequestPart("passportImage") MultipartFile passportImage,
                                                               @Valid  @RequestPart(value="passportDetails") PassportDetailsDTO passportDetailsDTO) throws IOException {
        return new ResponseEntity<>(passportDetailsImpl.createPassport(employeeId,passportImage,passportDetailsDTO),HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{employeeId}/passport")
    public ResponseEntity<PassportDetailsDTO> getPassportDetails(@PathVariable String employeeId){
        return  new ResponseEntity<>(passportDetailsImpl.getPassportDetails(employeeId),HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{employeeId}/passport")
    public ResponseEntity<PassportDetails> updatePassportDetails(@PathVariable String employeeId,
                                                                 @RequestPart("passportImage") MultipartFile passportImage,
                                                                 @Valid   @RequestPart("passportDetails") PassportDetailsDTO passportDetailsDTO) throws IOException {
        return new ResponseEntity<>(passportDetailsImpl.updatePasswordDetails(employeeId,passportImage,passportDetailsDTO),HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{employeeId}/passport")
    public ResponseEntity<PassportDetails>deletePassport(@PathVariable String employeeId){
        return new ResponseEntity<>(passportDetailsImpl.deleteByEmployeeId(employeeId),HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PostMapping("/{employeeId}/voter")
    public ResponseEntity<VoterDetails>addVoter(@PathVariable String employeeId,
                                                @RequestPart("voterImage") MultipartFile voterImage,
                                                @Valid  @RequestPart(value="voterDetails") VoterDTO voterDTO)throws IOException {
        VoterDetails voter=voterIdServiceImpl.createVoter(employeeId,voterImage,voterDTO);
        return new ResponseEntity<>(voter,HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{employeeId}/voter")
    public ResponseEntity<VoterDTO>getVoter(@PathVariable String employeeId){
        VoterDTO voterDto=voterIdServiceImpl.getVoterByEmployee(employeeId);
        return new ResponseEntity<>(voterDto,HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{employeeId}/voter")
    public ResponseEntity<VoterDetails>updateVoter(@PathVariable String employeeId,
                                                   @RequestPart(value="voterImage",required=false) MultipartFile voterImage,
                                                   @Valid  @RequestPart VoterDTO voterDTO) throws IOException {
        VoterDetails voter=voterIdServiceImpl.updateVoter(employeeId,voterImage,voterDTO);
        return new ResponseEntity<>(voter,HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{employeeId}/voter")
    public ResponseEntity<VoterDetails>deleteVoter(@PathVariable String employeeId){
        return new ResponseEntity<>(voterIdServiceImpl.deleteByEmployeeId(employeeId),HttpStatus.OK);
    }



    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PostMapping("/{employeeId}/previousExperience")
    public ResponseEntity<WorkExperienceDetails>createExperience(@PathVariable String employeeId,
                                                                 @Valid @RequestBody WorkExperienceDTO workExperienceDTO) {
        WorkExperienceDetails experienceDetails=  workExperienceServiceImpl.createExperenceByEmployeId(employeeId,workExperienceDTO);
        return new ResponseEntity<>(experienceDetails,HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{employeeId}/previousExperience/{id}")
    public ResponseEntity<WorkExperienceDetails>updateExperience(@PathVariable String employeeId,
                                                                 @Valid  @RequestBody WorkExperienceDTO workExperienceDTO,
                                                                 @PathVariable String id)
    {
        WorkExperienceDetails workExperienceDetails1=workExperienceServiceImpl.updateExperience(employeeId,workExperienceDTO,id);
        return new ResponseEntity<>(workExperienceDetails1,HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{employeeId}/previousExperience")
    public ResponseEntity<List<WorkExperienceDTO>> getExperience(@PathVariable String employeeId){
        List<WorkExperienceDTO>experienceDetails=workExperienceServiceImpl.getExperience(employeeId);
        return new ResponseEntity<>(experienceDetails,HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{employeeId}/previousExperience/{id}")
    public ResponseEntity<WorkExperienceDetails>deleteExperience(@PathVariable String employeeId,
                                                                 @PathVariable String id){
        return new ResponseEntity<>(workExperienceServiceImpl.deleteExperienceById(employeeId,id),HttpStatus.OK);
    }



    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PostMapping("/{employeeId}/degreeDetails")
    public ResponseEntity<DegreeCertificates>addDegree(@PathVariable String employeeId,
                                                       @RequestPart("addFiles")MultipartFile addFiles,
                                                       @Valid @RequestPart(value="degree") DegreeCertificates degreeCertificates ) throws IOException {
        DegreeCertificates degree=degreeServiceImpl.addDegree(employeeId,addFiles,degreeCertificates);
        return new ResponseEntity<>(degree,HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{employeeId}/degreeDetails")
    public ResponseEntity<List<DegreeDTO>>getDetails(@PathVariable String  employeeId){
        List<DegreeDTO>degreeDTOS=degreeServiceImpl.getDegree(employeeId);
        return new ResponseEntity<>(degreeDTOS,HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{employeeId}/degreeDetails/{id}")
    public ResponseEntity<DegreeCertificates>updateDegree(@PathVariable String employeeId,
                                                          @RequestPart("addFiles")MultipartFile addFiles,
                                                          @PathVariable String id,
                                                          @Valid @RequestPart(value="degree") DegreeCertificates degreeCertificates) throws IOException, UnauthorizedAccessException {
        DegreeCertificates degree =degreeServiceImpl.updateDegree(employeeId,addFiles,id,degreeCertificates);
        return new ResponseEntity<>(degree,HttpStatus.OK);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{employeeId}/degreeDetails/{id}")
    public ResponseEntity<DegreeCertificates>deleteDegree(@PathVariable String employeeId,
                                                          @PathVariable String id) {
        return new ResponseEntity<>(degreeServiceImpl.deleteById(employeeId,id),HttpStatus.OK);

    }
    @GetMapping("/{employeeId}/degreeDetails/{id}")
    public ResponseEntity<DegreeDTO>getById(@PathVariable String employeeId,
                                            @PathVariable String id){
        DegreeDTO degreeDTO=degreeServiceImpl.getById(employeeId,id);
        return new ResponseEntity<>(degreeDTO,HttpStatus.OK);
    }

}