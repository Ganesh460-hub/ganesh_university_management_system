package com.ums.ums_project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(unique = true, nullable = false)
    private String collegeId;

    private String name;
    private String email;
    private String department;

    // Personal details
    private String bloodGroup;
    private String caste;
    private String scholarshipType; // Management, Scholarship, etc.

    // Address details
    private String doorNo;
    private String street;
    private String village;
    private String mandal;
    private String district;
    private String state;
    private String pincode;

    // Parent/Guardian details
    private String parentOrGuardianName;
    private String parentOrGuardianPhone;

    private Integer currentSemester = 1;

    private String photo = "default-profile.png";

    public Student() {}

    // Getters and setters for all fields
    public String getCollegeId() { return collegeId; }
    public void setCollegeId(String collegeId) { this.collegeId = collegeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getCaste() { return caste; }
    public void setCaste(String caste) { this.caste = caste; }

    public String getScholarshipType() { return scholarshipType; }
    public void setScholarshipType(String scholarshipType) { this.scholarshipType = scholarshipType; }

    public String getDoorNo() { return doorNo; }
    public void setDoorNo(String doorNo) { this.doorNo = doorNo; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getMandal() { return mandal; }
    public void setMandal(String mandal) { this.mandal = mandal; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getParentOrGuardianName() { return parentOrGuardianName; }
    public void setParentOrGuardianName(String parentOrGuardianName) { this.parentOrGuardianName = parentOrGuardianName; }

    public String getParentOrGuardianPhone() { return parentOrGuardianPhone; }
    public void setParentOrGuardianPhone(String parentOrGuardianPhone) { this.parentOrGuardianPhone = parentOrGuardianPhone; }

    public Integer getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(Integer currentSemester) { this.currentSemester = currentSemester; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    // Helper method to get complete address
    public String getCompleteAddress() {
        StringBuilder address = new StringBuilder();
        if (doorNo != null && !doorNo.isEmpty()) address.append(doorNo).append(", ");
        if (street != null && !street.isEmpty()) address.append(street).append(", ");
        if (village != null && !village.isEmpty()) address.append(village).append(", ");
        if (mandal != null && !mandal.isEmpty()) address.append(mandal).append(", ");
        if (district != null && !district.isEmpty()) address.append(district).append(", ");
        if (state != null && !state.isEmpty()) address.append(state);
        if (pincode != null && !pincode.isEmpty()) address.append(" - ").append(pincode);
        return address.toString();
    }
}