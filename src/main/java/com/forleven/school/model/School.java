package com.forleven.school.model;

import javax.persistence.*;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.forleven.common.domain.LogFields;

@Builder
@Wither
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "school")
@Entity
@JsonPropertyOrder({
        "id_school",
        "name"
})
public class School extends LogFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_school")
    @JsonProperty("id_school")
    @ApiModelProperty(position = 1, example = "1")
    private Long id;

    @ApiModelProperty(position = 2, example = "high school")
    private String name;
}
