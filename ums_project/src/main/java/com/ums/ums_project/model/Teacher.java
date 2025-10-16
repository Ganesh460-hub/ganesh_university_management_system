package com.ums.ums_project.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @Column(unique = true, nullable = false)
    private String employeeId;

    private String name;
    private String email;
    private String department;

    // Professional details
    private String highestQualification; // PhD, M.Tech, M.Sc, etc.
    private String position; // Junior Professor, Associate Professor, Senior Professor, HOD, etc.
    private String subject; // Mathematics, Physics, Chemistry, etc.
    private String phoneNumber;

    // Personal details
    private String caste;
    private String bloodGroup;

    // Address details
    private String doorNo;
    private String street;
    private String city;
    private String district;
    private String state;
    private String pincode;

    private String photo = "default-profile.png";

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Attendance> attendances;

    public Teacher() {}

    public Teacher(String employeeId, String name, String email, String department) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    // Getters & Setters for all fields
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getHighestQualification() { return highestQualification; }
    public void setHighestQualification(String highestQualification) { this.highestQualification = highestQualification; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCaste() { return caste; }
    public void setCaste(String caste) { this.caste = caste; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getDoorNo() { return doorNo; }
    public void setDoorNo(String doorNo) { this.doorNo = doorNo; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }

    // Helper method to get complete address
    public String getCompleteAddress() {
        StringBuilder address = new StringBuilder();
        if (doorNo != null && !doorNo.isEmpty()) address.append(doorNo).append(", ");
        if (street != null && !street.isEmpty()) address.append(street).append(", ");
        if (city != null && !city.isEmpty()) address.append(city).append(", ");
        if (district != null && !district.isEmpty()) address.append(district).append(", ");
        if (state != null && !state.isEmpty()) address.append(state);
        if (pincode != null && !pincode.isEmpty()) address.append(" - ").append(pincode);
        return address.toString();
    }

    // Helper method to get full professional title
    public String getProfessionalTitle() {
        return position + " - " + department;
    }
}