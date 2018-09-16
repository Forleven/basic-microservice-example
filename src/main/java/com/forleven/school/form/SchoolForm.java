package com.forleven.school.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import io.swagger.annotations.ApiModelProperty;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Wither
@Data
public class SchoolForm {

    @NotNull
    @NotEmpty
    @ApiModelProperty(position = 1, example = "my pretty school")
    private String name;
}
