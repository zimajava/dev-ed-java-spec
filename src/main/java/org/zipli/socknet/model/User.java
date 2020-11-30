package org.zipli.socknet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Stream;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer idUser;

    private String email;

    private String userName;

    private String pass;

}
