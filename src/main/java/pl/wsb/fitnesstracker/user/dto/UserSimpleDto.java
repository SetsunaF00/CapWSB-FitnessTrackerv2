package pl.wsb.fitnesstracker.user.dto;

public class UserSimpleDto {

    private String firstName;
    private String lastName;

    public UserSimpleDto() {
        // wymagany pusty konstruktor (np. dla Jacksona)
    }

    public UserSimpleDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // gettery i settery
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
