package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourierDto {
    private String login;
    private String password;
    private String firstName;

    public CourierDto(){
    }

    public CourierDto(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public CourierDto(String login) {
        this.login = login;
    }
}
