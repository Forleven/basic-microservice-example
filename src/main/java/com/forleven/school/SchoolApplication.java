package com.forleven.school;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.forleven.common.EnableForlevenCommon;
import com.forleven.school.model.School;
import com.forleven.school.repository.SchoolRepository;

@SpringBootApplication
@EnableForlevenCommon
public class SchoolApplication implements CommandLineRunner {

	@Autowired
	private SchoolRepository schoolRepository;

	public static void main(String[] args) {
		SpringApplication.run(SchoolApplication.class, args).setId("school-service");
	}

	@Override
	public void run(String... args) throws Exception {
		List<School> initialSchoolsToSample = IntStream.range(1, 42)
				.mapToObj(i -> School.builder().name(i + " school").build())
				.collect(Collectors.toList());

		schoolRepository.saveAll(initialSchoolsToSample);
	}
}
