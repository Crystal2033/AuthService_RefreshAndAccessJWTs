/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
public class AppError {
    private int status;
    private String message;
    private Date timestamp;

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }


}
