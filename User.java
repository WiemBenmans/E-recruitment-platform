package com.enit.Erecruitement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    @Id
    private ObjectId id;
    private String name;
    private String surname;
    private String email;
    private String password;

}
