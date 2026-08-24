package dev.devportfolio.experience.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.devportfolio.shared.domain.DomainValidationException;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExperienceTest {

    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Test
    void currentExperienceCannotHaveEndDate() {
        assertThatThrownBy(() -> new Experience(PORTFOLIO_ID, "Acme", "Dev", null, LocalDate.of(2020, 1, 1),
                LocalDate.of(2021, 1, 1), true, null, 0, Set.of())).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void endDateCannotBeBeforeStartDate() {
        assertThatThrownBy(() -> new Experience(PORTFOLIO_ID, "Acme", "Dev", null, LocalDate.of(2021, 1, 1),
                LocalDate.of(2020, 1, 1), false, null, 0, Set.of())).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void acceptsValidCurrentExperienceWithoutEndDate() {
        Experience experience = new Experience(PORTFOLIO_ID, "Acme", "Dev", null, LocalDate.of(2020, 1, 1), null,
                true, null, 0, Set.of());

        assertThat(experience.isCurrent()).isTrue();
        assertThat(experience.getEndDate()).isNull();
    }
}
