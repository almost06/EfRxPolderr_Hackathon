package com.example.demo.service;

import com.example.demo.entity.Donor;
import com.example.demo.entity.Project;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {

    public double calculateMatch(Donor donor, Project project, double baseScore) {
        if (donor == null || project == null) {
            return baseScore;
        }

        List<String> skillsNeededTags = project.getSkillsNeededTags();
        List<String> volunteerSkills = donor.getVolunteerSkills();

        if (isNotEmpty(skillsNeededTags) && isNotEmpty(volunteerSkills)) {
            Set<String> donorSkills = new HashSet<>(volunteerSkills);
            long matchingSkills = skillsNeededTags.stream()
                    .filter(donorSkills::contains)
                    .distinct()
                    .count();

            return baseScore + (matchingSkills * 25.0);
        }

        return baseScore;
    }

    private boolean isNotEmpty(List<String> values) {
        return values != null && !values.isEmpty();
    }
}
