package com.devision.subscription.service; // Service package for subscription domain

import com.devision.subscription.dto.SearchProfileRequest; // Request DTO
import com.devision.subscription.dto.SearchProfileResponse; // Response DTO
import com.devision.subscription.enums.EmploymentStatus; // Employment status enum
import com.devision.subscription.enums.PlanType; // Plan type enum
import com.devision.subscription.model.SearchProfile; // Search profile entity
import com.devision.subscription.repository.SearchProfileRepository; // Search profile repository
import com.devision.subscription.repository.SubscriptionRepository; // Subscription repository
import org.springframework.http.HttpStatus; // HTTP status codes
import org.springframework.stereotype.Service; // Service stereotype
import org.springframework.web.server.ResponseStatusException; // Exception for HTTP errors

import java.math.BigDecimal; // Decimal numbers for salary
import java.util.*; // Collections and utilities
import java.util.stream.Collectors; // Stream collectors

@Service // Marks class as Spring service
public class SearchProfileServiceImpl implements SearchProfileService { // Implementation of search profile service

        private final SearchProfileRepository searchProfileRepository; // Search profile persistence
        private final SubscriptionRepository subscriptionRepository; // Subscription lookup for premium check

        private final com.devision.subscription.kafka.ApplicantProfileUpdateProducer profileUpdateProducer; // Kafka
                                                                                                            // publisher
                                                                                                            // for
                                                                                                            // salary
                                                                                                            // updates

        public SearchProfileServiceImpl(SearchProfileRepository searchProfileRepository, // Constructor injection
                        SubscriptionRepository subscriptionRepository, // Subscription repo for plan validation
                        com.devision.subscription.kafka.ApplicantProfileUpdateProducer profileUpdateProducer) { // Kafka
                                                                                                                // producer
                this.searchProfileRepository = searchProfileRepository; // Assign search profile repo
                this.subscriptionRepository = subscriptionRepository; // Assign subscription repo
                this.profileUpdateProducer = profileUpdateProducer; // Assign producer
        }

        @Override // Implements interface method
        public SearchProfileResponse upsert(String applicantId, SearchProfileRequest request) { // Create/update profile
                // Require PREMIUM active subscription
                subscriptionRepository.findByApplicantIdAndIsActiveTrue(applicantId) // Find any active subscription
                                .filter(sub -> sub.getPlanType() == PlanType.PREMIUM) // Ensure premium
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, // Throw 403 if not
                                                                                                     // premium
                                                "Premium plan required")); // Error message

                // Normalize tags
                List<String> tags = Optional.ofNullable(request.technicalTags) // Handle null list
                                .orElseGet(ArrayList::new) // Default to empty list
                                .stream() // Convert to stream
                                .map(s -> s == null ? null : s.trim()) // Trim each tag
                                .filter(s -> s != null && !s.isEmpty()) // Remove null/empty
                                .distinct() // Remove duplicates
                                .collect(Collectors.toList()); // Collect as list

                // Parse employment statuses from strings to enum
                Set<EmploymentStatus> statuses = Optional.ofNullable(request.employmentStatuses) // Handle null list
                                .orElseGet(ArrayList::new) // Default empty
                                .stream() // Stream items
                                .map(s -> s == null ? null : s.trim().toUpperCase(Locale.ROOT)) // Normalize to enum
                                                                                                // format
                                .filter(Objects::nonNull) // Drop nulls
                                .map(s -> { // Map to enum
                                        try { // Try parse enum
                                                return EmploymentStatus.valueOf(s.replace('-', '_')); // Convert dash to
                                                                                                      // underscore
                                        } catch (IllegalArgumentException ex) { // Handle invalid values
                                                return null; // Skip invalid
                                        }
                                })
                                .filter(Objects::nonNull) // Remove invalid values
                                .collect(Collectors.toCollection(() -> EnumSet.noneOf(EmploymentStatus.class))); // Build
                                                                                                                 // enum
                                                                                                                 // set

                // If neither FULL_TIME nor PART_TIME specified, include both
                if (!statuses.contains(EmploymentStatus.FULL_TIME) && !statuses.contains(EmploymentStatus.PART_TIME)) { // Check
                                                                                                                        // default
                                                                                                                        // requirement
                        statuses.add(EmploymentStatus.FULL_TIME); // Add default FULL_TIME
                        statuses.add(EmploymentStatus.PART_TIME); // Add default PART_TIME
                }

                // Salary defaults
                BigDecimal minSalary = request.minSalary == null ? BigDecimal.ZERO
                                : request.minSalary.max(BigDecimal.ZERO); // Clamp min to >= 0
                BigDecimal maxSalary = request.maxSalary; // null means no upper limit

                // Parse job titles from semicolon-separated string
                List<String> titles = Optional.ofNullable(request.jobTitles) // Handle null
                                .map(str -> Arrays.stream(str.split(";")) // Split by semicolon
                                                .map(String::trim) // Trim values
                                                .filter(s -> !s.isEmpty()) // Drop empty
                                                .distinct() // Deduplicate
                                                .collect(Collectors.toList())) // Collect list
                                .orElseGet(ArrayList::new); // Default empty list

                // Upsert existing profile
                Optional<SearchProfile> existingOpt = searchProfileRepository.findByApplicantId(applicantId); // Lookup
                                                                                                              // by
                                                                                                              // applicant
                SearchProfile profile = existingOpt.orElseGet(SearchProfile::new); // Use existing or new

                profile.setApplicantId(applicantId); // Set applicant id
                profile.setTechnicalTags(tags); // Set normalized tags
                profile.setEmploymentStatuses(new ArrayList<>(statuses)); // Set statuses list
                profile.setCountry(request.country); // Set country
                profile.setMinSalary(minSalary); // Set min salary
                profile.setMaxSalary(maxSalary); // Set max salary
                profile.setDesiredJobTitles(titles); // Set desired titles

                BigDecimal previousMin = existingOpt.map(SearchProfile::getMinSalary).orElse(null); // Previous min
                                                                                                    // salary
                BigDecimal previousMax = existingOpt.map(SearchProfile::getMaxSalary).orElse(null); // Previous max
                                                                                                    // salary

                boolean salaryChanged = !Objects.equals(previousMin, profile.getMinSalary()) // Compare min salary
                                || !Objects.equals(previousMax, profile.getMaxSalary()); // Compare max salary

                searchProfileRepository.save(profile); // Persist profile

                // Publish Kafka event when salary range changes
                if (salaryChanged) { // Check change
                        profileUpdateProducer.publishSalaryUpdate(applicantId, profile.getMinSalary(), // Send min
                                        profile.getMaxSalary()); // Send max
                }

                return new SearchProfileResponse( // Build response
                                applicantId, // Applicant id
                                profile.getTechnicalTags(), // Tags
                                profile.getEmploymentStatuses(), // Statuses
                                profile.getCountry(), // Country
                                profile.getMinSalary(), // Min salary
                                profile.getMaxSalary(), // Max salary
                                profile.getDesiredJobTitles()); // Titles
        }

        @Override // Implements interface method
        public SearchProfileResponse get(String applicantId) { // Fetch profile
                return searchProfileRepository.findByApplicantId(applicantId) // Look up profile
                                .map(p -> new SearchProfileResponse( // Map entity to response
                                                applicantId, // Applicant id
                                                p.getTechnicalTags(), // Tags
                                                p.getEmploymentStatuses(), // Statuses
                                                p.getCountry(), // Country
                                                p.getMinSalary(), // Min salary
                                                p.getMaxSalary(), // Max salary
                                                p.getDesiredJobTitles())) // Titles
                                .orElseGet(() -> new SearchProfileResponse( // Default if none
                                                applicantId, // Applicant id
                                                Collections.emptyList(), // Empty tags
                                                Collections.emptyList(), // Empty statuses
                                                null, // No country
                                                null, // No min salary
                                                null, // No max salary
                                                Collections.emptyList())); // Empty titles
        }
}
